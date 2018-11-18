package com.ccz.appinall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ccz.appinall.application.AppInAllWsServer;
import com.ccz.appinall.library.util.HashUtil;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
@Slf4j
public class AppinallApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(AppinallApplication.class, args);
			AppInAllWsServer tcs = context.getBean(AppInAllWsServer.class);
			if(tcs.start() == false) {
				log.error("System Shutdown as the Initialize failed");
				System.exit(0);
				return;
			}
				
			tcs.closeSync();
			tcs.stop();
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
