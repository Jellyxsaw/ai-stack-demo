package com.example.demo.config;

import com.example.demo.model.GpuModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class GpuConfiguration {
    
    @Bean
    public List<GpuModel> predefinedGpuModels() {
        return List.of(
            new GpuModel("rtx3080", "NVIDIA RTX 3080", 8704, 29.77, 10, 320, 699),
            new GpuModel("rtx4090", "NVIDIA RTX 4090", 16384, 82.58, 24, 450, 1599),
            new GpuModel("a100", "NVIDIA A100", 6912, 19.5, 40, 400, 10000)
        );
    }
}
