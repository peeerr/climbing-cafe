package com.peeerr.climbing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ClimbingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClimbingApplication.class, args);
	}

}
