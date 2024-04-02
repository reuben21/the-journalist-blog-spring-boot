package com.reuben.thejournalistserviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TheJournalistServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheJournalistServiceRegistryApplication.class, args);
	}

}
