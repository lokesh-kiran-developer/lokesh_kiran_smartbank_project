package com.smartbank.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// This is the "phone book" for our microservices.
// Account Service and Transaction Service will both register
// themselves here on startup, so they can find each other
// by NAME instead of hardcoded host:port.
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
