package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_configurations")
public class UserConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String configName;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hardware_id")
    private CustomerHardware hardware;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id")
    private ComputeJob job;
    
    // 
    public UserConfiguration(String username, String configName, CustomerHardware hardware, ComputeJob job) {
        this.username = username;
        this.configName = configName;
        this.hardware = hardware;
        this.job = job;
    }
}
