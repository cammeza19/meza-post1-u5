package com.universidad.reservaslabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.universidad.reservaslabs")
public class ReservasLabsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasLabsApiApplication.class, args);
    }
}