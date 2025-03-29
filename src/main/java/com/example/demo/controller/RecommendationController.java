package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.UserConfigurationRepository;
import com.example.demo.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    private final UserConfigurationRepository userConfigurationRepository;
    
    @Autowired
    public RecommendationController(RecommendationService recommendationService, 
                                   UserConfigurationRepository userConfigurationRepository) {
        this.recommendationService = recommendationService;
        this.userConfigurationRepository = userConfigurationRepository;
    }
    
    @Operation(
        summary = "\u63d0\u4ea4\u55ae\u4e00GPU\u8cc7\u6e90\u914d\u7f6e\u8acb\u6c42",
        description = "\u63d0\u4ea4\u55ae\u4e00\u7528\u6236\u7684\u786c\u9ad4\u914d\u7f6e\u548c\u8a08\u7b97\u9700\u6c42\uff0c\u7372\u53d6\u63a8\u85a6\u914d\u7f6e",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "3D\u6e32\u67d3\u4efb\u52d9\u7bc4\u4f8b",
                        value = """
                        {
                          "hardware": {
                            "customerId": "user123",
                            "gpuInventory": [
                              {
                                "gpuModelId": "rtx3080",
                                "quantity": 2
                              },
                              {
                                "gpuModelId": "rtx4090",
                                "quantity": 1
                              }
                            ]
                          },
                          "job": {
                            "customerId": "user123",
                            "jobType": "RENDERING",
                            "dataSizeGB": 250.5,
                            "expectedDurationHours": 12.5,
                            "precision": "HIGH",
                            "parallelism": "MEDIUM"
                          }
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "\u6a5f\u5668\u5b78\u7fd2\u8a13\u7df4\u7bc4\u4f8b",
                        value = """
                        {
                          "hardware": {
                            "customerId": "ml_team_01",
                            "gpuInventory": [
                              {
                                "gpuModelId": "a100",
                                "quantity": 4
                              }
                            ]
                          },
                          "job": {
                            "customerId": "ml_team_01",
                            "jobType": "ML_TRAINING",
                            "dataSizeGB": 1024.0,
                            "expectedDurationHours": 72.0,
                            "precision": "HIGH",
                            "parallelism": "HIGH"
                          }
                        }
                        """
                    )
                }
            )
        )
    )
    @PostMapping("/recommendations")
    public CompletableFuture<ResponseEntity<RecommendationResult>> getRecommendation(
            @RequestBody CustomerRequest request) {
        if (request.getJob().getJobId() == null) {
            request.getJob().setJobId(UUID.randomUUID().toString());
        }
        
        return recommendationService.processRequest(request.getHardware(), request.getJob())
            .thenApply(ResponseEntity::ok);
    }
    
    @Operation(
        summary = "\u6279\u91cf\u63d0\u4ea4GPU\u8cc7\u6e90\u914d\u7f6e\u8acb\u6c42",
        description = "\u540c\u6642\u63d0\u4ea4\u591a\u4e2a\u7528\u6236\u7684\u914d\u7f6e\u8acb\u6c42\uff0c\u7372\u53d6\u6279\u91cf\u63a8\u85a6\u7d50\u679c",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "\u6df7\u5408\u5de5\u4f5c\u8ca0\u8f09\u7bc4\u4f8b",
                        value = """
                        [
                          {
                            "hardware": {
                              "customerId": "render_team_01",
                              "gpuInventory": [
                                {
                                  "gpuModelId": "rtx4090",
                                  "quantity": 3
                                }
                              ]
                            },
                            "job": {
                              "customerId": "render_team_01",
                              "jobType": "RENDERING",
                              "dataSizeGB": 500.0,
                              "expectedDurationHours": 24.0,
                              "precision": "HIGH",
                              "parallelism": "MEDIUM"
                            }
                          },
                          {
                            "hardware": {
                              "customerId": "data_science_01",
                              "gpuInventory": [
                                {
                                  "gpuModelId": "a100",
                                  "quantity": 8
                                }
                              ]
                            },
                            "job": {
                              "customerId": "data_science_01",
                              "jobType": "DATA_PROCESSING",
                              "dataSizeGB": 2048.0,
                              "expectedDurationHours": 48.0,
                              "precision": "MEDIUM",
                              "parallelism": "HIGH"
                            }
                          }
                        ]
                        """
                    )
                }
            )
        )
    )
    @PostMapping("/recommendations/batch")
    public CompletableFuture<ResponseEntity<List<RecommendationResult>>> getBatchRecommendations(
            @RequestBody List<CustomerRequest> requests) {
        requests.forEach(request -> {
            if (request.getJob().getJobId() == null) {
                request.getJob().setJobId(UUID.randomUUID().toString());
            }
        });
        
        return recommendationService.processMultipleRequests(requests)
            .thenApply(ResponseEntity::ok);
    }
    
    @Operation(
        summary = "\u904b\u884c\u6f14\u793a",
        description = "\u4f7f\u7528\u9810\u5b9a\u7684\u6e2c\u8a66\u6848\u4f8b\u904b\u884c\u7cfb\u7edf\u6f14\u793a"
    )
    @PostMapping("/demo")
    public CompletableFuture<ResponseEntity<List<RecommendationResult>>> runDemo() {
        List<CustomerRequest> demoRequests = createDemoRequests();
        return recommendationService.processMultipleRequests(demoRequests)
            .thenApply(ResponseEntity::ok);
    }
    
    @Operation(
        summary = "\u5132\u5b58\u7528\u6236\u914d\u7f6e",
        description = "\u5c07\u6307\u5b9a\u7528\u6236\u63d0\u4ea4\u7684\u8a2d\u5099\u914d\u7f6e\u66f4\u65b0\u5230\u8cc7\u6599\u5eab",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "3D\u6e32\u67d3\u914d\u7f6e\u7bc4\u4f8b",
                        value = """
                        {
                          "configName": "\u6211\u7684\u6e32\u67d3\u914d\u7f6e",
                          "request": {
                            "hardware": {
                              "customerId": "user123",
                              "gpuInventory": [
                                {
                                  "gpuModelId": "rtx3080",
                                  "quantity": 2
                                },
                                {
                                  "gpuModelId": "rtx4090",
                                  "quantity": 1
                                }
                              ]
                            },
                            "job": {
                              "customerId": "user123",
                              "jobType": "RENDERING",
                              "dataSizeGB": 250.5,
                              "expectedDurationHours": 12.5,
                              "precision": "HIGH",
                              "parallelism": "MEDIUM"
                            }
                          }
                        }
                        """
                    )
                }
            )
        )
    )
    @PostMapping("/configurations")
    public ResponseEntity<?> saveUserConfiguration(@RequestBody SaveConfigurationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 確保 job 有一個有效的 jobId
        if (request.getRequest().getJob().getJobId() == null) {
            request.getRequest().getJob().setJobId(UUID.randomUUID().toString());
        }

        // 檢查configName是否存在 如已存在則更新

        UserConfiguration existingConfig = userConfigurationRepository.findByUsernameAndConfigName(username, request.getConfigName());
        if (existingConfig != null) {
            existingConfig.setHardware(request.getRequest().getHardware());
            existingConfig.setJob(request.getRequest().getJob());
            userConfigurationRepository.save(existingConfig);
            return ResponseEntity.ok(new MessageResponse("\u914d\u7f6e\u5df2\u6210\u529f\u5132\u5b58"));
        }
        
        UserConfiguration configuration = new UserConfiguration(
            username,
            request.getConfigName(),
            request.getRequest().getHardware(),
            request.getRequest().getJob()
        );
        
        userConfigurationRepository.save(configuration);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("\u914d\u7f6e\u5df2\u6210\u529f\u5132\u5b58"));
    }
    
    @Operation(
        summary = "\u7372\u53d6\u7528\u6236\u914d\u7f6e\u5217\u8868",
        description = "\u7372\u53d6\u7576\u524d\u7528\u6236\u6240\u6709\u4fdd\u5b58\u7684\u914d\u7f6e\u5217\u8868"
    )
    @GetMapping("/configurations")
    public ResponseEntity<List<UserConfiguration>> getUserConfigurations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        List<UserConfiguration> configurations = userConfigurationRepository.findByUsername(username);
        
        return ResponseEntity.ok(configurations);
    }
    

    @Operation(
        summary = "\u7372\u53d6\u7279\u5b9a\u914d\u7f6e\u7684\u63a8\u85a6\u7d50\u679c",
        description = "\u6839\u64da\u7528\u6236\u4fdd\u5b58\u7684\u7279\u5b9a\u914d\u7f6e\u8a08\u7b97\u4e26\u8fd4\u56de\u63a8\u85a6\u7d50\u679c"
    )
    @GetMapping("/configurations/{configName}/recommendation")
    public CompletableFuture<ResponseEntity<RecommendationResult>> getConfigurationRecommendation(
            @PathVariable String configName) {
        
        System.out.println("getConfigurationRecommendation");
        
        // 保存當前的 SecurityContext 和認證信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication: " + authentication.getName());
    
        String username = authentication.getName();
    
        // 印出username
        System.out.println("username: " + username);
        
        UserConfiguration configuration = userConfigurationRepository.findByUsernameAndConfigName(username, configName);
        
        // 如果configName找不到則返回錯誤訊息:未找到配置
        if (configuration == null) {
            RecommendationResult emptyResult = new RecommendationResult();
            emptyResult.setRecommendationId("error");
            emptyResult.setCustomerId(username);
            emptyResult.setJobId("not_found");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(emptyResult));
        }
    
        // 創建一個新的 CompletableFuture 在同一個線程上下文中執行
        return recommendationService.processRequest(configuration.getHardware(), configuration.getJob())
            .thenApply(result -> {
                System.out.println("處理完成，返回結果");
                return ResponseEntity.ok(result);
            })
            // 處理異常情況
            .exceptionally(ex -> {
                System.err.println("處理請求時發生錯誤: " + ex.getMessage());
                RecommendationResult errorResult = new RecommendationResult();
                errorResult.setRecommendationId("error");
                errorResult.setCustomerId(username);
                errorResult.setJobId("error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
            });
    }
    
    private List<CustomerRequest> createDemoRequests() {
        CustomerRequest renderingJob = new CustomerRequest(
            new CustomerHardware("user1", List.of(new GpuInventory("rtx3080", 2))),
            new ComputeJob(UUID.randomUUID().toString(), "user1", JobType.RENDERING, 100.0, 24.0, PrecisionLevel.HIGH, ParallelismLevel.MEDIUM)
        );
        
        CustomerRequest mlJob = new CustomerRequest(
            new CustomerHardware("user2", List.of(new GpuInventory("rtx4090", 4))),
            new ComputeJob(UUID.randomUUID().toString(), "user2", JobType.ML_TRAINING, 500.0, 48.0, PrecisionLevel.MEDIUM, ParallelismLevel.HIGH)
        );
        
        CustomerRequest dataJob = new CustomerRequest(
            new CustomerHardware("user3", List.of(new GpuInventory("a100", 8))),
            new ComputeJob(UUID.randomUUID().toString(), "user3", JobType.DATA_PROCESSING, 1000.0, 12.0, PrecisionLevel.LOW, ParallelismLevel.HIGH)
        );
        
        return List.of(renderingJob, mlJob, dataJob);
    }
    
    @Data
    public static class SaveConfigurationRequest {
        private String configName;
        private CustomerRequest request;
    }
    
    @Data
    public static class MessageResponse {
        private final String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
    }
}
