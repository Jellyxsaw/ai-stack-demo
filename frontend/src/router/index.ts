import { createRouter, createWebHistory, RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router';
import { authApi } from '../services/api';

// 路由组件懒加载
const Login = () => import('../views/Login.vue');
const Dashboard = () => import('../views/Dashboard.vue');
const ConfigurationDetail = () => import('../views/ConfigurationDetail.vue');
const NewConfiguration = () => import('../views/NewConfiguration.vue');

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/configurations/:name',
    name: 'ConfigurationDetail',
    component: ConfigurationDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/configurations/new',
    name: 'NewConfiguration',
    component: NewConfiguration,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth as boolean);
  const isAuthenticated = authApi.isAuthenticated();

  if (requiresAuth && !isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && isAuthenticated) {
    next('/dashboard');
  } else {
    next();
  }
});

export default router; 