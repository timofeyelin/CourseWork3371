package com.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hotel")
public class HotelServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelServerApplication.class, args);
    }
}