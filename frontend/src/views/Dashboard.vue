<template>
  <div class="dashboard-container pt-4">
    <div class="bg-gray-800 shadow-lg rounded-lg p-5 mb-6">
      <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-700">
        <h1 class="text-xl font-bold text-white">GPU 配置列表</h1>
        
        <el-button type="primary" @click="handleCreateNew" size="default">
          <el-icon class="mr-1" :size="16"><Plus /></el-icon>
          新建配置
        </el-button>
      </div>
      
      <el-card v-if="configStore.loading" class="w-full flex justify-center items-center p-6 bg-gray-700">
        <el-skeleton :rows="5" animated />
      </el-card>
      
      <el-empty v-else-if="configStore.configurations.length === 0" description="暫無配置" class="mt-8 mb-8 text-gray-300">
        <el-button type="primary" @click="handleCreateNew" size="default">添加配置</el-button>
      </el-empty>
      
      <div v-else class="config-grid">
        <div 
          v-for="config in configStore.configurations" 
          :key="config.configName"
          class="config-item"
        >
          <el-card class="bg-gray-700 hover:shadow-xl transition-all border-0 config-card">
            <template #header>
              <div class="flex justify-between items-center py-1 bg-gray-800 -mx-3 px-3 -mt-2 cursor-pointer" @click="viewConfiguration(config.configName)">
                <h3 class="text-base font-medium text-white truncate">{{ config.configName }}</h3>
                <el-tag size="small" :type="getTagType(config.job.jobType)">{{ config.job.jobType }}</el-tag>
              </div>
            </template>
            
            <div class="mb-2 text-gray-200 cursor-pointer flex-1" @click="viewConfiguration(config.configName)">
              <div class="mb-2">
                <div class="text-sm font-medium mb-1">GPU:</div>
                <div class="flex flex-wrap gap-1">
                  <el-tag 
                    v-for="gpu in config.hardware.gpuInventory" 
                    :key="gpu.gpuModelId"
                    size="small"
                    :type="getTagType(gpu.gpuModelId)"
                    class="mb-1"
                  >
                    {{ gpu.gpuModelId }} x{{ gpu.quantity }}
                  </el-tag>
                </div>
              </div>
              
              <div class="grid grid-cols-2 gap-2 mt-2 bg-gray-800 p-2 rounded-lg">
                <div class="flex items-center p-1">
                  <div class="text-xs text-gray-300">
                    <span class="font-medium">數據:</span> {{ config.job.dataSizeGB }} GB
                  </div>
                </div>
                
                <div class="flex items-center p-1">
                  <div class="text-xs text-gray-300">
                    <span class="font-medium">時長:</span> {{ config.job.expectedDurationHours }} 小時
                  </div>
                </div>
              </div>
            </div>
            
            <div class="flex justify-end mt-auto border-t border-gray-600 pt-2">
              <el-button type="success" size="small" @click.stop="getRecommendation(config.configName)">
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
import { Plus } from '@element-plus/icons-vue';
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
  padding: 0 1rem;
}

.config-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  padding-bottom: 2rem;
  position: relative;
}

@media (min-width: 640px) {
  .config-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .config-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1280px) {
  .config-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.config-item {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  position: relative;
  margin-bottom: 1rem;
}

.config-card {
  height: 100%; 
  min-height: 0;
  overflow: visible;
  position: relative;
  z-index: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.el-card {
  transition: all 0.3s;
}

.el-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  z-index: 2;
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
}

:deep(.el-card__header) {
  padding: 12px 16px;
}
</style> 