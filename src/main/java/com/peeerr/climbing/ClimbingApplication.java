package com.peeerr.climbing;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Generated
@ConfigurationPropertiesScan
@EnableScheduling
@SpringBootApplication
public class ClimbingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbingApplication.class, args);
    }

}
