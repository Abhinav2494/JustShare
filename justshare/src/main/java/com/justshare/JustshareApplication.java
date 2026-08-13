package com.justshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class JustshareApplication {

	public static void main(String[] args) {

		SpringApplication.run(JustshareApplication.class, args);

	}

}
