<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div :class="['layout-sidebar', { collapsed: sidebarCollapsed }]">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <span v-show="!sidebarCollapsed" class="logo-text">智能投顾</span>
      </div>
      
      <div class="sidebar-content">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          :collapse="sidebarCollapsed"
          :unique-opened="true"
          router
        >
          <template v-for="item in menuData" :key="item.title">
            <!-- 有子菜单的项目 -->
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.title">
              <template #title>
                <el-icon><component :is="getMenuIcon(item.icon)" /></el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.link"
                :index="child.link"
              >
                {{ child.title }}
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 没有子菜单的项目 -->
            <el-menu-item v-else :index="item.link">
              <el-icon><component :is="getMenuIcon(item.icon)" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="layout-main">
      <!-- 顶部导航 -->
      <div class="layout-header">
        <div class="header-left">
          <el-button 
            text 
            @click="toggleSidebar"
            class="sidebar-toggle"
            :icon="Expand"
          />
          
          <!-- 面包屑导航 -->
          <el-breadcrumb class="breadcrumb" separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbList"
              :key="item.path"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 用户信息 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ userStore.userName ? userStore.userName.charAt(0) : 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userName || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="layout-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Expand,
  ArrowDown,
  User,
  Setting,
  SwitchButton,
  TrendCharts,
  House,
  Coin,
  DataAnalysis,
  Box,
  Money
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏状态
const sidebarCollapsed = ref(false)

// 菜单数据
const menuData = ref([
  {
    title: '仪表板',
    link: '/dashboard',
    icon: 'House'
  },
  {
    title: '基金研究',
    icon: 'Coin',
    children: [
      { title: '基金列表', link: '/fund/list' },
      { title: '基金筛选', link: '/fund/search' }
    ]
  },
  {
    title: '因子管理',
    icon: 'DataAnalysis',
    children: [
      { title: '因子列表', link: '/factor/list' },
      { title: '因子分析', link: '/factor/analysis' }
    ]
  },
  {
    title: '策略管理',
    icon: 'TrendCharts',
    children: [
      { title: '策略列表', link: '/strategy/list' },
      { title: '自定义策略', link: '/strategy/backtest' }
    ]
  },
  {
    title: '组合产品',
    icon: 'Box',
    children: [
      { title: '产品列表', link: '/product/list' },
      { title: '产品审核', link: '/product/approval' }
    ]
  },
  {
    title: '交易管理',
    icon: 'Money',
    children: [
      { title: '交易记录', link: '/trade/rebalance' },
      { title: '交易执行', link: '/trade/execution' }
    ]
  }
])

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 组件挂载时获取菜单和用户信息
onMounted(async () => {
  // getMenuData()
  
  // 如果已登录但用户信息为空，则获取用户信息
  if (userStore.token && !userStore.userName) {
    try {
      await userStore.getUserInfoAction()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
})

// 面包屑导航
const breadcrumbList = computed(() => {
  if (!route || !route.matched) {
    return []
  }
  
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  
  return matched.map(item => ({
    path: item.path || '',
    title: item.meta.title || ''
  }))
})

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 获取菜单图标
const getMenuIcon = (iconName) => {
  const iconMap = {
    'House': House,
    'Coin': Coin,
    'DataAnalysis': DataAnalysis,
    'TrendCharts': TrendCharts,
    'Box': Box,
    'Money': Money
  }
  return iconMap[iconName] || House
}

// 用户操作处理
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await userStore.logoutAction()
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch {
        // 用户取消退出
      }
      break
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.layout-sidebar {
  width: 250px;
  background: #001529;
  color: white;
  transition: width 0.3s ease;
  overflow: hidden;
}

.layout-sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid #1f2937;
}

.logo {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  border-radius: 6px;
  font-size: 18px;
  color: white;
  margin-right: 12px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: white;
}

.sidebar-content {
  height: calc(100vh - 64px);
  overflow-y: auto;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

:deep(.el-menu) {
  background-color: #001529;
  border-right: none;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
  background-color: transparent;
}

:deep(.el-menu-item:hover) {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

:deep(.el-menu-item.is-active) {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.15);
}

:deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.65);
  background-color: transparent;
}

:deep(.el-sub-menu__title:hover) {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

:deep(.el-sub-menu .el-menu) {
  background-color: #000c17;
}

/* 主内容区样式 */
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  height: 64px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle {
  font-size: 18px;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  background: #409eff;
  color: white;
  font-weight: bold;
}

.username {
  font-size: 14px;
  color: #606266;
}

/* 内容区域样式 */
.layout-content {
  flex: 1;
  overflow: auto;
  background: #f5f7fa;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    height: 100vh;
  }
  
  .layout-sidebar.collapsed {
    left: -250px;
  }
  
  .layout-main {
    margin-left: 0;
  }
  
  .breadcrumb {
    display: none;
  }
}
</style> 