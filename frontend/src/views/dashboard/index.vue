<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <h3 class="stat-title">基金总数</h3>
              <p class="stat-value">{{ stats.totalFunds }}</p>
              <p class="stat-change positive">
                <el-icon><TrendCharts /></el-icon>
                +12.5%
              </p>
            </div>
            <div class="stat-icon fund-icon">
              <el-icon><Coin /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <h3 class="stat-title">策略数量</h3>
              <p class="stat-value">{{ stats.totalStrategies }}</p>
              <p class="stat-change positive">
                <el-icon><TrendCharts /></el-icon>
                +8.3%
              </p>
            </div>
            <div class="stat-icon strategy-icon">
              <el-icon><DataAnalysis /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <h3 class="stat-title">组合产品</h3>
              <p class="stat-value">{{ stats.totalProducts }}</p>
              <p class="stat-change negative">
                <el-icon><Bottom /></el-icon>
                -2.1%
              </p>
            </div>
            <div class="stat-icon product-icon">
              <el-icon><Box /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <h3 class="stat-title">今日交易</h3>
              <p class="stat-value">{{ stats.todayTrades }}</p>
              <p class="stat-change positive">
                <el-icon><TrendCharts /></el-icon>
                +15.7%
              </p>
            </div>
            <div class="stat-icon trade-icon">
              <el-icon><Money /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>净值走势</span>
          </template>
          <div ref="netValueChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>资产配置</span>
          </template>
          <div ref="assetAllocationChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="16" class="chart-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>收益分析</span>
          </template>
          <div ref="returnAnalysisChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>风险指标</span>
          </template>
          <div ref="riskMetricsChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { 
  TrendCharts, 
  Bottom, 
  Coin, 
  DataAnalysis, 
  Box, 
  Money
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { 
  getDashboardStats, 
  getNetValueTrend, 
  getAssetAllocation, 
  getReturnAnalysis, 
  getRiskMetrics
} from '@/api/dashboard'

// 统计数据
const stats = ref({
  totalFunds: 0,
  totalStrategies: 0,
  totalProducts: 0,
  todayTrades: 0
})

// 图表数据
const netValueData = ref({})
const assetAllocationData = ref([])
const returnAnalysisData = ref({})
const riskMetricsData = ref({})

// 图表引用
const netValueChart = ref(null)
const assetAllocationChart = ref(null)
const returnAnalysisChart = ref(null)
const riskMetricsChart = ref(null)

// 初始化净值走势图
const initNetValueChart = () => {
  const chart = echarts.init(netValueChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['组合净值', '基准指数']
    },
    xAxis: {
      type: 'category',
      data: netValueData.value.months || []
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '组合净值',
        type: 'line',
        data: netValueData.value.portfolioValues || [],
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '基准指数',
        type: 'line',
        data: netValueData.value.benchmarkValues || [],
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化资产配置图
const initAssetAllocationChart = () => {
  const chart = echarts.init(assetAllocationChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}% ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '资产配置',
        type: 'pie',
        radius: '50%',
        data: assetAllocationData.value,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化收益分析图
const initReturnAnalysisChart = () => {
  const chart = echarts.init(returnAnalysisChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['月度收益', '累计收益']
    },
    xAxis: {
      type: 'category',
      data: returnAnalysisData.value.months || []
    },
    yAxis: [
      {
        type: 'value',
        name: '月度收益(%)',
        position: 'left',
        axisLabel: {
          formatter: '{value}%'
        }
      },
      {
        type: 'value',
        name: '累计收益(%)',
        position: 'right',
        axisLabel: {
          formatter: '{value}%'
        }
      }
    ],
    series: [
      {
        name: '月度收益',
        type: 'bar',
        data: returnAnalysisData.value.monthlyReturns || [],
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '累计收益',
        type: 'line',
        yAxisIndex: 1,
        data: returnAnalysisData.value.cumulativeReturns || [],
        itemStyle: {
          color: '#f56c6c'
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化风险指标图
const initRiskMetricsChart = () => {
  const chart = echarts.init(riskMetricsChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    radar: {
      indicator: riskMetricsData.value.indicators || []
    },
    series: [
      {
        name: '风险指标',
        type: 'radar',
        data: riskMetricsData.value.series || []
      }
    ]
  }
  
  chart.setOption(option)
}

// 加载数据的函数
const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statsResponse = await getDashboardStats()
    if (statsResponse.code === 200) {
      stats.value = statsResponse.data
    }

    // 加载净值走势数据
    const netValueResponse = await getNetValueTrend()
    if (netValueResponse.code === 200) {
      netValueData.value = netValueResponse.data
    }

    // 加载资产配置数据
    const assetResponse = await getAssetAllocation()
    if (assetResponse.code === 200) {
      assetAllocationData.value = assetResponse.data
    }

    // 加载收益分析数据
    const returnResponse = await getReturnAnalysis()
    if (returnResponse.code === 200) {
      returnAnalysisData.value = returnResponse.data
    }

    // 加载风险指标数据
    const riskResponse = await getRiskMetrics()
    if (riskResponse.code === 200) {
      riskMetricsData.value = riskResponse.data
    }

    // 重新初始化图表
    nextTick(() => {
      initNetValueChart()
      initAssetAllocationChart()
      initReturnAnalysisChart()
      initRiskMetricsChart()
    })
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
  }
}

// 组件挂载后初始化图表
onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.fund-icon {
  background: linear-gradient(135deg, #409eff, #36cfc9);
}

.strategy-icon {
  background: linear-gradient(135deg, #67c23a, #95de64);
}

.product-icon {
  background: linear-gradient(135deg, #e6a23c, #ffc53d);
}

.trade-icon {
  background: linear-gradient(135deg, #f56c6c, #ff7875);
}

.chart-row {
  margin-bottom: 24px;
}

/* Element Plus 卡片样式覆盖 */
:deep(.el-card__header) {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  font-weight: bold;
  border-radius: 8px 8px 0 0;
}

:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }
  
  .chart-row .el-col {
    margin-bottom: 16px;
  }
}
</style> 