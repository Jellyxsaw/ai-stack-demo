import axios from 'axios';
import { 
  LoginRequest, 
  LoginResponse, 
  UserConfiguration, 
  CustomerRequest, 
  SaveConfigurationRequest, 
  RecommendationResult 
} from '../types';

// 創建axios實例
const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 請求攔截器添加認證令牌
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 認證相關API
export const authApi = {
  // 用戶登入
  login(loginRequest: LoginRequest): Promise<LoginResponse> {
    return apiClient.post('/auth/login', loginRequest)
      .then(response => {
        const { token, username } = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        return response.data;
      });
  },
  
  // 登出
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  },
  
  // 檢查用戶是否已登入
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  },
  
  // 獲取當前用戶名
  getCurrentUser(): string | null {
    return localStorage.getItem('username');
  }
};

// 配置相關API
export const configApi = {
  // 獲取用戶所有配置
  getUserConfigurations(): Promise<UserConfiguration[]> {
    return apiClient.get('/configurations')
      .then(response => response.data);
  },
  
  // 根據配置名稱獲取推薦結果
  getConfigurationRecommendation(configName: string): Promise<RecommendationResult> {
    return apiClient.get(`/configurations/${configName}/recommendation`)
      .then(response => response.data);
  },
  
  // 保存用戶配置
  saveConfiguration(saveRequest: SaveConfigurationRequest): Promise<any> {
    return apiClient.post('/configurations', saveRequest)
      .then(response => response.data);
  },
  
  // 提交推薦請求
  submitRecommendation(request: CustomerRequest): Promise<RecommendationResult> {
    return apiClient.post('/recommendations', request)
      .then(response => response.data);
  }
}; 