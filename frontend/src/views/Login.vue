<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1 class="login-title">GPU 配置管理系統</h1>
      </div>
      
      <div class="login-body">
        <div v-if="authStore.error" class="login-error">
          {{ authStore.error }}
        </div>
        
        <div class="login-form">
          <div class="form-item">
            <label class="form-label">用戶名</label>
            <input 
              v-model="loginForm.username" 
              type="text"
              placeholder="請輸入用戶名"
              class="form-input"
              @keyup.enter="focusPassword"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">密碼</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="請輸入密碼"
              class="form-input"
              ref="passwordInput"
              @keyup.enter="handleLogin"
            />
          </div>
          
          <button 
            class="login-button"
            :disabled="authStore.loading"
            @click="handleLogin"
          >
            {{ authStore.loading ? '登入中...' : '登入' }}
          </button>
          
          <div class="login-info">
            測試賬號: user / user
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '../stores/auth';
import { LoginRequest } from '../types';

const router = useRouter();
const authStore = useAuthStore();
const passwordInput = ref<HTMLInputElement | null>(null);

const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
});

const focusPassword = () => {
  passwordInput.value?.focus();
};

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.error('請輸入用戶名和密碼');
    return;
  }
  
  const success = await authStore.login(loginForm);
  
  if (success) {
    ElMessage.success('登入成功');
    router.push('/dashboard');
  }
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #1a202c;
  padding: 20px;
}

.login-container {
  width: 360px;
  background-color: #2d3748;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.login-header {
  padding: 20px 0;
  background-color: #2d3748;
  border-bottom: 1px solid #4a5568;
}

.login-title {
  margin: 0;
  color: white;
  font-size: 20px;
  text-align: center;
  line-height: 1.2;
  font-weight: normal;
}

.login-body {
  padding: 24px;
}

.login-error {
  background-color: #742a2a;
  color: white;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  color: #cbd5e0;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-input {
  width: 100%;
  height: 40px;
  padding: 0 16px;
  background-color: #374151;
  border: 1px solid #4b5563;
  border-radius: 4px;
  color: white;
  font-size: 16px;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.login-button {
  width: 100%;
  height: 40px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.login-button:hover {
  background-color: #2563eb;
}

.login-button:disabled {
  background-color: #6b7280;
  cursor: not-allowed;
}

.login-info {
  margin-top: 20px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}
</style> 