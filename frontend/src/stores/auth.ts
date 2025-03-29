import { defineStore } from 'pinia';
import { ref } from 'vue';
import { authApi } from '../services/api';
import { LoginRequest } from '../types';

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(authApi.isAuthenticated());
  const username = ref(authApi.getCurrentUser());
  const loading = ref(false);
  const error = ref<string | null>(null);

  // 登入
  async function login(loginRequest: LoginRequest) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await authApi.login(loginRequest);
      isAuthenticated.value = true;
      username.value = response.username;
      return true;
    } catch (err) {
      error.value = '登入失敗，請檢查使用者名稱和密碼';
      return false;
    } finally {
      loading.value = false;
    }
  }

  // 登出
  function logout() {
    authApi.logout();
    isAuthenticated.value = false;
    username.value = null;
  }

  return {
    isAuthenticated,
    username,
    loading,
    error,
    login,
    logout
  };
}); 