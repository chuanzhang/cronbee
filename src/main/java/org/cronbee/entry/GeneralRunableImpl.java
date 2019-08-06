package org.cronbee.entry;

import java.lang.reflect.Method;
import java.net.InetAddress;

import org.cronbee.constant.CronConstant;
import org.cronbee.model.Cron;
import org.cronbee.service.CronService;
import org.cronbee.util.ApplicationContextUtil;
import org.cronbee.util.CronUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneralRunableImpl implements Runnable{
	
	private String taskName;
	
	public static CronService cronService;
	
	private Cron cron;
	
	private boolean isRunning;
	
	private String currentExecuteId;
	
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralRunableImpl.class);
	
	public GeneralRunableImpl(Cron cron) {
		if(cron == null) return;
		this.cron = cron;
		this.taskName = CronUtil.getTaskNamebyCron(cron);
	}
	
	
	private void beforeRun() {
        
        if(this.cron == null) {
            logger.error("cron not exist,  taskName:{}", taskName);
            return;
        }
        
        //同一个cron不允许在同一个机器上同时执行（往往是因为执行时间长导致重叠），以尽可能避免并发问题。
        //如果确实有这种需求，请多写几个入口方法或者设置参数或者其他方式。
        synchronized (this) {
			if(isRunning)return;
			
			isRunning = true;
		}
        
        //任务被关闭
        if(CronConstant.RUN_WAY_NONE.equals(cron.getRunWay())) {
            logger.info("task:{} is configured not to run,return",taskName );
            return;
        }
        
        //判断是否配置期望运行的ip，如果不是就稍等一会
        if(!CronUtil.isEmpty(cron.getPreferIp()) && !cron.getPreferIp().equals(CronUtil.getLocalIp())) {
        	try {
        		Thread.sleep(CronConstant.WAIT_TIME_IN_MILLS);
        	}
        	catch(Exception ex) {}
        }
        
        //插入执行记录
        try {
            //插入数据库表行
        	this.currentExecuteId = cronService.insertCronExecuteHistory(cron);
            
        }catch(Exception ex) {
        	if(CronConstant.RUN_WAY_SINGLE.equals(cron.getRunWay())) {
        		logger.info("task:{} run in single mode, insert run history error,maybe some other point is runing this task,current thread will abort",
        				taskName);
        		return;
        	}else {
	            logger.error("task:{} insert run history error:{}",ex);
        	}
        }
        
	}
	
	@Override
	public void run() {
		String runStatus = CronConstant.CronExecuteHistoryStatus.INIT.getCronExecuteHistoryStatus();
		String message = CronConstant.MESSAGE_OK;
		beforeRun();
		
		logger.info("begin run task:{}",this.taskName);
		
		try {
			Object bean = ApplicationContextUtil.getApplicationContext().getBean(this.cron.getBeanName());
			
			Class clazz = bean.getClass();
			Method method = clazz.getDeclaredMethod(this.cron.getMethodName());
			
			if(method.getParameterCount() == 0)method.invoke(bean);
			else method.invoke(bean, this.cron.getParameter());
			
			runStatus = CronConstant.CronExecuteHistoryStatus.FINISH.getCronExecuteHistoryStatus();
		}catch(Exception ex) {
			
			//赋值失败信息
			runStatus = CronConstant.CronExecuteHistoryStatus.FAIL.getCronExecuteHistoryStatus();
			logger.error("task:{} run error",this.taskName, ex);
			
			message = ex.toString();
			if(message.length() > CronConstant.MAX_MESSAGE_LENGTH) {
				message.substring(0, CronConstant.MAX_MESSAGE_LENGTH);
			}
		}finally {
			postRun(runStatus, message);
		}
		
	}
	
	/**
	 * 任务执行后的动作
	 */
	private void postRun(String status, String message) {
		
		//更新执行状态
        try {
            cronService.updateCronExecuteHistory(cron.getId(), this.currentExecuteId, status, message);
            logger.info("task:{} done.", this.taskName);
            
        }catch(Exception ex) {
            logger.error("task:{} insert run history error",this.taskName,ex);
        }
        
        isRunning = false;
	}
}
