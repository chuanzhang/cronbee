package org.cronbee.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private final static String DATASOURCE_KEY = "dataSource";

    @Primary
    @Bean(DATASOURCE_KEY)
    @Qualifier(DATASOURCE_KEY)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(DataSourceProperties properties) {
    	return DataSourceBuilder.create(properties.getClassLoader())
				.type(HikariDataSource.class)
				.driverClassName(properties.determineDriverClassName())
				.url(properties.determineUrl())
				.username(properties.determineUsername())
				.password(properties.determinePassword())
				.build();

    }
}
