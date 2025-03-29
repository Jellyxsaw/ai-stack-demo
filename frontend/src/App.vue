<template>
  <router-view v-if="!authStore.isAuthenticated" />
  
  <div v-else class="app-layout">
    <header class="app-header bg-gray-800 text-white shadow-md py-3 px-6 border-b border-gray-700">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <h1 class="text-xl font-bold text-white">GPU 配置管理系統</h1>
        </div>
        <div class="flex items-center space-x-3">
          <div class="text-gray-200">歡迎, <span class="font-medium">{{ authStore.username }}</span></div>
          <el-button type="info" @click="handleLogout" size="small">登出</el-button>
        </div>
      </div>
    </header>
    
    <div class="app-main">
      <aside class="app-sidebar bg-gray-800 border-r border-gray-700">
        <nav class="py-4">
          <div class="px-4 mb-4 text-gray-400 text-sm font-medium">主菜單</div>
          <router-link to="/dashboard" class="sidebar-item" active-class="sidebar-item-active">
            <el-icon><Menu /></el-icon>
            <span>儀表板</span>
          </router-link>
          <router-link to="/configurations/new" class="sidebar-item" active-class="sidebar-item-active">
            <el-icon><Plus /></el-icon>
            <span>新建配置</span>
          </router-link>
        </nav>
      </aside>
      
      <main class="app-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Menu, Plus } from '@element-plus/icons-vue';
import { useAuthStore } from './stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
  ElMessage.success('已成功登出');
};
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background-color: #1a202c;
  color: #e2e8f0;
}

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  height: 60px;
  z-index: 10;
}

.app-main {
  display: flex;
  flex: 1;
}

.no-header {
  margin-top: 0;
}

.app-sidebar {
  width: 200px;
  min-width: 200px;
  transition: all 0.3s;
}

.app-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.full-width {
  padding: 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 4px;
  color: #cbd5e0;
  text-decoration: none;
  border-radius: 4px;
  margin-left: 8px;
  margin-right: 8px;
  transition: all 0.2s;
}

.sidebar-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #63b3ed;
}

.sidebar-item-active {
  background-color: rgba(255, 255, 255, 0.1);
  color: #63b3ed;
  font-weight: 500;
}

.sidebar-item .el-icon {
  margin-right: 8px;
}
</style>
