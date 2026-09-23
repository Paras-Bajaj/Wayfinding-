// src/main/java/com/lpu/wayfinding/config/CacheConfig.java
package com.lpu.wayfinding.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Caffeine configured directly in RouteCache
}