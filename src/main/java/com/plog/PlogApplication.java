package com.plog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlogApplication.class, args);
    }

}
