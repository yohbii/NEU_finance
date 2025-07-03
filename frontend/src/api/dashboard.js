import request from '@/utils/request'

/**
 * 获取仪表板统计数据
 * @returns {Promise} 统计数据响应
 */
export function getDashboardStats() {
  return request({
    url: '/api/dashboard/stats',
    method: 'get'
  })
}

/**
 * 获取净值走势数据
 * @param {Object} params - 查询参数
 * @returns {Promise} 净值走势数据响应
 */
export function getNetValueTrend(params) {
  return request({
    url: '/api/dashboard/net-value-trend',
    method: 'get',
    params
  })
}

/**
 * 获取资产配置数据
 * @returns {Promise} 资产配置数据响应
 */
export function getAssetAllocation() {
  return request({
    url: '/api/dashboard/asset-allocation',
    method: 'get'
  })
}

/**
 * 获取收益分析数据
 * @param {Object} params - 查询参数
 * @returns {Promise} 收益分析数据响应
 */
export function getReturnAnalysis(params) {
  return request({
    url: '/api/dashboard/return-analysis',
    method: 'get',
    params
  })
}

/**
 * 获取风险指标数据
 * @returns {Promise} 风险指标数据响应
 */
export function getRiskMetrics() {
  return request({
    url: '/api/dashboard/risk-metrics',
    method: 'get'
  })
}

/**
 * 获取最新动态
 * @param {Object} params - 查询参数
 * @returns {Promise} 最新动态响应
 */
export function getRecentActivities(params) {
  return request({
    url: '/api/dashboard/recent-activities',
    method: 'get',
    params
  })
} 