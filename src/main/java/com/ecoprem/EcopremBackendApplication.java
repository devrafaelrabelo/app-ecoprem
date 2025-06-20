package com.ecoprem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ecoprem")
@EnableJpaRepositories(basePackages = "com.ecoprem")
public class EcopremBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcopremBackendApplication.class, args);
	}

}