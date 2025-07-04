import request from '@/utils/request'

/**
 * 分页查询产品列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 产品列表响应
 */
export function getProductList(params) {
  return request({
    url: '/api/products',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取产品详情
 * @param {number} id - 产品ID
 * @returns {Promise} 产品详情响应
 */
export function getProductDetail(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

/**
 * 根据产品代码获取产品详情
 * @param {string} productCode - 产品代码
 * @returns {Promise} 产品详情响应
 */
export function getProductByCode(productCode) {
  return request({
    url: `/products/code/${productCode}`,
    method: 'get'
  })
}

/**
 * 创建产品
 * @param {Object} data - 产品数据
 * @returns {Promise} 创建结果响应
 */
export function createProduct(data) {
  return request({
    url: '/api/products',
    method: 'post',
    data
  })
}

/**
 * 更新产品
 * @param {number} id - 产品ID
 * @param {Object} data - 产品数据
 * @returns {Promise} 更新结果响应
 */
export function updateProduct(id, data) {
  return request({
    url: `/api/products/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除产品
 * @param {number} id - 产品ID
 * @returns {Promise} 删除结果响应
 */
export function deleteProduct(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有产品类型
 * @returns {Promise} 产品类型列表响应
 */
export function getProductTypes() {
  return request({
    url: '/api/products/types',
    method: 'get'
  })
}

// 获取产品审核列表
export const getApprovalList = (params) => {
  return request({
    url: '/api/products/approval/list',
    method: 'get',
    params
  })
}

// 提交产品审核
export const submitApproval = (data) => {
  return request({
    url: '/api/products/approval/submit',
    method: 'post',
    data
  })
}

// 获取审核统计
export const getApprovalStats = () => {
  return request({
    url: '/api/products/approval/stats',
    method: 'get'
  })
} 