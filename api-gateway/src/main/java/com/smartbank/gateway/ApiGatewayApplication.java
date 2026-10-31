package com.smartbank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// This is the single entry point for the whole system.
// Instead of clients calling account-service and transaction-service
// directly on different ports, everything comes through here first —
// this is exactly the "API Gateway" concept from your interview guide.
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
