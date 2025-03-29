package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_hardware")
public class CustomerHardware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String customerId;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "hardware_id")
    private List<GpuInventory> gpuInventory;
    
    // 
    public CustomerHardware(String customerId, List<GpuInventory> gpuInventory) {
        this.customerId = customerId;
        this.gpuInventory = gpuInventory;
    }
}
