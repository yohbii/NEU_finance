import request from '@/utils/request'

/**
 * 分页查询基金列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 基金列表响应
 */
export function getFundList(params) {
  return request({
    url: '/api/funds',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取基金详情
 * @param {number} id - 基金ID
 * @returns {Promise} 基金详情响应
 */
export function getFundById(id) {
  return request({
    url: `/api/funds/${id}`,
    method: 'get'
  })
}

/**
 * 根据基金代码获取基金详情
 * @param {string} fundCode - 基金代码
 * @returns {Promise} 基金详情响应
 */
export function getFundByCode(fundCode) {
  return request({
    url: `/api/funds/code/${fundCode}`,
    method: 'get'
  })
}

/**
 * 创建基金
 * @param {Object} data - 基金数据
 * @returns {Promise} 创建结果响应
 */
export function createFund(data) {
  return request({
    url: '/api/funds',
    method: 'post',
    data
  })
}

/**
 * 更新基金
 * @param {number} id - 基金ID
 * @param {Object} data - 基金数据
 * @returns {Promise} 更新结果响应
 */
export function updateFund(id, data) {
  return request({
    url: `/api/funds/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除基金
 * @param {number} id - 基金ID
 * @returns {Promise} 删除结果响应
 */
export function deleteFund(id) {
  return request({
    url: `/api/funds/${id}`,
    method: 'delete'
  })
}

/**
 * 获取基金筛选选项
 * @returns {Promise} 筛选选项响应
 */
export function getFundFilterOptions() {
  return request({
    url: '/api/funds/options',
    method: 'get'
  })
}

export function getFundDetail(fundId) {
  return request({
    url: `/api/funds/${fundId}/detail`,
    method: 'get'
  })
}

export function getFundOptions() {
  return request({
    url: '/api/funds/options',
    method: 'get'
  })
}

export function getFundNetValue(fundId, params) {
  return request({
    url: `/api/funds/${fundId}/net-value`,
    method: 'get',
    params
  })
}

export function getFundPerformance(fundId) {
  return request({
    url: `/api/funds/${fundId}/performance`,
    method: 'get'
  })
}

export function compareFunds(fundCodes) {
  return request({
    url: '/api/funds/compare',
    method: 'post',
    data: { fundCodes }
  })
}

export function searchFunds(params) {
  return request({
    url: '/api/funds/search',
    method: 'get',
    params
  })
}

// 获取基金公司列表
export function getFundCompanies() {
  return request({
    url: '/api/funds/companies',
    method: 'get'
  })
}

// 获取基金经理列表
export function getFundManagers() {
  return request({
    url: '/api/funds/managers',
    method: 'get'
  })
} 