package com.upmudoum.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BackendTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendTradeApplication.class, args);
    }

}
