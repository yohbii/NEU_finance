<template>
  <div class="fund-detail-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/fund/list' }">基金管理</el-breadcrumb-item>
        <el-breadcrumb-item>基金详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div v-loading="loading" class="fund-detail-content">
      <template v-if="fundInfo">
        <!-- 基金基本信息卡片 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="fund-title">
                <h2>{{ fundInfo.fundName }}</h2>
                <el-tag type="primary" size="large">{{ fundInfo.fundCode }}</el-tag>
              </div>
              <div class="fund-actions">
                <el-button type="primary" :icon="Star">关注</el-button>
                <el-button type="success" :icon="ShoppingCart">投资</el-button>
              </div>
            </div>
          </template>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="基金类型">
                  <el-tag>{{ fundInfo.fundType }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="基金公司">
                  {{ fundInfo.fundCompany }}
                </el-descriptions-item>
                <el-descriptions-item label="基金经理">
                  {{ fundInfo.fundManager || '暂无' }}
                </el-descriptions-item>
                <el-descriptions-item label="成立日期">
                  {{ fundInfo.establishDate || '暂无' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-col>
            <el-col :span="12">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="单位净值">
                  <span class="net-value">{{ fundInfo.unitNetValue || '--' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="累计净值">
                  <span class="net-value">{{ fundInfo.accumulatedNetValue || '--' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="风险等级">
                  <el-rate v-model="fundInfo.riskLevel" disabled show-score text-color="#ff9900" />
                </el-descriptions-item>
                <el-descriptions-item label="最小投资">
                  {{ fundInfo.minInvestment ? `${fundInfo.minInvestment}元` : '暂无' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
          
          <el-divider />
          
          <div class="fund-description">
            <h4>基金简介</h4>
            <p>{{ fundInfo.description || '暂无基金简介' }}</p>
          </div>
        </el-card>

        <!-- 业绩表现卡片 -->
        <el-card class="performance-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><TrendCharts /></el-icon> 业绩表现</h3>
            </div>
          </template>
          
          <el-row :gutter="24" v-if="performanceData">
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">年初至今</div>
                <div class="performance-value" :class="getReturnClass(performanceData.ytdReturn)">
                  {{ formatPercentage(performanceData.ytdReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">近1月</div>
                <div class="performance-value" :class="getReturnClass(performanceData.oneMonthReturn)">
                  {{ formatPercentage(performanceData.oneMonthReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">近3月</div>
                <div class="performance-value" :class="getReturnClass(performanceData.threeMonthReturn)">
                  {{ formatPercentage(performanceData.threeMonthReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">近1年</div>
                <div class="performance-value" :class="getReturnClass(performanceData.oneYearReturn)">
                  {{ formatPercentage(performanceData.oneYearReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">近3年</div>
                <div class="performance-value" :class="getReturnClass(performanceData.threeYearReturn)">
                  {{ formatPercentage(performanceData.threeYearReturn) }}
                </div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="performance-item">
                <div class="performance-label">最大回撤</div>
                <div class="performance-value negative">
                  {{ formatPercentage(performanceData.maxDrawdown) }}
                </div>
              </div>
            </el-col>
          </el-row>
          
          <el-empty v-else description="暂无业绩数据" />
        </el-card>

        <!-- 风险指标卡片 -->
        <el-card class="risk-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><Warning /></el-icon> 风险指标</h3>
            </div>
          </template>
          
          <el-row :gutter="24" v-if="performanceData">
            <el-col :span="8">
              <div class="risk-item">
                <div class="risk-label">夏普比率</div>
                <div class="risk-value">
                  {{ performanceData.sharpeRatio ? performanceData.sharpeRatio.toFixed(4) : '--' }}
                </div>
                <div class="risk-desc">风险调整后收益</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="risk-item">
                <div class="risk-label">波动率</div>
                <div class="risk-value">
                  {{ formatPercentage(performanceData.volatility) }}
                </div>
                <div class="risk-desc">收益波动程度</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="risk-item">
                <div class="risk-label">最大回撤</div>
                <div class="risk-value negative">
                  {{ formatPercentage(performanceData.maxDrawdown) }}
                </div>
                <div class="risk-desc">最大损失幅度</div>
              </div>
            </el-col>
          </el-row>
          
          <el-empty v-else description="暂无风险数据" />
        </el-card>

        <!-- 净值走势图表 -->
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><LineChart /></el-icon> 净值走势</h3>
              <div class="chart-controls">
                <el-radio-group v-model="chartPeriod" @change="loadNetValueData">
                  <el-radio-button label="1M">近1月</el-radio-button>
                  <el-radio-button label="3M">近3月</el-radio-button>
                  <el-radio-button label="6M">近6月</el-radio-button>
                  <el-radio-button label="1Y">近1年</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          
          <div ref="chartContainer" class="chart-container"></div>
        </el-card>
      </template>
      
      <el-empty v-else-if="!loading" description="基金信息不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Star, 
  ShoppingCart, 
  TrendCharts, 
  Warning, 
  LineChart 
} from '@element-plus/icons-vue'
import { getFundById, getFundDetail, getFundPerformance, getFundNetValue } from '@/api/fund'
import * as echarts from 'echarts'

// 路由参数
const route = useRoute()
const fundId = route.params.id

// 响应式数据
const loading = ref(false)
const fundInfo = ref(null)
const performanceData = ref(null)
const netValueData = ref([])
const chartContainer = ref(null)
const chartPeriod = ref('3M')
let chart = null

// 加载基金详情
const loadFundDetail = async () => {
  try {
    loading.value = true
    
    const response = await getFundDetail(fundId)
    if (response.code === 200) {
      fundInfo.value = response.data
    }
    
  } catch (error) {
    console.error('加载基金详情失败:', error)
    ElMessage.error('加载基金详情失败')
  } finally {
    loading.value = false
  }
}

// 加载业绩数据
const loadPerformanceData = async () => {
  try {
    const response = await getFundPerformance(fundId)
    if (response.code === 200) {
      performanceData.value = response.data
    }
  } catch (error) {
    console.error('加载业绩数据失败:', error)
  }
}

// 加载净值数据
const loadNetValueData = async (period = '1Y') => {
  try {
    const response = await getFundNetValue(fundId, { period })
    if (response.code === 200) {
      netValueData.value = response.data
      initNetValueChart()
    }
  } catch (error) {
    console.error('加载净值数据失败:', error)
  }
}

// 初始化图表
const initNetValueChart = () => {
  if (!chartContainer.value || !netValueData.value.dates.length) return
  
  if (chart) {
    chart.dispose()
  }
  
  chart = echarts.init(chartContainer.value)
  
  const option = {
    title: {
      text: '单位净值走势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        const point = params[0]
        return `${point.axisValue}<br/>单位净值: ${point.value}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: netValueData.value.dates,
      axisLabel: {
        formatter: function (value) {
          return value.split('-').slice(1).join('/')
        }
      }
    },
    yAxis: {
      type: 'value',
      scale: true,
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '单位净值',
        type: 'line',
        data: netValueData.value.values,
        smooth: true,
        symbol: 'none',
        lineStyle: {
          width: 2,
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式处理
  window.addEventListener('resize', () => {
    if (chart) {
      chart.resize()
    }
  })
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

// 生命周期
onMounted(() => {
  loadFundDetail()
  loadPerformanceData()
  loadNetValueData()
})
</script>

<style scoped>
.fund-detail-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.fund-detail-content {
  max-width: 1200px;
  margin: 0 auto;
}

.info-card, .performance-card, .risk-card, .chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fund-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.fund-title h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.fund-actions {
  display: flex;
  gap: 12px;
}

.net-value {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.fund-description h4 {
  color: #303133;
  margin-bottom: 12px;
}

.fund-description p {
  color: #606266;
  line-height: 1.6;
}

.performance-item {
  text-align: center;
  padding: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  background: white;
}

.performance-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.performance-value {
  font-size: 20px;
  font-weight: bold;
}

.performance-value.positive {
  color: #67C23A;
}

.performance-value.negative {
  color: #F56C6C;
}

.risk-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  background: white;
}

.risk-label {
  font-size: 16px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: bold;
}

.risk-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.risk-value.negative {
  color: #F56C6C;
}

.risk-desc {
  font-size: 12px;
  color: #909399;
}

.chart-controls {
  display: flex;
  gap: 8px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}
</style>