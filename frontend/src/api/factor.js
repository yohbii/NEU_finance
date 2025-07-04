import request from '@/utils/request'

/**
 * 分页查询因子列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 因子列表响应
 */
export function getFactorList(params) {
  return request({
    url: '/api/factors',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取因子详情
 * @param {number} id - 因子ID
 * @returns {Promise} 因子详情响应
 */
export function getFactorDetail(id) {
  return request({
    url: `/api/factors/${id}`,
    method: 'get'
  })
}

/**
 * 根据因子代码获取因子详情
 * @param {string} factorCode - 因子代码
 * @returns {Promise} 因子详情响应
 */
export function getFactorByCode(factorCode) {
  return request({
    url: `/factors/code/${factorCode}`,
    method: 'get'
  })
}

/**
 * 创建因子
 * @param {Object} data - 因子数据
 * @returns {Promise} 创建结果响应
 */
export function createFactor(data) {
  return request({
    url: '/api/factors',
    method: 'post',
    data
  })
}

/**
 * 更新因子
 * @param {number} id - 因子ID
 * @param {Object} data - 因子数据
 * @returns {Promise} 更新结果响应
 */
export function updateFactor(id, data) {
  return request({
    url: `/api/factors/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除因子
 * @param {number} id - 因子ID
 * @returns {Promise} 删除结果响应
 */
export function deleteFactor(id) {
  return request({
    url: `/api/factors/${id}`,
    method: 'delete'
  })
}

/**
 * 获取因子筛选选项
 * @returns {Promise} 筛选选项响应
 */
export function getFactorOptions() {
  return request({
    url: '/api/factors/options',
    method: 'get'
  })
}

// 执行因子分析
export const runFactorAnalysis = (data) => {
  return request({
    url: '/api/factors/analysis/run',
    method: 'post',
    data
  })
}

// 获取因子分析结果
export const getAnalysisResult = (analysisId) => {
  return request({
    url: `/api/factors/analysis/result/${analysisId}`,
    method: 'get'
  })
} 