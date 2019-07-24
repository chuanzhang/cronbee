package org.cronbee.controller;

import org.cronbee.service.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CronController {
	@Autowired
	CronService cronService;
	
	
	@RequestMapping("/cron/{bean}/stop")
	public void stopCron(@PathVariable(name = "bean", required =true) String bean) {
		cronService.stopCron(bean);
	}
}
