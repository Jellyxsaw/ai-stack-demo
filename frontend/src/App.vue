<template>
  <router-view v-if="!authStore.isAuthenticated" />
  
  <div v-else class="app-layout">
    <div class="app-header fixed top-0 left-0 right-0 z-50 bg-gray-800/80 backdrop-blur-md border-b border-gray-700">
      <div class="flex items-center justify-between h-[60px] px-6">
        <div class="flex items-center">
          <router-link to="/dashboard" class="text-xl font-bold text-white hover:text-blue-400 transition-colors">
            GPU 配置管理系統
          </router-link>
        </div>
        
        <div class="flex items-center">
          <el-button 
            type="info" 
            @click="handleLogout" 
            size="small"
            class="hover:bg-gray-600 transition-colors"
          >
            登出
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="app-main">
      <aside class="app-sidebar fixed left-0 top-[60px] bottom-0 bg-gray-800/95 backdrop-blur-md border-r border-gray-700">
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
  background: linear-gradient(135deg, #0F2027 0%, #203A43 50%, #2C5364 100%);
}

.app-layout {
  min-height: 100vh;
  padding-top: 60px;
}

.app-header {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.app-main {
  display: flex;
  min-height: calc(100vh - 60px);
}

.app-sidebar {
  width: 200px;
  z-index: 40;
}

.app-content {
  margin-left: 200px;
  padding: 24px;
  width: calc(100% - 200px);
  min-height: calc(100vh - 60px);
  background-color: transparent;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin: 4px 8px;
  color: #cbd5e0;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s ease;
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

/* 響應式設計 */
@media (max-width: 768px) {
  .app-sidebar {
    transform: translateX(-200px);
  }
  
  .app-content {
    margin-left: 0;
    width: 100%;
  }
  
  .app-layout.sidebar-open .app-sidebar {
    transform: translateX(0);
  }
  
  .app-layout.sidebar-open .app-content {
    margin-left: 200px;
    width: calc(100% - 200px);
  }
}
</style>