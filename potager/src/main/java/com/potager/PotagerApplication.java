package com.potager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PotagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PotagerApplication.class, args);
	}

}
