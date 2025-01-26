package com.forex.config;

import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimiter {

    private static final int MAX_REQUESTS = 10;
    private static final int TIME_WINDOW = 60;

    private AtomicInteger requestCount = new AtomicInteger(0);
    private LocalDateTime lastRequestTime = LocalDateTime.now();

    public boolean isRateLimited() {
        if (lastRequestTime.plusSeconds(TIME_WINDOW).isBefore(LocalDateTime.now())) {
            requestCount.set(0);
            lastRequestTime = LocalDateTime.now();
        }

        return requestCount.incrementAndGet() > MAX_REQUESTS;
    }
}
