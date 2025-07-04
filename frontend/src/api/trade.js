import request from '@/utils/request'

/**
 * 分页查询交易记录列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 交易记录列表响应
 */
export function getTradeList(params) {
  return request({
    url: '/api/trades',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取交易记录详情
 * @param {number} id - 交易记录ID
 * @returns {Promise} 交易记录详情响应
 */
export function getTradeDetail(id) {
  return request({
    url: `/api/trades/${id}`,
    method: 'get'
  })
}

/**
 * 根据交易编号获取交易记录详情
 * @param {string} tradeNo - 交易编号
 * @returns {Promise} 交易记录详情响应
 */
export function getTradeByTradeNo(tradeNo) {
  return request({
    url: `/trades/trade-no/${tradeNo}`,
    method: 'get'
  })
}

/**
 * 创建交易记录
 * @param {Object} data - 交易记录数据
 * @returns {Promise} 创建结果响应
 */
export function createTrade(data) {
  return request({
    url: '/api/trades',
    method: 'post',
    data
  })
}

/**
 * 更新交易记录
 * @param {number} id - 交易记录ID
 * @param {Object} data - 交易记录数据
 * @returns {Promise} 更新结果响应
 */
export function updateTrade(id, data) {
  return request({
    url: `/api/trades/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除交易记录
 * @param {number} id - 交易记录ID
 * @returns {Promise} 删除结果响应
 */
export function deleteTrade(id) {
  return request({
    url: `/api/trades/${id}`,
    method: 'delete'
  })
}

/**
 * 根据组合ID查询交易记录
 * @param {number} portfolioId - 组合ID
 * @returns {Promise} 交易记录列表响应
 */
export function getTradesByPortfolioId(portfolioId) {
  return request({
    url: `/trades/portfolio/${portfolioId}`,
    method: 'get'
  })
}

/**
 * 批量创建交易记录
 * @param {Array} data - 交易记录数据数组
 * @returns {Promise} 创建结果响应
 */
export function batchCreateTrades(data) {
  return request({
    url: '/api/trades/batch',
    method: 'post',
    data
  })
}

export function getRebalancePlan(id) {
  return request({
    url: `/api/trades/rebalance/${id}`,
    method: 'get'
  })
}

export function executeRebalance(data) {
  return request({
    url: '/api/trades/rebalance/execute',
    method: 'post',
    data
  })
}

// 获取交易执行列表
export const getExecutionList = (params) => {
  return request({
    url: '/api/trades/execution/list',
    method: 'get',
    params
  })
}

// 开始执行交易
export const startExecution = (data) => {
  return request({
    url: '/api/trades/execution/start',
    method: 'post',
    data
  })
}

// 执行单个订单
export const executeOrder = (orderId) => {
  return request({
    url: `/api/trades/execution/execute/${orderId}`,
    method: 'post'
  })
}

// 获取执行统计
export const getExecutionStats = () => {
  return request({
    url: '/api/trades/execution/stats',
    method: 'get'
  })
} 