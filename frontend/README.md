# GPU資源配置推薦系統 - 前端

這是一個基於Vue 3、TypeScript和Element Plus的前端應用，用於GPU資源配置推薦系統。

## 功能

- 用戶認證：登入和登出功能
- 配置管理：查看、創建和管理GPU配置
- 推薦結果：根據配置獲取推薦的GPU資源分配

## 需要串接的API

1. 登入頁面使用
   - `/api/v1/auth/login` - 用戶登入

2. 基本頁面展示
   - `/api/v1/configurations` - 獲取用戶配置列表
   - `/api/v1/configurations/{configName}/recommendation` - 根據配置名稱獲取推薦結果
   - `/api/v1/recommendations` - 提交新的配置請求（重複名稱會自動更新）

## 開發環境設置

### 安裝依賴

```bash
npm install
```

### 本地開發

```bash
npm run dev
```

### 構建生產版本

```bash
npm run build
```

### 預覽生產構建

```bash
npm run preview
```

## 技術棧

- Vue 3 - 漸進式JavaScript框架
- TypeScript - 類型化的JavaScript
- Vite - 現代前端構建工具
- Element Plus - 基於Vue 3的UI組件庫
- Pinia - Vue.js的狀態管理庫
- Vue Router - Vue.js的官方路由
- Tailwind CSS - 實用優先的CSS框架
- Axios - 基於Promise的HTTP客戶端
