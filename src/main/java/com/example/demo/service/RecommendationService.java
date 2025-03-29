package com.example.demo.service;

import com.example.demo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    public CompletableFuture<RecommendationResult> processRequest(CustomerHardware hardware, ComputeJob job) {
        // 保存當前的 SecurityContext 和認證信息
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("處理推薦請求，當前認證用戶: {}", authentication != null ? authentication.getName() : "unknown");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 在新執行緒中設置相同的認證信息
                if (authentication != null) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    logger.info("異步任務中設置認證用戶: {}", authentication.getName());
                }
                
                // Simulate processing time
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Create a sample recommendation (this should be replaced with actual logic)
                RecommendationResult result = new RecommendationResult();
                result.setRecommendationId(UUID.randomUUID().toString());
                result.setCustomerId(hardware.getCustomerId());
                result.setJobId(job.getJobId());
                
                // Calculate estimated metrics based on job type and hardware
                result.setEstimatedCompletionTimeHours(calculateEstimatedTime(hardware, job));
                result.setEstimatedPowerConsumptionKWh(calculatePowerConsumption(hardware, job));
                result.setEstimatedCostUSD(calculateCost(hardware, job));
                
                logger.info("推薦計算完成，結果ID: {}", result.getRecommendationId());
                return result;
            } finally {
                // 清除當前執行緒的 SecurityContext 以防止內存洩漏
                SecurityContextHolder.clearContext();
            }
        }, taskExecutor);
    }
    
    public CompletableFuture<List<RecommendationResult>> processMultipleRequests(List<CustomerRequest> requests) {
        // 保存當前的 SecurityContext 和認證信息
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("處理多個推薦請求，當前認證用戶: {}", authentication != null ? authentication.getName() : "unknown");
        
        List<CompletableFuture<RecommendationResult>> futures = requests.stream()
            .map(req -> processRequest(req.getHardware(), req.getJob()))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
            );
    }
    
    private double calculateEstimatedTime(CustomerHardware hardware, ComputeJob job) {
        // Placeholder for time estimation logic
        return job.getExpectedDurationHours() * getComplexityFactor(job);
    }
    
    private double calculatePowerConsumption(CustomerHardware hardware, ComputeJob job) {
        // Placeholder for power consumption calculation
        return hardware.getGpuInventory().stream()
            .mapToDouble(gpu -> gpu.getQuantity() * 300.0) // Assuming average 300W per GPU
            .sum() * job.getExpectedDurationHours() / 1000.0; // Convert to kWh
    }
    
    private double calculateCost(CustomerHardware hardware, ComputeJob job) {
        // Placeholder for cost calculation
        double powerCost = calculatePowerConsumption(hardware, job) * 0.12; // Assuming $0.12 per kWh
        return powerCost + (job.getExpectedDurationHours() * 10.0); // Basic cost model
    }
    
    private double getComplexityFactor(ComputeJob job) {
        switch (job.getJobType()) {
            case RENDERING:
                return 1.2;
            case ML_TRAINING:
                return 1.5;
            case DATA_PROCESSING:
                return 1.0;
            default:
                return 1.0;
        }
    }
}