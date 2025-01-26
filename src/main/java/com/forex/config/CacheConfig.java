//package com.forex.config;
//
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.github.benmanes.caffeine.cache.Caffeine;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//
//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchangeRates");
//        cacheManager.setCaffeine(
//                Caffeine.newBuilder()
//                        .expireAfterWrite(10, TimeUnit.MINUTES)
////                        .maximumSize(100)  // Maximum size for cache (optional)
//        );
//        return cacheManager;
//    }
//}
//
