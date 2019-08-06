package org.cronbee.entry;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;

import org.cronbee.model.Cron;
import org.cronbee.service.CronService;
import org.cronbee.util.CronUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;



@Component
public class TaskEntry {
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    
    @Autowired
    private CronService cronService;
    
    
    
    private static boolean bInit = false;
    
    private static final Logger logger = LoggerFactory.getLogger(TaskEntry.class);
    
    /**
     * 初始化，容器启动后执行
     */
    public synchronized void init() {
        
        //防止多次被实例化导致任务重复执行
        if(!bInit) {
            List<Cron> cronList = cronService.getAllCron();
            if(cronList == null || cronList.size() == 0) {
                bInit = true;
                return;
            }
            
            GeneralRunableImpl.cronService = cronService;
            
            for (Cron cron : cronList) {
            	
            	try {
	            	//去除重复cron，只留下靠前的一个
	            	if(cronService.cronExsists(cron)) {
	            		logger.warn("cron:{} 重复加载", cron.toString());
	            		continue;
	            	}
	            	
	                cronService.addCronToMap(cron);
                
                	Runnable bee = new GeneralRunableImpl(cron);

                	ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(bee, new CronTrigger(cron.getCronExpression()));
                	
                    cronService.addFutureToMap(CronUtil.getTaskNamebyCron(cron), future);
                    
                    logger.info("cron:" + cron.toString() + "被加载");
                }catch(Exception ex) {
                    logger.info("cron:" + cron.toString() + "加载异常：" + ex);
                }
            }
            
            bInit = true;
        }
        
    }

    /**
     * 刷新定时任务列表
     */
    public synchronized void refresh() {
    	List<Cron> cronList= cronService.getAllCron();
        if(cronList == null || cronList.size() == 0) {
            return;
        }
        
        for (Cron cron : cronList) {
        	String taskName = CronUtil.getTaskNamebyCron(cron);
        	
        	//完全无变化的cron不用更新
        	if(cronService.cronExsists(cron)) {
        		Cron oldCron = cronService.getCronByTaskName(taskName);
        		if(cron.equals(oldCron)) continue;
        	}
        	
        	logger.info("cron:{}有更新:{}", cron.getId(), cron.toString());
        	
            cronService.addCronToMap(cron);
            try {
            	//旧的任务停止调度，已经执行的不允许中断
            	ScheduledFuture<?> oldFuture = cronService.getFutureByTaskName(taskName);
            	oldFuture.cancel(false);
            	
            	//新的任务开始调度
            	Runnable bee = new GeneralRunableImpl(cron);
            	ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(bee, new CronTrigger(cron.getCronExpression()));
            	
                cronService.addFutureToMap(taskName, future);
                
                logger.info("cron被加载:{}", cron.toString());
            }catch(Exception ex) {
                logger.info("cron:{}\n加载异常：{}", cron.toString(), ex);
            }
        }
    }
    
}
