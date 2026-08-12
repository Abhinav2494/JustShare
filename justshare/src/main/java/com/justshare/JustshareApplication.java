package com.justshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class JustshareApplication {

	public static void main(String[] args) {

//		SpringApplication.run(JustshareApplication.class, args);
		System.out.println("DB_USERNAME = " + System.getenv("DB_USERNAME"));
		System.out.println("TEST_ENV = " + System.getenv("TEST_ENV"));

	}

}
