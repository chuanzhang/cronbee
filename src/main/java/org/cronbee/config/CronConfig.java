package org.cronbee.config;

import javax.sql.DataSource;

import org.cronbee.util.CronUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cronbee")
public class CronConfig {
	private String group;
	private String maxThreadPoolSize;
	private String dataSourceBeanName;
	
	private DataSource dataSource;
	
	private final int DEFAULT_MAX_POOLL_SIZE = 30; //线程池默认值
	private final String DEFAULT_DATASOURCE_NAME = "dataSource";
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}
	public void setMaxThreadPoolSize(String maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}
	
	public String getDataSourceName() {
		if(CronUtil.isEmpty(dataSourceBeanName))dataSourceBeanName = DEFAULT_DATASOURCE_NAME;
		
		return dataSourceBeanName;
	}

	public void setDataSourceName(String dataSourceBeanName) {
		this.dataSourceBeanName = dataSourceBeanName;
	}
	

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * 返回int类型的最大线程个数配置
	 * @return
	 */
	public int getIntMaxThreadPoolSize() {
		if(CronUtil.isEmpty(this.maxThreadPoolSize)) return this.DEFAULT_MAX_POOLL_SIZE;
		
		try {
			return Integer.parseInt(this.maxThreadPoolSize);
		}catch (Exception ex) {
			return this.DEFAULT_MAX_POOLL_SIZE;
		}
	}
	
}
