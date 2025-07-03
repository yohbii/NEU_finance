<template>
  <div class="strategy-backtest-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/strategy/list' }">策略管理</el-breadcrumb-item>
        <el-breadcrumb-item>策略回测</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="backtest-content">
      <!-- 策略信息卡片 -->
      <el-card class="strategy-info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><TrendCharts /></el-icon> 策略信息</h3>
          </div>
        </template>
        
        <div v-if="strategyInfo" class="strategy-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="策略名称">
              {{ strategyInfo.strategyName }}
            </el-descriptions-item>
            <el-descriptions-item label="策略类型">
              <el-tag>{{ strategyInfo.strategyType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="风险等级">
              <el-rate v-model="strategyInfo.riskLevel" disabled show-score />
            </el-descriptions-item>
            <el-descriptions-item label="目标收益率">
              {{ formatPercentage(strategyInfo.targetReturn) }}
            </el-descriptions-item>
            <el-descriptions-item label="再平衡频率">
              {{ strategyInfo.rebalanceFrequency }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDateTime(strategyInfo.createdAt) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 回测配置卡片 -->
      <el-card class="backtest-config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><Setting /></el-icon> 回测配置</h3>
            <el-button 
              type="primary" 
              @click="runBacktest" 
              :loading="backtesting"
              :disabled="!isConfigValid"
            >
              {{ backtesting ? '回测中...' : '开始回测' }}
            </el-button>
          </div>
        </template>
        
        <el-form :model="backtestConfig" :rules="configRules" ref="configFormRef" label-width="120px">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="回测开始日期" prop="startDate">
                <el-date-picker
                  v-model="backtestConfig.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="回测结束日期" prop="endDate">
                <el-date-picker
                  v-model="backtestConfig.endDate"
                  type="date"
                  placeholder="选择结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="初始资金" prop="initialCapital">
                <el-input-number
                  v-model="backtestConfig.initialCapital"
                  :min="10000"
                  :max="100000000"
                  :step="10000"
                  style="width: 100%"
                />
                <span style="margin-left: 8px; color: #666;">元</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="基准指数" prop="benchmarkCode">
                <el-select
                  v-model="backtestConfig.benchmarkCode"
                  placeholder="选择基准指数"
                  style="width: 100%"
                >
                  <el-option
                    v-for="benchmark in benchmarkOptions"
                    :key="benchmark.value"
                    :value="benchmark.value"
                    :label="benchmark.label"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="再平衡频率">
                <el-select
                  v-model="backtestConfig.rebalanceFreq"
                  placeholder="选择再平衡频率"
                  style="width: 100%"
                >
                  <el-option label="日度" value="daily" />
                  <el-option label="周度" value="weekly" />
                  <el-option label="月度" value="monthly" />
                  <el-option label="季度" value="quarterly" />
                  <el-option label="年度" value="yearly" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易费率">
                <el-input-number
                  v-model="backtestConfig.tradingFeeRate"
                  :min="0"
                  :max="0.01"
                  :precision="4"
                  :step="0.0001"
                  style="width: 100%"
                />
                <span style="margin-left: 8px; color: #666;">%</span>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 回测结果卡片 -->
      <el-card v-if="backtestResult" class="backtest-result-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><DataAnalysis /></el-icon> 回测结果</h3>
            <div class="result-actions">
              <el-button type="success" @click="exportResult" :icon="Download">
                导出报告
              </el-button>
              <el-button type="info" @click="saveBacktest" :icon="Collection">
                保存回测
              </el-button>
            </div>
          </div>
        </template>
        
        <!-- 关键指标 -->
        <div class="key-metrics">
          <el-row :gutter="24">
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">总收益率</div>
                <div class="metric-value" :class="getReturnClass(backtestResult.totalReturn)">
                  {{ formatPercentage(backtestResult.totalReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">年化收益率</div>
                <div class="metric-value" :class="getReturnClass(backtestResult.annualReturn)">
                  {{ formatPercentage(backtestResult.annualReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">最大回撤</div>
                <div class="metric-value negative">
                  {{ formatPercentage(backtestResult.maxDrawdown) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">夏普比率</div>
                <div class="metric-value">
                  {{ backtestResult.sharpeRatio.toFixed(4) }}
                </div>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="24" style="margin-top: 20px;">
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">波动率</div>
                <div class="metric-value">
                  {{ formatPercentage(backtestResult.volatility) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">胜率</div>
                <div class="metric-value">
                  {{ formatPercentage(backtestResult.winRate) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">信息比率</div>
                <div class="metric-value">
                  {{ backtestResult.informationRatio.toFixed(4) }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-item">
                <div class="metric-label">卡玛比率</div>
                <div class="metric-value">
                  {{ backtestResult.calmarRatio.toFixed(4) }}
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <!-- 净值走势图 -->
        <el-divider />
        <div class="chart-section">
          <h4>净值走势对比</h4>
          <div ref="chartContainer" class="chart-container"></div>
        </div>
        
        <!-- 详细分析 -->
        <el-divider />
        <div class="detailed-analysis">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="持仓分析" name="holdings">
              <el-table :data="backtestResult.holdings" border>
                <el-table-column prop="assetCode" label="资产代码" width="120" />
                <el-table-column prop="assetName" label="资产名称" min-width="200" />
                <el-table-column prop="weight" label="权重" width="100">
                  <template #default="{ row }">
                    {{ formatPercentage(row.weight) }}
                  </template>
                </el-table-column>
                <el-table-column prop="return" label="收益贡献" width="120">
                  <template #default="{ row }">
                    <span :class="getReturnClass(row.return)">
                      {{ formatPercentage(row.return) }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="交易记录" name="trades">
              <el-table :data="backtestResult.trades" border max-height="400">
                <el-table-column prop="date" label="交易日期" width="120" />
                <el-table-column prop="assetCode" label="资产代码" width="120" />
                <el-table-column prop="type" label="交易类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.type === 'buy' ? 'success' : 'danger'">
                      {{ row.type === 'buy' ? '买入' : '卖出' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="shares" label="交易份额" width="120" />
                <el-table-column prop="price" label="交易价格" width="120" />
                <el-table-column prop="amount" label="交易金额" width="120" />
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="风险分析" name="risk">
              <div class="risk-analysis">
                <el-row :gutter="24">
                  <el-col :span="12">
                    <div class="risk-chart" ref="riskChartContainer"></div>
                  </el-col>
                  <el-col :span="12">
                    <div class="risk-metrics">
                      <el-descriptions :column="1" border>
                        <el-descriptions-item label="VaR (95%)">
                          {{ formatPercentage(backtestResult.var95) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="CVaR (95%)">
                          {{ formatPercentage(backtestResult.cvar95) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="下行波动率">
                          {{ formatPercentage(backtestResult.downsideVolatility) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="索提诺比率">
                          {{ backtestResult.sortinoRatio.toFixed(4) }}
                        </el-descriptions-item>
                      </el-descriptions>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  TrendCharts,
  Setting,
  DataAnalysis,
  Download,
  Collection
} from '@element-plus/icons-vue'
import { getStrategyDetail, runBacktest, saveBacktest as saveBacktestAPI } from '@/api/strategy'

// 路由参数
const route = useRoute()
const strategyId = route.params.id

// 响应式数据
const backtesting = ref(false)
const strategyInfo = ref(null)
const backtestResult = ref(null)
const activeTab = ref('holdings')
const configFormRef = ref(null)
const chartContainer = ref(null)
const riskChartContainer = ref(null)

// 回测配置
const backtestConfig = reactive({
  startDate: '',
  endDate: '',
  initialCapital: 1000000,
  benchmarkCode: 'CSI300',
  rebalanceFreq: 'monthly',
  tradingFeeRate: 0.0015
})

// 表单验证规则
const configRules = {
  startDate: [
    { required: true, message: '请选择回测开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择回测结束日期', trigger: 'change' }
  ],
  initialCapital: [
    { required: true, message: '请输入初始资金', trigger: 'blur' }
  ],
  benchmarkCode: [
    { required: true, message: '请选择基准指数', trigger: 'change' }
  ]
}

// 基准指数选项
const benchmarkOptions = ref([
  { label: '沪深300指数', value: 'CSI300' },
  { label: '中证500指数', value: 'CSI500' },
  { label: '创业板指数', value: 'SZSE100' },
  { label: '上证50指数', value: 'SSE50' },
  { label: '中证1000指数', value: 'CSI1000' }
])

// 计算属性
const isConfigValid = computed(() => {
  return backtestConfig.startDate && 
         backtestConfig.endDate && 
         backtestConfig.initialCapital > 0 &&
         backtestConfig.benchmarkCode
})

// 加载策略信息
const loadStrategyInfo = async () => {
  try {
    const response = await getStrategyDetail(strategyId)
    if (response.code === 200) {
      strategyInfo.value = response.data
      
      // 设置默认回测时间范围
      const endDate = new Date()
      const startDate = new Date()
      startDate.setFullYear(endDate.getFullYear() - 1)
      
      backtestConfig.endDate = endDate.toISOString().split('T')[0]
      backtestConfig.startDate = startDate.toISOString().split('T')[0]
    }
  } catch (error) {
    console.error('加载策略信息失败:', error)
    ElMessage.error('加载策略信息失败')
  }
}

// 执行回测
const runBacktest = async () => {
  try {
    // 表单验证
    const valid = await configFormRef.value.validate()
    if (!valid) return
    
    backtesting.value = true
    ElMessage.info('开始执行回测，请稍候...')
    
    const data = {
      strategyId: strategyId,
      startDate: backtestConfig.startDate,
      endDate: backtestConfig.endDate,
      initialCapital: backtestConfig.initialCapital,
      benchmarkCode: backtestConfig.benchmarkCode,
      rebalanceFreq: backtestConfig.rebalanceFreq,
      tradingFeeRate: backtestConfig.tradingFeeRate
    }
    
    const response = await runBacktest(data)
    if (response.code === 200) {
      backtestResult.value = response.data
      
      await nextTick()
      initCharts()
      
      ElMessage.success('回测完成！')
    }
    
  } catch (error) {
    console.error('回测执行失败:', error)
    ElMessage.error('回测执行失败')
  } finally {
    backtesting.value = false
  }
}

// 初始化图表
const initCharts = () => {
  if (typeof echarts === 'undefined') {
    console.warn('ECharts not loaded, charts will not be displayed')
    return
  }
  
  // 净值走势图
  if (chartContainer.value) {
    const chart = echarts.init(chartContainer.value)
    
    // 模拟净值数据
    const dates = []
    const strategyValues = []
    const benchmarkValues = []
    
    for (let i = 0; i < 365; i++) {
      const date = new Date(backtestConfig.startDate)
      date.setDate(date.getDate() + i)
      dates.push(date.toISOString().split('T')[0])
      
      strategyValues.push((1 + Math.random() * 0.001 - 0.0005) * (strategyValues[i-1] || 1))
      benchmarkValues.push((1 + Math.random() * 0.0008 - 0.0004) * (benchmarkValues[i-1] || 1))
    }
    
    const option = {
      title: {
        text: '策略净值走势对比',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['策略净值', '基准净值'],
        bottom: 10
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: dates,
        axisLabel: {
          formatter: function (value) {
            return value.split('-').slice(1).join('/')
          }
        }
      },
      yAxis: {
        type: 'value',
        scale: true
      },
      series: [
        {
          name: '策略净值',
          type: 'line',
          data: strategyValues,
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#409EFF' }
        },
        {
          name: '基准净值',
          type: 'line',
          data: benchmarkValues,
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#67C23A' }
        }
      ]
    }
    
    chart.setOption(option)
  }
}

// 导出回测报告
const exportResult = () => {
  ElMessage.success('回测报告导出功能开发中...')
}

// 保存回测
const saveBacktest = async () => {
  try {
    if (!backtestResult.value) {
      ElMessage.warning('没有回测结果可保存')
      return
    }
    
    const data = {
      strategyId: strategyId,
      config: backtestConfig,
      result: backtestResult.value
    }
    
    const response = await saveBacktestAPI(data)
    if (response.code === 200) {
      ElMessage.success('回测结果已保存')
    }
    
  } catch (error) {
    console.error('保存回测失败:', error)
    ElMessage.error('保存回测失败')
  }
}

// 格式化百分比
const formatPercentage = (value) => {
  if (value === null || value === undefined) return '--'
  return `${(value * 100).toFixed(2)}%`
}

// 获取收益率样式类
const getReturnClass = (value) => {
  if (value === null || value === undefined) return ''
  return value >= 0 ? 'positive' : 'negative'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadStrategyInfo()
})
</script>

<style scoped>
.strategy-backtest-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.backtest-content {
  max-width: 1400px;
  margin: 0 auto;
}

.strategy-info-card,
.backtest-config-card,
.backtest-result-card {
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

.result-actions {
  display: flex;
  gap: 12px;
}

.key-metrics {
  margin-bottom: 24px;
}

.metric-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  background: white;
}

.metric-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.metric-value.positive {
  color: #67C23A;
}

.metric-value.negative {
  color: #F56C6C;
}

.chart-section h4 {
  margin-bottom: 16px;
  color: #303133;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.risk-chart {
  height: 300px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
}

.risk-metrics {
  height: 300px;
  overflow-y: auto;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: bold;
}
</style> 