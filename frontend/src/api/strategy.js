import request from '@/utils/request'

/**
 * 分页查询策略列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 策略列表响应
 */
export function getStrategyList(params) {
  return request({
    url: '/api/strategies',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取策略详情
 * @param {number} id - 策略ID
 * @returns {Promise} 策略详情响应
 */
export function getStrategyDetail(id) {
  return request({
    url: `/api/strategies/${id}`,
    method: 'get'
  })
}

/**
 * 创建策略
 * @param {Object} data - 策略数据
 * @returns {Promise} 创建结果响应
 */
export function createStrategy(data) {
  return request({
    url: '/api/strategies',
    method: 'post',
    data
  })
}

/**
 * 更新策略
 * @param {number} id - 策略ID
 * @param {Object} data - 策略数据
 * @returns {Promise} 更新结果响应
 */
export function updateStrategy(id, data) {
  return request({
    url: `/api/strategies/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除策略
 * @param {number} id - 策略ID
 * @returns {Promise} 删除结果响应
 */
export function deleteStrategy(id) {
  return request({
    url: `/api/strategies/${id}`,
    method: 'delete'
  })
}

/**
 * 获取策略持仓列表
 * @param {number} id - 策略ID
 * @returns {Promise} 持仓列表响应
 */
export function getStrategyHoldings(id) {
  return request({
    url: `/api/strategies/${id}/holdings`,
    method: 'get'
  })
}

/**
 * 更新策略持仓
 * @param {number} id - 策略ID
 * @param {Array} data - 持仓数据
 * @returns {Promise} 更新结果响应
 */
export function updateStrategyHoldings(id, data) {
  return request({
    url: `/api/strategies/${id}/holdings`,
    method: 'put',
    data
  })
}

export function getStrategyOptions() {
  return request({
    url: '/api/strategies/options',
    method: 'get'
  })
}

// 执行策略回测
export const runBacktest = (data) => {
  return request({
    url: '/strategy/backtest/run',
    method: 'post',
    data
  })
}

// 获取回测结果
export const getBacktestResult = (backtestId) => {
  return request({
    url: `/strategy/backtest/result/${backtestId}`,
    method: 'get'
  })
}

// 保存回测结果
export const saveBacktest = (data) => {
  return request({
    url: '/strategy/backtest/save',
    method: 'post',
    data
  })
} 