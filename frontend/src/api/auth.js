import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录表单数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 登录响应
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise} 登出响应
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise} 用户信息响应
 */
export function getCurrentUser() {
  return request({
    url: '/api/auth/me',
    method: 'get'
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 * @returns {Promise} 注册响应
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

/**
 * 刷新token
 * @param {string} refreshToken - 刷新token
 * @returns {Promise} 新token响应
 */
export function refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh-token',
    method: 'post',
    data: { refreshToken }
  })
}

/**
 * 修改密码
 * @param {Object} data - 修改密码数据
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} 修改密码响应
 */
export function changePassword(data) {
  return request({
    url: '/api/auth/change-password',
    method: 'post',
    data
  })
} 