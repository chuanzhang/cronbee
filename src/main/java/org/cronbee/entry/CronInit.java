package org.cronbee.entry;

import javax.sql.DataSource;

import org.cronbee.config.CronConfig;
import org.cronbee.util.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CronInit implements ApplicationRunner{
	
	@Autowired
	private CronConfig cronConfig;
	
	@Autowired
	private TaskEntry taskEntry;
	
	/**
	 * 容器加载完成时执行
	 */
	@Override
	public void run(ApplicationArguments var1)   throws Exception{
		DataSource dataSource = (DataSource)ApplicationContextUtil.getApplicationContext().getBean(cronConfig.getDataSourceName());
		
		cronConfig.setDataSource(dataSource);
		
		taskEntry.init();
	}
}
