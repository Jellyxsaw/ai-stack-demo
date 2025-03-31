<template>
  <div class="dashboard-container pt-6">
    <div class="bg-gray-800/50 backdrop-blur-sm shadow-2xl rounded-xl p-6 mb-6 border border-gray-700/50">
      <div class="flex justify-between items-center mb-8 pb-4 border-b border-gray-700/50">
        <h1 class="text-2xl font-bold text-white flex items-center">
          GPU 配置列表
        </h1>
        
        <el-button type="primary" @click="handleCreateNew" size="large" class="flex items-center hover:scale-105 transform transition-all duration-200">
          <el-icon class="mr-2" :size="18"><Plus /></el-icon>
          新建配置
        </el-button>
      </div>
      
      <el-card v-if="configStore.loading" class="w-full flex justify-center items-center p-8 bg-gray-700/50 border-0">
        <div class="w-full max-w-2xl">
          <el-skeleton :rows="5" animated />
        </div>
      </el-card>
      
      <el-empty v-else-if="configStore.configurations.length === 0" 
        description="暫無配置" 
        class="my-12 text-gray-300"
        :image-size="200">
        <el-button type="primary" @click="handleCreateNew" size="large" class="mt-4">
          <el-icon class="mr-2"><Plus /></el-icon>
          添加配置
        </el-button>
      </el-empty>
      
      <div v-else class="config-grid">
        <div 
          v-for="config in configStore.configurations" 
          :key="config.configName"
          class="config-item group"
        >
          <el-card class="bg-gray-700/50 hover:bg-gray-700/70 backdrop-blur-sm transition-all duration-300 border-0 config-card overflow-visible">
            <template #header>
              <div class="flex justify-between items-center py-2 bg-gray-800/70 -mx-3 px-4 -mt-2 cursor-pointer rounded-t-lg transition-colors duration-200 hover:bg-gray-800" 
                @click="viewConfiguration(config.configName)">
                <h3 class="text-base font-medium text-white truncate flex-1 mr-2">{{ config.configName }}</h3>
                <el-tag size="small" :type="getTagType(config.job.jobType)" class="shrink-0">{{ config.job.jobType }}</el-tag>
              </div>
            </template>
            
            <div class="mb-4 text-gray-200 cursor-pointer flex-1" @click="viewConfiguration(config.configName)">
              <div class="mb-3">
                <div class="text-sm font-medium mb-2 text-gray-300">GPU 配置:</div>
                <div class="flex flex-wrap gap-2">
                  <el-tag 
                    v-for="gpu in config.hardware.gpuInventory" 
                    :key="gpu.gpuModelId"
                    size="small"
                    :type="getTagType(gpu.gpuModelId)"
                    class="mb-1 transition-all duration-200 hover:scale-105"
                  >
                    {{ gpu.gpuModelId }} x{{ gpu.quantity }}
                  </el-tag>
                </div>
              </div>
              
              <div class="grid grid-cols-2 gap-3 mt-3">
                <div class="info-block">
                  <div class="text-xs text-gray-400 mb-1">數據大小</div>
                  <div class="text-sm font-medium text-white">
                    {{ config.job.dataSizeGB }} GB
                  </div>
                </div>
                
                <div class="info-block">
                  <div class="text-xs text-gray-400 mb-1">預計時長</div>
                  <div class="text-sm font-medium text-white">
                    {{ config.job.expectedDurationHours }} 小時
                  </div>
                </div>
              </div>
            </div>
            
            <div class="flex justify-end mt-auto pt-3 border-t border-gray-600/50">
              <el-button 
                type="success" 
                size="small" 
                @click.stop="getRecommendation(config.configName)"
                class="hover:scale-105 transform transition-all duration-200"
              >
                獲取推薦
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus, Monitor } from '@element-plus/icons-vue';
import { useConfigStore } from '../stores/config';

const router = useRouter();
const configStore = useConfigStore();

onMounted(async () => {
  await fetchConfigurations();
});

const fetchConfigurations = async () => {
  try {
    await configStore.fetchConfigurations();
  } catch (error) {
    ElMessage.error('獲取配置列表失敗');
  }
};

const handleCreateNew = () => {
  router.push('/configurations/new');
};

const viewConfiguration = (configName: string) => {
  router.push(`/configurations/${configName}`);
};

const getRecommendation = async (configName: string) => {
  try {
    ElMessage({
      message: '正在獲取推薦結果...',
      type: 'info',
      duration: 2000,
      zIndex: 10000,
      offset: 80,
      showClose: true
    });
    
    const result = await configStore.fetchRecommendationByConfigName(configName);
    if (result) {
      ElMessage({
        message: '成功獲取推薦結果',
        type: 'success',
        duration: 2000,
        zIndex: 10000,
        offset: 80,
        showClose: true
      });
      
      setTimeout(() => {
        router.push(`/configurations/${configName}`);
      }, 1000);
    }
  } catch (error) {
    console.error('獲取推薦結果失敗:', error);
    ElMessage({
      message: '獲取推薦結果失敗',
      type: 'error',
      duration: 3000,
      zIndex: 10000,
      offset: 80,
      showClose: true
    });
  }
};

// 根据GPU类型或任务类型返回不同的标签类型
const getTagType = (typeId: string) => {
  if (typeId.includes('3080')) return 'success';
  if (typeId.includes('4090')) return 'primary';
  if (typeId.includes('a100')) return 'danger';
  
  switch(typeId) {
    case 'RENDERING': return 'success';
    case 'ML_TRAINING': return 'primary';
    case 'DATA_PROCESSING': return 'warning';
    default: return 'info';
  }
};
</script>

<style scoped>
.dashboard-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  padding-bottom: 2rem;
}

.config-item {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 280px;
}

.config-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.config-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.3);
}

.info-block {
  background: rgba(30, 41, 59, 0.5);
  padding: 0.75rem;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
}

.info-block:hover {
  background: rgba(30, 41, 59, 0.8);
  transform: translateY(-2px);
}

/* 確保標籤完全顯示 */
.el-tag {
  z-index: 1;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}

/* 確保彈出消息顯示在最上層 */
:deep(.el-message) {
  z-index: 9999 !important;
}

/* 自定義Element Plus卡片樣式 */
:deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 1.25rem;
}

:deep(.el-card__header) {
  padding: 1rem;
  border-bottom: 1px solid rgba(75, 85, 99, 0.3);
}

/* 添加滾動條樣式 */
.dashboard-container::-webkit-scrollbar {
  width: 8px;
}

.dashboard-container::-webkit-scrollbar-track {
  background: rgba(30, 41, 59, 0.2);
  border-radius: 4px;
}

.dashboard-container::-webkit-scrollbar-thumb {
  background: rgba(99, 179, 237, 0.5);
  border-radius: 4px;
}

.dashboard-container::-webkit-scrollbar-thumb:hover {
  background: rgba(99, 179, 237, 0.7);
}

/* 添加動畫效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.config-item {
  animation: fadeIn 0.3s ease-out forwards;
  animation-delay: calc(0.05s * var(--i, 0));
}
</style>