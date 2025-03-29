package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GpuModel {
    private String id;
    private String modelName;
    private int cudaCores;
    private double computePower;  // TFLOPS
    private int vramGB;
    private double powerConsumptionW;
    private double priceUSD;
}
