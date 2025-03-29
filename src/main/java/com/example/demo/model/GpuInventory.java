package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gpu_inventory")
public class GpuInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String gpuModelId;
    private int quantity;
    
    // 
    public GpuInventory(String gpuModelId, int quantity) {
        this.gpuModelId = gpuModelId;
        this.quantity = quantity;
    }
}
