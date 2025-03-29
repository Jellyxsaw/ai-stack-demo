package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResult {
    private String recommendationId;
    private String customerId;
    private String jobId;
    private List<GpuAllocation> allocations;
    private double estimatedCompletionTimeHours;
    private double estimatedPowerConsumptionKWh;
    private double estimatedCostUSD;
    private long processingTimeMs;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GpuAllocation {
    private String gpuModelId;
    private int recommendedQuantity;
    private double utilizationPercentage;
}
