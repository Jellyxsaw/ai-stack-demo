import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { configApi } from '../services/api';
import { 
  UserConfiguration, 
  CustomerRequest, 
  SaveConfigurationRequest, 
  RecommendationResult 
} from '../types';

export const useConfigStore = defineStore('config', () => {
  const configurations = ref<UserConfiguration[]>([]);
  const currentRecommendation = ref<RecommendationResult | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // 獲取所有配置
  async function fetchConfigurations() {
    loading.value = true;
    error.value = null;
    
    try {
      configurations.value = await configApi.getUserConfigurations();
    } catch (err) {
      error.value = '獲取配置列表失敗';
      console.error(err);
    } finally {
      loading.value = false;
    }
  }

  // 根據配置名獲取推薦結果
  async function fetchRecommendationByConfigName(configName: string) {
    loading.value = true;
    error.value = null;
    
    try {
      currentRecommendation.value = await configApi.getConfigurationRecommendation(configName);
      return currentRecommendation.value;
    } catch (err) {
      error.value = '獲取推薦結果失敗';
      console.error(err);
      return null;
    } finally {
      loading.value = false;
    }
  }

  // 保存配置
  async function saveConfiguration(configName: string, request: CustomerRequest) {
    loading.value = true;
    error.value = null;
    
    const saveRequest: SaveConfigurationRequest = {
      configName,
      request
    };
    
    try {
      await configApi.saveConfiguration(saveRequest);
      // 保存成功後刷新配置列表
      await fetchConfigurations();
      return true;
    } catch (err) {
      error.value = '保存配置失敗';
      console.error(err);
      return false;
    } finally {
      loading.value = false;
    }
  }

  // 提交推薦請求
  async function submitRecommendation(request: CustomerRequest) {
    loading.value = true;
    error.value = null;
    
    try {
      currentRecommendation.value = await configApi.submitRecommendation(request);
      return currentRecommendation.value;
    } catch (err) {
      error.value = '提交推薦請求失敗';
      console.error(err);
      return null;
    } finally {
      loading.value = false;
    }
  }

  // 配置名稱列表
  const configNames = computed(() => {
    return configurations.value.map(config => config.configName);
  });

  return {
    configurations,
    currentRecommendation,
    loading,
    error,
    configNames,
    fetchConfigurations,
    fetchRecommendationByConfigName,
    saveConfiguration,
    submitRecommendation
  };
}); 