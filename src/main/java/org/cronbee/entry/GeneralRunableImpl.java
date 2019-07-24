package org.cronbee.entry;

import java.lang.reflect.Method;

import org.cronbee.constant.CronConstant;
import org.cronbee.model.Cron;
import org.cronbee.service.CronService;
import org.cronbee.util.ApplicationContextUtil;
import org.cronbee.util.CronUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneralRunableImpl implements Runnable{
	
	String beanName;
	String methodName;
	
	String taskName;
	
	@Autowired
	CronService cronService;
	
	Cron cron;
	
	boolean isRunning;
	
	String currentExecuteId;
	
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralRunableImpl.class);
	
	public GeneralRunableImpl(String beanName, String methodName) {
		this.beanName = beanName;
		this.methodName = methodName;
		
		this.taskName = CronUtil.getTaskName(beanName, methodName);
		this.cron = cronService.getCronByTaskName(this.taskName);
	}
	
	
	private void beforeRun() {
        
        if(this.cron == null) {
            logger.error("cron not exist,  taskName:{}", taskName);
            return;
        }
        
        //同一个cron不允许在同一个机器上同时执行，以尽可能避免并发问题。如果确实有这种需求，请多写几个入口方法或者自行启动子线程。
        synchronized (this) {
			if(isRunning)return;
			
			isRunning = true;
		}
        
        //任务被关闭
        if(CronConstant.RUN_WAY_NONE.equals(cron.getRunWay())) {
            logger.info("task:{} is configured not to run,return",taskName );
            return;
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
	            logger.error("task:{} insert run history error:{}",ex.getMessage());
        	}
        }
        
        
        String status = CronConstant.CronExecuteHistoryStatus.FINISH.getCronExecuteHistoryStatus();
        //单点运行方式
        if(CronConstant.RUN_WAY_SINGLE.equals(cron.getRunWay())) {
            
            logger.info("task:{} is configured run on single point",taskName);
        }
        
        
	}
	
	@Override
	public void run() {
		String runStatus = CronConstant.CronExecuteHistoryStatus.INIT.getCronExecuteHistoryStatus();
		
		logger.info("begin run task:{}",this.taskName);
		
		try {
			Object bean = ApplicationContextUtil.getApplicationContext().getBean(beanName);
			
			Class clazz = bean.getClass();
			Method method = clazz.getDeclaredMethod(this.methodName);
			method.invoke(bean);
			runStatus = CronConstant.CronExecuteHistoryStatus.FINISH.getCronExecuteHistoryStatus();
		}catch(Exception ex) {
			runStatus = CronConstant.CronExecuteHistoryStatus.FAIL.getCronExecuteHistoryStatus();
		}finally {
			postRun(runStatus);
		}
		
	}
	
	/**
	 * 任务执行后的动作
	 */
	private void postRun(String status) {
		//更新执行状态
        try {
            cronService.updateCronExecuteHistory(cron.getId(), this.currentExecuteId, status);
            logger.info("task:{} done.", this.taskName);
            
        }catch(Exception ex) {
            logger.error("task:{} insert run history error",this.taskName,ex);
        }
        
        isRunning = false;
	}
}
