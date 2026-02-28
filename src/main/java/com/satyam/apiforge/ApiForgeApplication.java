package com.satyam.apiforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiForgeApplication.class, args);
    }

}
