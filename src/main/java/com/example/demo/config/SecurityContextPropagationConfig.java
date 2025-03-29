package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityContextPropagationConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityContextPropagationConfig.class);
    
    @PostConstruct
    public void init() {
        // 設置 SecurityContextHolder 策略為可繼承的線程本地模式
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        logger.info("SecurityContextHolder 策略設置為: {}", SecurityContextHolder.getContextHolderStrategy().getClass().getName());
    }
}