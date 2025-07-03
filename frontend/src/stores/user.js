import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  const permissions = ref([])
  const roles = ref([])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value.username || userInfo.value.name || '')
  const avatar = computed(() => userInfo.value.avatar || '')

  // 登录
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      const { token: newToken, user } = response.data
      
      token.value = newToken
      userInfo.value = user
      permissions.value = user.permissions || []
      roles.value = user.roles || []
      
      localStorage.setItem('token', newToken)
      
      return Promise.resolve(response)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 获取用户信息
  const getUserInfoAction = async () => {
    try {
      const response = await getCurrentUser()
      const user = response.data
      
      userInfo.value = user
      permissions.value = user.permissions || []
      roles.value = user.roles || []
      
      return Promise.resolve(response)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 登出
  const logoutAction = async () => {
    try {
      await logout()
      
      token.value = ''
      userInfo.value = {}
      permissions.value = []
      roles.value = []
      
      localStorage.removeItem('token')
      
      return Promise.resolve()
    } catch (error) {
      // 即使接口调用失败，也要清除本地状态
      token.value = ''
      userInfo.value = {}
      permissions.value = []
      roles.value = []
      localStorage.removeItem('token')
      
      return Promise.reject(error)
    }
  }

  // 检查权限
  const hasPermission = (permission) => {
    return permissions.value.includes(permission)
  }

  // 检查角色
  const hasRole = (role) => {
    return roles.value.includes(role)
  }

  // 重置状态
  const resetState = () => {
    token.value = ''
    userInfo.value = {}
    permissions.value = []
    roles.value = []
    localStorage.removeItem('token')
  }

  return {
    // 状态
    token,
    userInfo,
    permissions,
    roles,
    
    // 计算属性
    isLoggedIn,
    userName,
    avatar,
    
    // 方法
    loginAction,
    getUserInfoAction,
    logoutAction,
    hasPermission,
    hasRole,
    resetState
  }
}) 