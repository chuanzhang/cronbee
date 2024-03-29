package org.cronbee.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;



import org.apache.logging.log4j.util.Strings;
import org.cronbee.config.CronConfig;
import org.cronbee.constant.CronConstant;
import org.cronbee.dao.CronDao;
import org.cronbee.dao.CronExecuteHistoryDao;
import org.cronbee.entry.GeneralRunableImpl;
import org.cronbee.model.Cron;
import org.cronbee.util.CronUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CronService {
    @Autowired
    CronDao cronDao;
    
    @Autowired
    CronExecuteHistoryDao cronExecuteHistoryDao;
    
    private static final ConcurrentHashMap<String, Cron> cronMap = new ConcurrentHashMap<String, Cron>();
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<String, ScheduledFuture<?>>();
    
    @Autowired
    private CronConfig cronConfig;
    
    /**
     * 获取所有cron
     * @param cronGroup
     * @return
     */
    public List<Cron> getAllCron() {
    	
    	return cronDao.getAllCron(cronConfig.getGroup());
    }
    
    /**
     * 将cron对象放到map里，后续可以直接找到
     * @param cron
     */
    public void addCronToMap(Cron cron) {
    	if(cron == null) return;
    	cronMap.put(CronUtil.getTaskNamebyCron(cron), cron);
    }
    
    public boolean cronExsists(Cron cron) {
    	String taskName = CronUtil.getTaskNamebyCron(cron);
    	return cronMap.containsKey(taskName);
    }
    
    /**
     * 将future加到map中
     * @param beanName
     * @param future
     */
    public void addFutureToMap(String taskName, ScheduledFuture<?> future) {
    	if(CronUtil.isEmpty(taskName) || future == null) return;
    	taskMap.put(taskName, future);
    }
    
    /**
     * 
     * @param beanName
     * @return
     */
    public Cron getCronByTaskName(String taskName) {
    	if(Strings.isBlank(taskName))return null;
        return cronMap.get(taskName);
    }
    
    /**
     * 
     * @param beanName
     * @return
     */
    public ScheduledFuture<?> getFutureByTaskName(String taskName) {
    	if(Strings.isBlank(taskName)) return null;
    	return taskMap.get(taskName);
    }
    
    
    /**
     * id生成器，由四部分组成：当前秒数、cron_id
     * @param cron_id
     * @return
     */
    private String genericId(String cron_id){
        int maxCronIdLen = 10;
        if(cron_id.length() > maxCronIdLen) {
            cron_id = cron_id.substring(cron_id.length() - maxCronIdLen);
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        
        return sdf.format(date) + '-' + cron_id;
    }
    
    
    /**
     * 插入执行记录，并返回记录行id
     * @param cron
     * @return
     */
    public String insertCronExecuteHistory(Cron cron) {
    	if(cron == null) return null;
    	String id = genericId(cron.getId());
    	int ret = 0;
    	//非单点运行方式
        if(!CronConstant.RUN_WAY_SINGLE.equals(cron.getRunWay())) {
        	id = id + '-' + UUID.randomUUID().toString().replace("-", "");
        }
        
        ret = cronExecuteHistoryDao.insertCronExecuteHistory(id, cron);
        
        if(ret > 0) {
        	return id;
        }else {
        	return null;
        }
    }
    
    /**
     * 更新执行记录
     * @param cronId
     * @param executeId
     * @param status
     * @return
     */
    public int updateCronExecuteHistory(String cronId,String executeId, String status, String message) {
    	if(cronId == null || executeId == null || status == null) return 0;
    	cronDao.updateLastRunTime(cronId);
    	return cronExecuteHistoryDao.updateCronExecuteHistory(executeId, status, message);
    }
    
    /**
     * 终止任务
     * @param beanName
     * @return
     */
    public boolean stopCron( String taskName) {
    	
    	ScheduledFuture<?> future = getFutureByTaskName(taskName);
    	if(future == null) return false;
    	return future.cancel(true);
    }
    
    /**
     * 直接执行任务
     * @param beanName
     */
    public void runJob(String taskName) {
    	Cron cron = getCronByTaskName(taskName);
    	if(cron != null) {
    		Runnable bee = new GeneralRunableImpl(cron);
    		bee.run();
    	}
    }
}
