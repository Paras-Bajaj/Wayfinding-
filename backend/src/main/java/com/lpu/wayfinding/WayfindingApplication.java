// src/main/java/com/lpu/wayfinding/WayfindingApplication.java
package com.lpu.wayfinding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class WayfindingApplication {
    public static void main(String[] args) {
        SpringApplication.run(WayfindingApplication.class, args);
    }
}