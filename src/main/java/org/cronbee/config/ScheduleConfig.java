package org.cronbee.config;


import java.util.concurrent.RejectedExecutionHandler;

import org.cronbee.constant.CronConstant;
import org.cronbee.entry.RejectExceptionHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 配置后台任务的线程池
 * @author fanyang
 *
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
	
	@Autowired
	CronConfig cronConfig;
	
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());
    }
    
    
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        
        //设置最大线程个数
        threadPoolTaskScheduler.setPoolSize(cronConfig.getIntMaxThreadPoolSize());
        
        //设置异常处理类
        RejectedExecutionHandler rejectedExecutionHandler = new RejectExceptionHandlerImpl();
        threadPoolTaskScheduler.setRejectedExecutionHandler(rejectedExecutionHandler);
        
        /**需要实例化线程*/
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
    
}