package com.ccz.appinall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.ccz.appinall.application.AppInAllWsServer;
import com.ccz.appinall.library.util.HashUtil;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class AppinallApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(AppinallApplication.class, args);
			AppInAllWsServer tcs = context.getBean(AppInAllWsServer.class);
			if(tcs.start() == false) {
				System.out.println("System Shutdown as the Initialize failed");
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
