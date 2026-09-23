// src/main/java/com/lpu/wayfinding/cache/RouteCache.java
package com.lpu.wayfinding.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lpu.wayfinding.dto.response.RouteResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * LRU + TTL cache for frequent route queries.
 * Caffeine provides thread-safe concurrent LRU semantics.
 */
@Component
public class RouteCache {

    private final Cache<String, RouteResponse> cache;

    public RouteCache() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .recordStats()
                .build();
    }

    public String key(Long startId, Long endId, boolean wheelchair, int hourBucket) {
        return String.format("%d:%d:%b:%d", startId, endId, wheelchair, hourBucket);
    }

    public RouteResponse get(String key) {
        return cache.getIfPresent(key);
    }

    public void put(String key, RouteResponse value) {
        cache.put(key, Objects.requireNonNull(value));
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public Cache<String, RouteResponse> raw() {
        return cache;
    }
}