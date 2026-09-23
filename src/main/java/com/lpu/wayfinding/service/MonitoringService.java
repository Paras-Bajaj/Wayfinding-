// src/main/java/com/lpu/wayfinding/service/MonitoringService.java
package com.lpu.wayfinding.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final MeterRegistry registry;
    private Counter cacheHits;
    private Counter cacheMisses;
    private Counter routeFailures;
    private Counter routeSuccesses;
    private Timer routeTimer;

    @PostConstruct
    public void init() {
        cacheHits = Counter.builder("wayfinding.cache.hits").register(registry);
        cacheMisses = Counter.builder("wayfinding.cache.misses").register(registry);
        routeFailures = Counter.builder("wayfinding.route.failures").register(registry);
        routeSuccesses = Counter.builder("wayfinding.route.successes").register(registry);
        routeTimer = Timer.builder("wayfinding.route.duration").register(registry);
    }

    public void recordCacheHit() { cacheHits.increment(); }
    public void recordCacheMiss() { cacheMisses.increment(); }
    public void recordRouteFailure() { routeFailures.increment(); }
    public void recordRouteSuccess(long ms) {
        routeSuccesses.increment();
        routeTimer.record(ms, TimeUnit.MILLISECONDS);
    }
}