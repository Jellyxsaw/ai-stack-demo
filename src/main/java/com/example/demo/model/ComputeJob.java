package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compute_jobs")
public class ComputeJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "job_id", nullable = false)
    private String jobId;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;
    
    @Column(name = "data_size_gb", nullable = false)
    private double dataSizeGB;
    
    @Column(name = "expected_duration_hours")
    private double expectedDurationHours;
    
    @Enumerated(EnumType.STRING)
    private PrecisionLevel precision;
    
    @Enumerated(EnumType.STRING)
    private ParallelismLevel parallelism;
    
    // 
    public ComputeJob(String jobId, String customerId, JobType jobType, double dataSizeGB, 
                     double expectedDurationHours, PrecisionLevel precision, ParallelismLevel parallelism) {
        this.jobId = jobId;
        this.customerId = customerId;
        this.jobType = jobType;
        this.dataSizeGB = dataSizeGB;
        this.expectedDurationHours = expectedDurationHours;
        this.precision = precision;
        this.parallelism = parallelism;
    }
}
