export interface GpuInventoryItem {
  gpuModelId: string;
  quantity: number;
}

export interface CustomerHardware {
  customerId: string;
  gpuInventory: GpuInventoryItem[];
}

export interface ComputeJob {
  customerId: string;
  jobId?: string;
  jobType: string;
  dataSizeGB: number;
  expectedDurationHours: number;
  precision: string;
  parallelism: string;
}

export interface CustomerRequest {
  hardware: CustomerHardware;
  job: ComputeJob;
}

export interface GpuAllocation {
  gpuModelId: string;
  recommendedQuantity: number;
  utilizationPercentage: number;
}

export interface RecommendationResult {
  recommendationId: string;
  customerId: string;
  jobId: string;
  allocations: GpuAllocation[];
  estimatedCompletionTimeHours: number;
  estimatedPowerConsumptionKWh: number;
  estimatedCostUSD: number;
  processingTimeMs: number;
}

// 登录相关类型
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
}

// 用户配置相关类型
export interface UserConfiguration {
  id?: number;
  username: string;
  configName: string;
  hardware: CustomerHardware;
  job: ComputeJob;
}

export interface SaveConfigurationRequest {
  configName: string;
  request: CustomerRequest;
}

export interface MessageResponse {
  message: string;
}
