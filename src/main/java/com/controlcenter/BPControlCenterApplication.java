package com.controlcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.controlcenter")
@EnableJpaRepositories(basePackages = "com.controlcenter")
public class BPControlCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BPControlCenterApplication.class, args);
	}

}