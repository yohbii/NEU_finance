<template>
  <div class="trade-execution-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/trade/rebalance' }">交易管理</el-breadcrumb-item>
        <el-breadcrumb-item>交易执行</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="execution-content">
      <!-- 执行状态卡片 -->
      <el-row :gutter="20" class="status-cards">
        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-item">
              <div class="status-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-value">{{ executionStats.pending }}</div>
                <div class="status-label">待执行</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-item">
              <div class="status-icon executing">
                <el-icon><Loading /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-value">{{ executionStats.executing }}</div>
                <div class="status-label">执行中</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-item">
              <div class="status-icon completed">
                <el-icon><Check /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-value">{{ executionStats.completed }}</div>
                <div class="status-label">已完成</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="status-card">
            <div class="status-item">
              <div class="status-icon failed">
                <el-icon><Close /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-value">{{ executionStats.failed }}</div>
                <div class="status-label">执行失败</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 交易执行控制面板 -->
      <el-card class="control-panel" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><Operation /></el-icon> 交易执行控制</h3>
            <div class="control-actions">
              <el-button type="success" @click="startExecution" :loading="executing">
                {{ executing ? '执行中...' : '开始执行' }}
              </el-button>
              <el-button type="warning" @click="pauseExecution" :disabled="!executing">
                暂停执行
              </el-button>
              <el-button type="danger" @click="stopExecution" :disabled="!executing">
                停止执行
              </el-button>
            </div>
          </div>
        </template>

        <div class="execution-config">
          <el-form :model="executionConfig" label-width="120px">
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="执行模式">
                  <el-select v-model="executionConfig.mode" placeholder="选择执行模式" style="width: 100%">
                    <el-option label="自动执行" value="auto" />
                    <el-option label="手动确认" value="manual" />
                    <el-option label="批量执行" value="batch" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="执行间隔">
                  <el-input-number
                    v-model="executionConfig.interval"
                    :min="1"
                    :max="60"
                    placeholder="秒"
                    style="width: 100%"
                  />
                  <span style="margin-left: 8px; color: #666;">秒</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="最大并发数">
                  <el-input-number
                    v-model="executionConfig.maxConcurrency"
                    :min="1"
                    :max="10"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </el-card>

      <!-- 交易执行列表 -->
      <el-card class="execution-list" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><List /></el-icon> 交易执行列表</h3>
            <div class="list-actions">
              <el-select v-model="filterStatus" placeholder="执行状态" style="width: 150px;">
                <el-option label="全部" value="" />
                <el-option label="待执行" value="pending" />
                <el-option label="执行中" value="executing" />
                <el-option label="已完成" value="completed" />
                <el-option label="执行失败" value="failed" />
              </el-select>
              <el-button @click="loadExecutionList" :icon="RefreshRight">刷新</el-button>
            </div>
          </div>
        </template>

        <el-table
          :data="executionList"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="tradeNo" label="订单号" width="180" />
          <el-table-column prop="portfolioId" label="组合" width="150">
            <template #default="{ row }">
              组合{{ row.portfolioId }}
            </template>
          </el-table-column>
          <el-table-column prop="assetCode" label="资产代码" width="120" />
          <el-table-column label="资产名称" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              {{ getFundName(row.assetCode) }}
            </template>
          </el-table-column>
          <el-table-column prop="tradeType" label="交易类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.tradeType === 1 ? 'success' : 'danger'">
                {{ row.tradeType === 1 ? '买入' : '卖出' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="tradeAmount" label="目标金额" width="120">
            <template #default="{ row }">
              {{ formatCurrency(row.tradeAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="执行进度" width="120">
            <template #default="{ row }">
              <el-progress :percentage="getProgress(row)" :stroke-width="8" />
            </template>
          </el-table-column>
          <el-table-column prop="tradeStatus" label="执行状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.tradeStatus)">
                {{ getStatusText(row.tradeStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewDetail(row)">
                详情
              </el-button>
              <el-button 
                v-if="row.tradeStatus === 0" 
                type="success" 
                size="small" 
                @click="executeOrder(row)"
              >
                执行
              </el-button>
              <el-button 
                v-if="row.tradeStatus === 3" 
                type="danger" 
                size="small" 
                @click="handleError(row)"
              >
                处理
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="交易执行详情"
      width="800px"
      :before-close="closeDetail"
    >
      <div v-if="currentTrade" class="trade-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="交易编号">
            {{ currentTrade.tradeNo }}
          </el-descriptions-item>
          <el-descriptions-item label="组合ID">
            组合{{ currentTrade.portfolioId }}
          </el-descriptions-item>
          <el-descriptions-item label="资产代码">
            {{ currentTrade.assetCode }}
          </el-descriptions-item>
          <el-descriptions-item label="资产名称">
            {{ getFundName(currentTrade.assetCode) }}
          </el-descriptions-item>
          <el-descriptions-item label="交易类型">
            <el-tag :type="currentTrade.tradeType === 1 ? 'success' : 'danger'">
              {{ currentTrade.tradeType === 1 ? '买入' : '卖出' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="交易状态">
            <el-tag :type="getStatusType(currentTrade.tradeStatus)">
              {{ getStatusText(currentTrade.tradeStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="交易金额">
            {{ formatCurrency(currentTrade.tradeAmount) }}
          </el-descriptions-item>
          <el-descriptions-item label="交易份额">
            {{ currentTrade.tradeShares?.toLocaleString() || '0' }}
          </el-descriptions-item>
          <el-descriptions-item label="交易价格">
            ¥{{ currentTrade.tradePrice?.toFixed(4) || '0.0000' }}
          </el-descriptions-item>
          <el-descriptions-item label="交易费用">
            {{ formatCurrency(currentTrade.tradeFee) }}
          </el-descriptions-item>
          <el-descriptions-item label="交易日期">
            {{ currentTrade.tradeDate }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(currentTrade.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ currentTrade.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="execution-timeline" style="margin-top: 20px;">
          <h4>执行时间线</h4>
          <el-timeline>
            <el-timeline-item
              timestamp="2024-01-01 10:00:00"
              type="primary"
            >
              订单创建
            </el-timeline-item>
            <el-timeline-item
              timestamp="2024-01-01 10:05:00"
              type="success"
            >
              风控检查通过
            </el-timeline-item>
            <el-timeline-item
              v-if="currentTrade.tradeStatus === 1"
              timestamp="2024-01-01 10:10:00"
              type="success"
            >
              交易执行完成
            </el-timeline-item>
            <el-timeline-item
              v-else
              timestamp="--"
              type="info"
            >
              等待执行
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDetail">关闭</el-button>
          <el-button 
            v-if="currentTrade?.tradeStatus === 0" 
            type="success" 
            @click="executeCurrentOrder"
          >
            立即执行
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Clock,
  Loading,
  Check,
  Close,
  Operation,
  List,
  RefreshRight
} from '@element-plus/icons-vue'
import { getExecutionList, startExecution as startExecutionAPI, executeOrder as executeOrderAPI, getExecutionStats } from '@/api/trade'

// 响应式数据
const loading = ref(false)
const executing = ref(false)
const filterStatus = ref('')
const detailVisible = ref(false)
const currentTrade = ref(null)

// 执行统计
const executionStats = reactive({
  pending: 15,
  executing: 3,
  completed: 128,
  failed: 2
})

// 执行配置
const executionConfig = reactive({
  mode: 'auto',
  interval: 5,
  maxConcurrency: 3
})

// 执行列表
const executionList = ref([])

// 加载执行列表
const loadExecutionList = async () => {
  try {
    loading.value = true
    
    const params = {
      status: filterStatus.value
    }
    
    const response = await getExecutionList(params)
    if (response.code === 200) {
      executionList.value = response.data.records || []
    }
    
  } catch (error) {
    console.error('加载执行列表失败:', error)
    ElMessage.error('加载执行列表失败')
  } finally {
    loading.value = false
  }
}

// 加载执行统计
const loadExecutionStats = async () => {
  try {
    const response = await getExecutionStats()
    if (response.code === 200) {
      Object.assign(executionStats, response.data)
    }
  } catch (error) {
    console.error('加载执行统计失败:', error)
  }
}

// 开始执行
const startExecution = async () => {
  try {
    executing.value = true
    ElMessage.info('开始执行交易订单...')
    
    const data = {
      mode: executionConfig.mode,
      interval: executionConfig.interval,
      maxConcurrency: executionConfig.maxConcurrency
    }
    
    const response = await startExecutionAPI(data)
    if (response.code === 200) {
      ElMessage.success('交易执行已启动')
      loadExecutionList()
      loadExecutionStats()
    }
    
  } catch (error) {
    console.error('启动执行失败:', error)
    ElMessage.error('启动执行失败')
  } finally {
    executing.value = false
  }
}

// 暂停执行
const pauseExecution = () => {
  executing.value = false
  ElMessage.info('交易执行已暂停')
}

// 停止执行
const stopExecution = () => {
  executing.value = false
  ElMessage.warning('交易执行已停止')
}

// 执行单个订单
const executeOrder = async (order) => {
  try {
    const response = await executeOrderAPI(order.id)
    if (response.code === 200) {
      ElMessage.info(`开始执行订单 ${order.orderNo}`)
      loadExecutionList()
    }
  } catch (error) {
    console.error('执行订单失败:', error)
    ElMessage.error('执行订单失败')
  }
}

// 查看详情
const viewDetail = (trade) => {
  currentTrade.value = trade
  detailVisible.value = true
}

// 关闭详情
const closeDetail = () => {
  detailVisible.value = false
  currentTrade.value = null
}

// 执行当前订单
const executeCurrentOrder = async () => {
  if (currentTrade.value) {
    await executeOrder(currentTrade.value)
    closeDetail()
  }
}

// 处理错误
const handleError = (order) => {
  ElMessage.info('差错处理功能开发中...')
}

// 工具方法
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',    // 待执行
    1: 'success',    // 已成交
    2: 'primary',    // 部分成交
    3: 'danger'      // 已撤销
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '待执行',
    1: '已成交',
    2: '部分成交',
    3: '已撤销'
  }
  return textMap[status] || '未知'
}

const getProgress = (trade) => {
  // 根据交易状态计算进度
  if (trade.tradeStatus === 1) return 100  // 已成交
  if (trade.tradeStatus === 2) return 50   // 部分成交
  if (trade.tradeStatus === 3) return 0    // 已撤销
  return 0  // 待执行
}

const getFundName = (assetCode) => {
  // 简单的基金名称映射，实际应该从基金信息中获取
  const fundNames = {
    '000001': '华夏成长混合',
    '000002': '易方达价值精选',
    '000003': '南方优选成长',
    '000004': '嘉实稳健收益'
  }
  return fundNames[assetCode] || `基金${assetCode}`
}

const formatCurrency = (amount) => {
  return `¥${amount?.toLocaleString() || '0'}`
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadExecutionList()
  loadExecutionStats()
})
</script>

<style scoped>
.trade-execution-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.status-cards {
  margin-bottom: 20px;
}

.status-card {
  border-radius: 8px;
  overflow: hidden;
}

.status-item {
  display: flex;
  align-items: center;
  padding: 10px;
}

.status-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.status-icon.pending {
  background: #E6A23C;
}

.status-icon.executing {
  background: #409EFF;
}

.status-icon.completed {
  background: #67C23A;
}

.status-icon.failed {
  background: #F56C6C;
}

.status-info {
  flex: 1;
}

.status-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.status-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.control-panel,
.execution-list {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-actions,
.list-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.execution-config {
  margin-top: 16px;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}
</style> 