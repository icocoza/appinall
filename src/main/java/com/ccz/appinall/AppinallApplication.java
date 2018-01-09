package com.ccz.appinall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.ccz.appinall.application.server.AppInAllWebsocketServer;
import com.ccz.appinall.library.util.HashUtil;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class AppinallApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(AppinallApplication.class, args);
			AppInAllWebsocketServer tcs = context.getBean(AppInAllWebsocketServer.class);
			tcs.start();
			tcs.closeSync();
			tcs.stop();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
