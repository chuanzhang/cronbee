package org.cronbee.entry;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.cronbee.dao.CronDao;
import org.cronbee.model.Cron;
import org.cronbee.service.CronService;
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
    
    @Resource
    private CronDao cronDao;
    
    @Autowired
    private CronService cronService;
    
    
    
    private static boolean bInit = false;
    
    private static final Logger logger = LoggerFactory.getLogger(CronDao.class);
    
    /**
     * 初始化，在实例构造结束后执行
     */
    @PostConstruct
    public synchronized void init() {
        
        //防止多次被实例化导致任务重复执行
        if(!bInit) {
            List<Cron> cronList= cronService.getAllCron();
            if(cronList == null || cronList.size() == 0) {
                bInit = true;
                return;
            }
            
            for (Cron cron : cronList) {
                cronService.addCronToMap(cron);
                try {
                	Runnable runnable = new GeneralRunableImpl(cron.getBeanName(), cron.getMethodName());

                	ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(runnable, new CronTrigger(cron.getCronExpression()));
                	
//                    ScheduledFuture<?> future = threadPoolTaskScheduler.schedule((Runnable)ApplicationContextUtil.getApplicationContext()
//                                .getBean(cron.getBeanName()), new CronTrigger(cron.getCronExpression()));
                    cronService.addFutureToMap(cron.getBeanName(), future);
                    
                    logger.info("cron:" + cron.getBeanName() + "被加载");
                }catch(Exception ex) {
                    logger.info("cron:" + cron.getBeanName() + "加载异常：" + ex.getMessage());
                }
            }
            
            bInit = true;
        }
        
    }

    /**
     * 刷新定时任务列表
     */
    public void refresh() {
        init();
    }
    
}
