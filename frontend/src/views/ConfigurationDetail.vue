<template>
  <div class="detail-container">
    <div class="bg-gray-800 shadow-lg rounded-lg p-5 mb-6">
      <div class="flex items-center mb-6 pb-3 border-b border-gray-700">
        <el-button @click="goBack" type="info" plain size="default">
          <el-icon class="mr-1"><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h1 class="text-xl font-bold ml-4 text-white">{{ configName }} 詳情</h1>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 左側配置詳情 -->
        <div>
          <el-card class="mb-6 config-card bg-gray-700 border-0">
            <template #header>
              <div class="font-bold text-lg text-white py-2 bg-gray-800 -mx-4 px-4 -mt-3">配置詳情</div>
            </template>
            
            <div v-if="loading" class="py-4">
              <el-skeleton :rows="5" animated />
            </div>
            
            <div v-else-if="!configuration" class="py-8 text-center text-gray-300">
              <el-empty description="未找到配置信息" />
            </div>
            
            <div v-else class="config-content text-gray-200">
              <div class="mb-5 pb-4 border-b border-gray-600">
                <h3 class="font-medium text-lg mb-3 text-blue-300 flex items-center">
                  <i class="el-icon-cpu mr-2"></i>
                  硬件配置
                </h3>
                <div class="ml-4 space-y-2">
                  <p class="mb-2 flex items-center">
                    <i class="el-icon-user mr-2 text-gray-400"></i>
                    <strong class="mr-2">用戶ID:</strong> 
                    <span class="bg-gray-800 px-2 py-1 rounded">{{ configuration.hardware.customerId }}</span>
                  </p>
                  <p class="font-medium mb-1">GPU清單:</p>
                  <div class="flex flex-wrap gap-2">
                    <el-tag 
                      v-for="gpu in configuration.hardware.gpuInventory" 
                      :key="gpu.gpuModelId"
                      class="mr-2 mb-2 py-1 px-2"
                      :type="getTagType(gpu.gpuModelId)"
                    >
                      {{ gpu.gpuModelId }} x{{ gpu.quantity }}
                    </el-tag>
                  </div>
                </div>
              </div>
              
              <div>
                <h3 class="font-medium text-lg mb-3 text-blue-300 flex items-center">
                  <i class="el-icon-setting mr-2"></i>
                  任務配置
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 ml-4">
                  <div class="info-item bg-gray-800 p-2 border-0 rounded-lg">
                    <strong class="block text-gray-300 mb-1">任務類型:</strong>
                    <el-tag>{{ configuration.job.jobType }}</el-tag>
                  </div>
                  <div class="info-item bg-gray-800 p-2 border-0 rounded-lg">
                    <strong class="block text-gray-300 mb-1">數據大小:</strong>
                    <span class="font-semibold text-gray-200">{{ configuration.job.dataSizeGB }} GB</span>
                  </div>
                  <div class="info-item bg-gray-800 p-2 border-0 rounded-lg">
                    <strong class="block text-gray-300 mb-1">預計時長:</strong>
                    <span class="font-semibold text-gray-200">{{ configuration.job.expectedDurationHours }} 小時</span>
                  </div>
                  <div class="info-item bg-gray-800 p-2 border-0 rounded-lg">
                    <strong class="block text-gray-300 mb-1">精度:</strong>
                    <el-tag :type="getPrecisionType(configuration.job.precision)">
                      {{ configuration.job.precision }}
                    </el-tag>
                  </div>
                  <div class="info-item bg-gray-800 p-2 border-0 rounded-lg">
                    <strong class="block text-gray-300 mb-1">並行度:</strong>
                    <el-tag :type="getParallelismType(configuration.job.parallelism)">
                      {{ configuration.job.parallelism }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 右側推薦結果 -->
        <div>
          <el-card class="config-card bg-gray-700 border-0">
            <template #header>
              <div class="flex justify-between items-center py-2 bg-gray-800 -mx-4 px-4 -mt-3">
                <span class="font-bold text-lg text-white">推薦結果</span>
                <el-button 
                  type="primary" 
                  size="default" 
                  @click="getRecommendation"
                  :loading="recommendationLoading"
                >
                  獲取推薦
                </el-button>
              </div>
            </template>
            
            <div v-if="recommendationLoading" class="py-4">
              <el-skeleton :rows="5" animated />
            </div>
            
            <div v-else-if="!recommendation" class="py-8 text-center text-gray-300">
              <el-empty description="暫無推薦結果">
                <template #description>
                  <p class="text-gray-400">暫無推薦結果，請點擊"獲取推薦"按鈕獲取</p>
                </template>
              </el-empty>
            </div>
            
            <div v-else class="config-content text-gray-200">
              <div v-if="!recommendation.allocations || recommendation.allocations.length === 0" class="mb-5 pb-4 border-b border-gray-600">
                <div class="flex items-center mb-3">
                  <i class="el-icon-warning-outline mr-2 text-yellow-400"></i>
                  <h3 class="font-medium text-lg text-yellow-300">資源分配信息不可用</h3>
                </div>
                <div class="ml-4 p-3 bg-gray-800 border border-yellow-600 rounded-lg">
                  <p class="text-gray-300">此配置的資源分配信息目前不可用。系統已計算出預計指標，但未提供具體GPU分配方案。</p>
                </div>
              </div>
              
              <div>
                <h3 class="font-medium text-lg mb-3 text-blue-300 flex items-center">
                  <i class="el-icon-data-analysis mr-2"></i>
                  預計指標
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div class="info-card bg-gray-800 border-0">
                    <div class="info-card-header bg-blue-900 p-2 border-b border-blue-700 rounded-t-lg">
                      <div class="text-blue-300 font-medium flex items-center">
                        <i class="el-icon-time mr-1"></i>
                        完成時間
                      </div>
                    </div>
                    <div class="info-card-value text-center py-3 font-bold text-gray-200">
                      {{ recommendation.estimatedCompletionTimeHours ? recommendation.estimatedCompletionTimeHours.toFixed(2) : '0.00' }} 小時
                    </div>
                  </div>
                  <div class="info-card bg-gray-800 border-0">
                    <div class="info-card-header bg-green-900 p-2 border-b border-green-700 rounded-t-lg">
                      <div class="text-green-300 font-medium flex items-center">
                        <i class="el-icon-lightning mr-1"></i>
                        電力消耗
                      </div>
                    </div>
                    <div class="info-card-value text-center py-3 font-bold text-gray-200">
                      {{ recommendation.estimatedPowerConsumptionKWh ? recommendation.estimatedPowerConsumptionKWh.toFixed(2) : '0.00' }} kWh
                    </div>
                  </div>
                  <div class="info-card bg-gray-800 border-0">
                    <div class="info-card-header bg-amber-900 p-2 border-b border-amber-700 rounded-t-lg">
                      <div class="text-amber-300 font-medium flex items-center">
                        <i class="el-icon-money mr-1"></i>
                        預計成本
                      </div>
                    </div>
                    <div class="info-card-value text-center py-3 font-bold text-green-400">
                      ${{ recommendation.estimatedCostUSD ? recommendation.estimatedCostUSD.toFixed(2) : '0.00' }}
                    </div>
                  </div>
                </div>
                
                <div class="mt-6 p-4 bg-gray-800 border-0 rounded-lg">
                  <div class="flex justify-between items-center mb-2">
                    <span class="font-medium text-gray-300">綜合評分</span>
                    <span class="font-bold text-lg" :class="getScoreTextColor(completion)">{{ progressFormat(completion) }}</span>
                  </div>
                  <el-progress 
                    :percentage="completion" 
                    :color="progressColor"
                    :stroke-width="15"
                  />
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { useConfigStore } from '../stores/config';
import { UserConfiguration, RecommendationResult } from '../types';

const route = useRoute();
const router = useRouter();
const configStore = useConfigStore();

const configName = computed(() => route.params.name as string);
const configuration = ref<UserConfiguration | null>(null);
const recommendation = ref<RecommendationResult | null>(null);

const loading = ref(false);
const recommendationLoading = ref(false);

// 完成進度百分比
const completion = computed(() => {
  if (!recommendation.value) return 0;
  
  // 根據預計完成時間計算進度
  const estimatedTime = recommendation.value.estimatedCompletionTimeHours;
  const originalTime = configuration.value?.job.expectedDurationHours || estimatedTime;
  
  // 基於完成時間和原始時間的比較計算評分
  if (estimatedTime < originalTime * 0.7) {
    return 80; // 優秀
  } else if (estimatedTime < originalTime * 0.9) {
    return 60; // 中等
  } else if (estimatedTime < originalTime) {
    return 40; // 一般
  } else {
    return 30; // 較低評分
  }
});

// 進度條顏色
const progressColor = computed(() => {
  const progress = completion.value;
  if (progress < 20) return '#f56c6c'; // 差
  if (progress < 40) return '#e6a23c'; // 一般
  if (progress < 60) return '#6f7ad3'; // 中等
  if (progress < 80) return '#1989fa'; // 良好
  return '#67c23a'; // 優秀
});

// 根据GPU类型返回不同标签颜色
const getTagType = (gpuModel: string) => {
  if (gpuModel.includes('3080')) return 'success';
  if (gpuModel.includes('4090')) return 'primary';
  if (gpuModel.includes('a100')) return 'danger';
  return 'info';
};

// 根据精度返回不同标签颜色
const getPrecisionType = (precision: string) => {
  if (precision === 'HIGH') return 'danger';
  if (precision === 'MEDIUM') return 'warning';
  return 'info';
};

// 根据并行度返回不同标签颜色
const getParallelismType = (parallelism: string) => {
  if (parallelism === 'HIGH') return 'danger';
  if (parallelism === 'MEDIUM') return 'warning';
  return 'info';
};

// 进度条格式化
const progressFormat = (percentage: number) => {
  if (percentage < 20) return '較差';
  if (percentage < 40) return '一般';
  if (percentage < 60) return '中等';
  if (percentage < 80) return '良好';
  return '優秀';
};

// 根據評分獲取文字顏色
const getScoreTextColor = (percentage: number) => {
  if (percentage < 20) return 'text-red-500';
  if (percentage < 40) return 'text-orange-400';
  if (percentage < 60) return 'text-blue-400';
  if (percentage < 80) return 'text-sky-400';
  return 'text-green-400';
};

onMounted(async () => {
  await fetchConfiguration();
});

// 獲取配置詳情
const fetchConfiguration = async () => {
  loading.value = true;
  
  try {
    await configStore.fetchConfigurations();
    
    const config = configStore.configurations.find(
      c => c.configName === configName.value
    );
    
    if (config) {
      configuration.value = config;
    } else {
      ElMessage.warning('未找到配置信息');
    }
  } catch (error) {
    ElMessage.error('獲取配置信息失敗');
  } finally {
    loading.value = false;
  }
};

// 獲取推薦結果
const getRecommendation = async () => {
  if (!configName.value) return;
  
  recommendationLoading.value = true;
  
  try {
    const result = await configStore.fetchRecommendationByConfigName(configName.value);
    if (result) {
      recommendation.value = result;
      
      // 添加成功提示
      ElMessage({
        message: '成功獲取推薦結果',
        type: 'success',
        duration: 2000
      });
    }
  } catch (error) {
    ElMessage({
      message: '獲取推薦結果失敗',
      type: 'error',
      duration: 3000
    });
    console.error('獲取推薦結果錯誤:', error);
  } finally {
    recommendationLoading.value = false;
  }
};

// 返回列表
const goBack = () => {
  router.push('/dashboard');
};
</script>

<style scoped>
.detail-container {
  max-width: 1400px;
  margin: 0 auto;
}

.config-card {
  height: 100%;
  transition: all 0.3s ease;
}

.config-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.config-content {
  min-height: 300px;
}

.info-item {
  transition: all 0.2s;
}

.info-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.info-card {
  transition: all 0.2s;
  border-radius: 8px;
  overflow: hidden;
}

.info-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.info-card-value {
  font-size: 18px;
  font-weight: 500;
}
</style> 