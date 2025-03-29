<template>
  <div class="config-container">
    <div class="header">
      <h1 class="title">GPU 配置管理系統</h1>
      <div class="user-info">
        <span>歡迎, {{ username }}</span>
        <div class="action-buttons">
          <el-button type="primary" size="small" @click="goBack">返回</el-button>
          <el-button type="danger" size="small" @click="logout">登出</el-button>
        </div>
      </div>
    </div>

    <div class="main-content">
      <h2 class="section-title">創建新配置</h2>
      
      <el-form 
        ref="formRef"
        :model="formData" 
        :rules="rules"
        label-position="top"
        class="config-form"
      >
        <div class="form-grid">
          <!-- 基本信息區塊 -->
          <div class="form-section">
            <h3>基本信息</h3>
            <div class="form-row">
              <el-form-item label="配置名稱" prop="configName">
                <el-input v-model="formData.configName" placeholder="請輸入配置名稱" />
              </el-form-item>
              
              <el-form-item label="用戶ID" prop="customerId">
                <el-input v-model="formData.customerId" placeholder="請輸入用戶ID" />
              </el-form-item>
            </div>
          </div>

          <!-- 任務配置區塊 -->
          <div class="form-section">
            <h3>任務配置</h3>
            <div class="form-row">
              <el-form-item label="任務類型" prop="jobType">
                <el-select 
                  v-model="formData.jobType" 
                  placeholder="選擇任務類型" 
                  class="w-full"
                  :popper-options="popperOptions"
                  popper-class="custom-select-dropdown"
                >
                  <el-option label="3D渲染" value="RENDERING" />
                  <el-option label="機器學習訓練" value="ML_TRAINING" />
                  <el-option label="數據處理" value="DATA_PROCESSING" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="數據大小 (GB)" prop="dataSizeGB">
                <el-input-number 
                  v-model="formData.dataSizeGB" 
                  :precision="1" 
                  :step="10" 
                  :min="1"
                  controls-position="right"
                />
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="預計運行時間 (小時)" prop="expectedDurationHours">
                <el-input-number 
                  v-model="formData.expectedDurationHours" 
                  :precision="1" 
                  :step="1" 
                  :min="0.5"
                  controls-position="right"
                />
              </el-form-item>
              
              <el-form-item label="精度要求" prop="precision">
                <el-select 
                  v-model="formData.precision"
                  :popper-options="popperOptions"
                  popper-class="custom-select-dropdown"
                >
                  <el-option label="高" value="HIGH" />
                  <el-option label="中" value="MEDIUM" />
                  <el-option label="低" value="LOW" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="並行度" prop="parallelism">
                <el-select 
                  v-model="formData.parallelism"
                  :popper-options="popperOptions"
                  popper-class="custom-select-dropdown"
                >
                  <el-option label="高" value="HIGH" />
                  <el-option label="中" value="MEDIUM" />
                  <el-option label="低" value="LOW" />
                </el-select>
              </el-form-item>
            </div>
          </div>

          <!-- 硬件配置區塊 -->
          <div class="form-section hardware-section">
            <h3>硬件配置</h3>
            <div class="gpu-table">
              <table>
                <thead>
                  <tr>
                    <th>GPU型號</th>
                    <th>數量</th>
                    <th class="action-column">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(gpu, index) in formData.gpuInventory" :key="index">
                    <td>
                      <el-form-item 
                        :prop="`gpuInventory.${index}.gpuModelId`"
                        :rules="{ required: true, message: '請選擇GPU型號', trigger: 'change' }"
                        class="no-margin"
                      >
                        <el-select 
                          v-model="gpu.gpuModelId" 
                          placeholder="選擇GPU型號"
                          :popper-options="popperOptions"
                          popper-class="custom-select-dropdown"
                        >
                          <el-option label="RTX 3080" value="rtx3080" />
                          <el-option label="RTX 4090" value="rtx4090" />
                          <el-option label="A100" value="a100" />
                        </el-select>
                      </el-form-item>
                    </td>
                    <td>
                      <el-form-item 
                        :prop="`gpuInventory.${index}.quantity`"
                        :rules="{ required: true, type: 'number', message: '請輸入數量', trigger: 'change' }"
                        class="no-margin"
                      >
                        <el-input-number 
                          v-model="gpu.quantity" 
                          :min="1" 
                          :max="16"
                          controls-position="right"
                          class="quantity-input"
                        />
                      </el-form-item>
                    </td>
                    <td class="action-cell">
                      <el-button 
                        v-if="formData.gpuInventory.length > 1"
                        type="danger" 
                        circle
                        size="small"
                        @click="removeGpu(index)"
                      >
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <el-button type="primary" @click="addGpu" class="add-gpu-btn">
              添加GPU
            </el-button>
          </div>
        </div>

        <div class="action-footer">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="submitForm" :loading="loading">
            保存配置
          </el-button>
          <el-button type="success" @click="submitAndGetRecommendation" :loading="loading">
            保存並獲取推薦
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { useConfigStore } from '../stores/config';
import { CustomerRequest, GpuInventoryItem } from '../types';

const router = useRouter();
const configStore = useConfigStore();
const formRef = ref<FormInstance>();
const loading = ref(false);
const username = ref('user'); // 假設從認證中獲取用戶名

// Popper.js 配置選項 - 用於下拉選單
const popperOptions = {
  modifiers: [
    {
      name: 'offset',
      options: {
        offset: [0, 8],
      },
    },
    {
      name: 'preventOverflow',
      options: {
        padding: 8,
        boundary: 'viewport',
      },
    },
    {
      name: 'flip',
      options: {
        fallbackPlacements: ['top', 'bottom', 'right', 'left'],
        padding: 8,
      },
    },
  ],
  strategy: 'fixed',
};

// 表單數據
const formData = reactive({
  configName: '',
  customerId: '',
  gpuInventory: [{ gpuModelId: '', quantity: 1 }] as GpuInventoryItem[],
  jobType: '',
  dataSizeGB: 100,
  expectedDurationHours: 12,
  precision: 'MEDIUM',
  parallelism: 'MEDIUM'
});

// 表單驗證規則
const rules = reactive<FormRules>({
  configName: [
    { required: true, message: '請輸入配置名稱', trigger: 'blur' },
    { min: 2, max: 30, message: '長度在2到30個字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, message: '請輸入用戶ID', trigger: 'blur' }
  ],
  jobType: [
    { required: true, message: '請選擇任務類型', trigger: 'change' }
  ],
  dataSizeGB: [
    { required: true, type: 'number', message: '請輸入數據大小', trigger: 'blur' }
  ],
  expectedDurationHours: [
    { required: true, type: 'number', message: '請輸入預計運行時間', trigger: 'blur' }
  ],
  precision: [
    { required: true, message: '請選擇精度要求', trigger: 'change' }
  ],
  parallelism: [
    { required: true, message: '請選擇並行度', trigger: 'change' }
  ]
});

// 添加GPU
const addGpu = () => {
  formData.gpuInventory.push({ gpuModelId: '', quantity: 1 });
};

// 移除GPU
const removeGpu = (index: number) => {
  formData.gpuInventory.splice(index, 1);
};

// 重置表單
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
    formData.gpuInventory = [{ gpuModelId: '', quantity: 1 }];
  }
};

// 構建請求數據
const buildRequestData = (): CustomerRequest => {
  return {
    hardware: {
      customerId: formData.customerId,
      gpuInventory: formData.gpuInventory.filter(gpu => gpu.gpuModelId)
    },
    job: {
      customerId: formData.customerId,
      jobType: formData.jobType,
      dataSizeGB: formData.dataSizeGB,
      expectedDurationHours: formData.expectedDurationHours,
      precision: formData.precision,
      parallelism: formData.parallelism
    }
  };
};

// 提交表單
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      
      try {
        const requestData = buildRequestData();
        const success = await configStore.saveConfiguration(formData.configName, requestData);
        
        if (success) {
          ElMessage.success('配置保存成功');
          router.push('/dashboard');
        }
      } catch (error) {
        ElMessage.error('保存配置失敗');
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning('請檢查表單內容');
    }
  });
};

// 提交表單並獲取推薦
const submitAndGetRecommendation = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      
      try {
        const requestData = buildRequestData();
        const success = await configStore.saveConfiguration(formData.configName, requestData);
        
        if (success) {
          ElMessage({
            message: '配置保存成功，正在獲取推薦結果...',
            type: 'info',
            duration: 2000
          });
          
          const result = await configStore.fetchRecommendationByConfigName(formData.configName);
          
          if (result) {
            ElMessage({
              message: '成功獲取推薦結果',
              type: 'success',
              duration: 2000
            });
            
            // 延遲跳轉讓用戶看到成功消息
            setTimeout(() => {
              router.push(`/configurations/${formData.configName}`);
            }, 800);
          }
        }
      } catch (error) {
        console.error('操作失敗:', error);
        ElMessage({
          message: '操作失敗，請稍後再試',
          type: 'error',
          duration: 3000
        });
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning('請檢查表單內容');
    }
  });
};

// 返回儀表板
const goBack = () => {
  router.push('/dashboard');
};

// 登出
const logout = () => {
  // 實現登出邏輯
  router.push('/login');
};

// 掛載時添加全局樣式修復下拉選單問題
onMounted(() => {
  const styleEl = document.createElement('style');
  styleEl.textContent = `
    .el-popper.custom-select-dropdown {
      z-index: 9999 !important;
    }
  `;
  document.head.appendChild(styleEl);
});
</script>

<style scoped>
.config-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #1a202c;
  color: #e2e8f0;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background-color: #2d3748;
  border-bottom: 1px solid #4a5568;
  flex-shrink: 0;
  z-index: 100; /* 確保頭部總是在最上層 */
}

.title {
  font-size: 1.25rem;
  font-weight: bold;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.main-content {
  padding: 1rem;
  overflow-y: auto;
  flex-grow: 1;
  position: relative;
  z-index: 1;
}

.section-title {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #4a5568;
  padding-bottom: 0.5rem;
}

.config-form {
  width: 100%;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-section {
  background-color: #2d3748;
  border-radius: 6px;
  padding: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.form-section h3 {
  font-size: 1rem;
  margin-top: 0;
  margin-bottom: 0.75rem;
  padding-bottom: 0.4rem;
  border-bottom: 1px solid #4a5568;
  font-weight: 500;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.action-footer {
  display: flex;
  justify-content: center;
  gap: 0.75rem;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}

/* 硬件配置表格樣式 */
.hardware-section {
  width: 100%;
}

.gpu-table {
  width: 100%;
  margin-bottom: 0.75rem;
  overflow-x: auto;
}

.gpu-table table {
  width: 100%;
  border-collapse: collapse;
}

.gpu-table th {
  text-align: left;
  padding: 0.4rem 0.5rem;
  background-color: #364154;
  font-weight: 500;
  font-size: 0.9rem;
}

.gpu-table td {
  padding: 0.4rem 0.5rem;
  border-bottom: 1px solid #4a5568;
}

.action-column {
  width: 60px;
  text-align: center;
}

.action-cell {
  text-align: center;
}

.add-gpu-btn {
  width: auto;
  margin-top: 0.5rem;
}

.quantity-input {
  width: 100%;
}

.no-margin {
  margin-bottom: 0 !important;
}

/* 表單元素樣式 */
:deep(.el-form-item__label) {
  color: #ffffff;
  padding-bottom: 3px;
  font-size: 0.9rem;
  font-weight: 500;
}

:deep(.el-form-item) {
  margin-bottom: 12px;
}

:deep(.el-input__wrapper),
:deep(.el-input-number__wrapper),
:deep(.el-select__wrapper) {
  background-color: #1e293b;
  box-shadow: 0 0 0 1px #4a5568;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input-number__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #718096;
}

:deep(.el-input__inner),
:deep(.el-input-number__inner) {
  color: #ffffff;
  font-size: 0.9rem;
  height: 32px;
  background-color: #1e293b;
}

/* 數字輸入框樣式優化 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  display: none;
}

:deep(.el-input-number__wrapper) {
  padding-right: 8px;
}

:deep(.el-input-number .el-input__wrapper) {
  padding-right: 8px;
  background-color: #1e293b;
}

:deep(.el-input-number .el-input__inner) {
  color: #ffffff;
  text-align: left;
  font-weight: 500;
}

:deep(.el-button) {
  font-size: 0.9rem;
  height: auto;
  padding: 6px 12px;
}

:deep(.el-button--small) {
  padding: 5px 10px;
  font-size: 0.8rem;
}

:deep(.el-button.is-circle) {
  padding: 5px;
}

/* 自定義下拉箭頭樣式 */
:deep(.el-select__caret) {
  display: none;
}

:deep(.el-select__wrapper) {
  position: relative;
}

:deep(.el-select__wrapper):after {
  content: '';
  position: absolute;
  right: 8px;
  top: 50%;
  width: 6px;
  height: 6px;
  border-right: 2px solid #ffffff;
  border-bottom: 2px solid #ffffff;
  transform: translateY(-50%) rotate(45deg);
  pointer-events: none;
  opacity: 1;
}

:deep(.el-select.is-focus .el-select__wrapper):after {
  transform: translateY(-50%) rotate(-135deg);
}

:deep(.el-input__suffix) {
  right: 20px;
}

/* 移除並行度和精度下拉框的箭頭 */
:deep(.el-select__wrapper .el-select__icon) {
  display: none;
}

/* 修復下拉選單樣式 - 關鍵修改 */
:deep(.el-popper) {
  background-color: #1a202c !important;
  border: 1px solid #4a5568 !important;
  border-radius: 4px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5) !important;
  z-index: 9999 !important; /* 大幅提高 z-index */
}

:deep(.el-select-dropdown__wrap) {
  max-height: 240px;
  background-color: #1a202c;
}

:deep(.el-select-dropdown__item) {
  color: #ffffff !important;
  font-weight: normal;
  font-size: 0.9rem;
  height: 36px;
  line-height: 36px;
  padding: 0 16px;
  position: relative !important; /* 確保項目正確顯示 */
}

:deep(.el-select-dropdown__item.hover),
:deep(.el-select-dropdown__item:hover) {
  background-color: #2d3748 !important;
}

:deep(.el-select-dropdown__item.selected) {
  background-color: #3b82f6 !important;
  color: #ffffff !important;
  font-weight: 500;
}

/* 移除下拉選單箭頭 */
:deep(.el-popper__arrow) {
  display: none !important;
}

/* 修復下拉選單顯示問題 */
:deep(.el-scrollbar__view) {
  overflow-x: hidden !important;
}

:deep(.el-select-dropdown) {
  overflow: visible !important;
  margin: 5px 0 !important;
}

:deep(.el-scrollbar) {
  width: 100% !important;
}

/* 修正下拉選單的寬度 */
:deep(.el-popper.is-light) {
  min-width: 140px !important;
  width: auto !important; /* 關鍵修改：不固定寬度 */
}

:deep(.el-select__popper) {
  min-width: 100% !important;
  width: auto !important; /* 關鍵修改：不固定寬度 */
  z-index: 9999 !important; /* 關鍵修改：提高 z-index */
}

/* 修正下拉選單的內邊距 */
:deep(.el-select-dropdown__list) {
  padding: 4px 0 !important;
}

/* 修正下拉選單的位置 */
:deep(.el-select__popper[data-popper-placement^=bottom]) {
  margin-top: 6px !important;
}

:deep(.el-select__popper[data-popper-placement^=top]) {
  margin-bottom: 6px !important;
}

/* 確保滾動條不遮擋內容 */
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden !important;
  overflow-y: auto !important;
}

/* 修正選擇器的基本定位 */
:deep(.el-select) {
  position: relative !important;
  width: 100% !important;
}

/* 確保下拉選單在正確的位置顯示 */
:deep(.el-select-dropdown.el-popper) {
  position: fixed !important; /* 關鍵修改：使用固定定位 */
}

/* 調整下拉選單的最大高度，避免過長 */
:deep(.el-select-dropdown__wrap) {
  max-height: 240px !important;
}

/* 響應式布局 */
@media (min-width: 768px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hardware-section {
    grid-column: span 2;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .action-footer {
    flex-wrap: nowrap;
  }
  
  .section-title {
    font-size: 1.2rem;
  }
}

/* 適配更小螢幕 */
@media (max-width: 640px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .user-info {
    width: 100%;
    justify-content: space-between;
  }
  
  .action-footer {
    flex-direction: column;
    width: 100%;
  }
  
  .action-footer .el-button {
    width: 100%;
  }
}
</style>