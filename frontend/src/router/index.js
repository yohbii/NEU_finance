import { createRouter, createWebHistory } from 'vue-router'
import { generateRoutes } from './generator'
import Layout from '@/components/layout/Layout.vue'

// 基础路由配置
const baseRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表板',
          requiresAuth: true
        }
      },
      {
        path: 'fund',
        name: 'Fund',
        redirect: '/fund/list',
        meta: {
          title: '基金研究',
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'FundList',
            component: () => import('@/views/fund/list.vue'),
            meta: {
              title: '基金列表',
              requiresAuth: true
            }
          },
          {
            path: 'search',
            name: 'FundSearch',
            component: () => import('@/views/fund/search.vue'),
            meta: {
              title: '基金筛选',
              requiresAuth: true
            }
          },
          {
            path: 'detail/:id',
            name: 'FundDetail',
            component: () => import('@/views/fund/detail.vue'),
            meta: {
              title: '基金详情',
              requiresAuth: true
            }
          }
        ]
      },
      {
        path: 'factor',
        name: 'Factor',
        redirect: '/factor/list',
        meta: {
          title: '因子管理',
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'FactorList',
            component: () => import('@/views/factor/list.vue'),
            meta: {
              title: '因子列表',
              requiresAuth: true
            }
          },
          {
            path: 'analysis',
            name: 'FactorAnalysis',
            component: () => import('@/views/factor/analysis.vue'),
            meta: {
              title: '因子分析',
              requiresAuth: true
            }
          }
        ]
      },
      {
        path: 'strategy',
        name: 'Strategy',
        redirect: '/strategy/list',
        meta: {
          title: '策略管理',
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'StrategyList',
            component: () => import('@/views/strategy/list.vue'),
            meta: {
              title: '策略列表',
              requiresAuth: true
            }
          },
          {
            path: 'backtest',
            name: 'StrategyBacktestStandalone',
            component: () => import('@/views/strategy/backtest.vue'),
            meta: {
              title: '自定义策略',
              requiresAuth: true,
              icon: 'EditPen'
            }
          }
        ]
      },
      {
        path: 'product',
        name: 'Product',
        redirect: '/product/list',
        meta: {
          title: '组合产品',
          requiresAuth: true
        },
        children: [
          {
            path: 'list',
            name: 'ProductList',
            component: () => import('@/views/product/list.vue'),
            meta: {
              title: '产品列表',
              requiresAuth: true
            }
          },
          {
            path: 'approval',
            name: 'ProductApproval',
            component: () => import('@/views/product/approval.vue'),
            meta: {
              title: '产品审核',
              requiresAuth: true
            }
          }
        ]
      },
      {
        path: 'trade',
        name: 'Trade',
        redirect: '/trade/rebalance',
        meta: {
          title: '交易管理',
          requiresAuth: true
        },
        children: [
          {
            path: 'rebalance',
            name: 'TradeRebalance',
            component: () => import('@/views/trade/rebalance.vue'),
            meta: {
              title: '交易记录',
              requiresAuth: true
            }
          },
          {
            path: 'execution',
            name: 'TradeExecution',
            component: () => import('@/views/trade/execution.vue'),
            meta: {
              title: '交易执行',
              requiresAuth: true
            }
          }
        ]
      }
    ]
  }
]

// 注释掉自动生成路由，使用手动配置的路由
// const autoRoutes = generateRoutes()
// const filteredAutoRoutes = autoRoutes.filter(route => !route.path.includes('/auth/'))
// baseRoutes[2].children = filteredAutoRoutes

const router = createRouter({
  history: createWebHistory(),
  routes: baseRoutes
})

// 路由守卫 - 临时禁用用于测试
router.beforeEach((to, from, next) => {
  // const token = localStorage.getItem('token')
  
  // if (to.meta.requiresAuth && !token) {
  //   next('/login')
  // } else {
    // 设置页面标题
    if (to.meta.title) {
      document.title = `${to.meta.title} - 智能投顾系统`
    }
    next()
  // }
})

export default router 