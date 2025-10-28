package com.pascs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PascsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PascsApplication.class, args);
        System.out.println("🚀 PASCS Backend started successfully!");
        System.out.println("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("📊 Actuator: http://localhost:8080/actuator");
    }
}