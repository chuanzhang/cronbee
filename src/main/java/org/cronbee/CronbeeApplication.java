package org.cronbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CronbeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CronbeeApplication.class, args);
	}

}
