<template>
  <div class="factor-analysis-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/factor/list' }">因子管理</el-breadcrumb-item>
        <el-breadcrumb-item>因子分析</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="analysis-content">
      <!-- 分析配置卡片 -->
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><Setting /></el-icon> 分析配置</h3>
            <el-button type="primary" @click="runAnalysis" :loading="analyzing">
              {{ analyzing ? '分析中...' : '开始分析' }}
            </el-button>
          </div>
        </template>

        <el-form :model="analysisConfig" label-width="120px">
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="分析类型">
                <el-select v-model="analysisConfig.type" placeholder="选择分析类型" style="width: 100%">
                  <el-option label="因子相关性分析" value="correlation" />
                  <el-option label="因子有效性检验" value="effectiveness" />
                  <el-option label="因子贡献度分析" value="contribution" />
                  <el-option label="因子稳定性分析" value="stability" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="analysisConfig.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="基金池">
                <el-select v-model="analysisConfig.fundPool" placeholder="选择基金池" style="width: 100%">
                  <el-option label="全部基金" value="all" />
                  <el-option label="股票型基金" value="stock" />
                  <el-option label="债券型基金" value="bond" />
                  <el-option label="混合型基金" value="mixed" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="选择因子">
            <div class="factor-selection">
              <el-checkbox-group v-model="analysisConfig.selectedFactors">
                <el-row :gutter="16">
                  <el-col :span="6" v-for="factor in factorOptions" :key="factor.id">
                    <el-checkbox :label="factor.id" :value="factor.id">
                      {{ factor.name }}
                    </el-checkbox>
                  </el-col>
                </el-row>
              </el-checkbox-group>
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 分析结果 -->
      <div v-if="analysisResult" class="analysis-results">
        <!-- 因子相关性分析 -->
        <el-card v-if="analysisConfig.type === 'correlation'" class="result-card" shadow="hover">
          <template #header>
            <h3><el-icon><Link /></el-icon> 因子相关性分析结果</h3>
          </template>
          
          <div class="correlation-analysis">
            <div class="correlation-heatmap">
              <h4>因子相关性热力图</h4>
              <div ref="correlationChart" class="chart-container"></div>
            </div>
            
            <el-divider />
            
            <div class="correlation-table">
              <h4>因子相关性矩阵</h4>
              <el-table :data="analysisResult.correlationMatrix" border>
                <el-table-column prop="factor" label="因子" width="150" fixed="left" />
                <el-table-column 
                  v-for="factor in analysisResult.factors" 
                  :key="factor" 
                  :prop="factor" 
                  :label="factor" 
                  width="100"
                >
                  <template #default="{ row }">
                    <span :class="getCorrelationClass(row[factor])">
                      {{ formatCorrelation(row[factor]) }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>

        <!-- 因子有效性检验 -->
        <el-card v-if="analysisConfig.type === 'effectiveness'" class="result-card" shadow="hover">
          <template #header>
            <h3><el-icon><DataAnalysis /></el-icon> 因子有效性检验结果</h3>
          </template>
          
          <div class="effectiveness-analysis">
            <el-table :data="analysisResult.effectivenessTest" border>
              <el-table-column prop="factorName" label="因子名称" width="200" />
              <el-table-column prop="icMean" label="IC均值" width="120">
                <template #default="{ row }">
                  <span :class="getICClass(row.icMean)">
                    {{ row.icMean.toFixed(4) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="icStd" label="IC标准差" width="120">
                <template #default="{ row }">
                  {{ row.icStd.toFixed(4) }}
                </template>
              </el-table-column>
              <el-table-column prop="icIR" label="IC_IR" width="120">
                <template #default="{ row }">
                  <span :class="getIRClass(row.icIR)">
                    {{ row.icIR.toFixed(4) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="winRate" label="胜率" width="120">
                <template #default="{ row }">
                  {{ (row.winRate * 100).toFixed(2) }}%
                </template>
              </el-table-column>
              <el-table-column prop="tStat" label="t统计量" width="120">
                <template #default="{ row }">
                  {{ row.tStat.toFixed(4) }}
                </template>
              </el-table-column>
              <el-table-column prop="pValue" label="p值" width="120">
                <template #default="{ row }">
                  <span :class="getPValueClass(row.pValue)">
                    {{ row.pValue.toFixed(4) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="effectiveness" label="有效性" width="120">
                <template #default="{ row }">
                  <el-tag :type="getEffectivenessType(row.effectiveness)">
                    {{ row.effectiveness }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>

        <!-- 因子贡献度分析 -->
        <el-card v-if="analysisConfig.type === 'contribution'" class="result-card" shadow="hover">
          <template #header>
            <h3><el-icon><DataLine /></el-icon> 因子贡献度分析结果</h3>
          </template>
          
          <div class="contribution-analysis">
            <el-row :gutter="24">
              <el-col :span="12">
                <h4>因子贡献度饼图</h4>
                <div ref="contributionPieChart" class="chart-container"></div>
              </el-col>
              <el-col :span="12">
                <h4>因子贡献度柱状图</h4>
                <div ref="contributionBarChart" class="chart-container"></div>
              </el-col>
            </el-row>
            
            <el-divider />
            
            <div class="contribution-table">
              <h4>因子贡献度详情</h4>
              <el-table :data="analysisResult.contributionData" border>
                <el-table-column prop="factorName" label="因子名称" width="200" />
                <el-table-column prop="contribution" label="贡献度" width="120">
                  <template #default="{ row }">
                    {{ (row.contribution * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
                <el-table-column prop="cumulativeContribution" label="累计贡献度" width="150">
                  <template #default="{ row }">
                    {{ (row.cumulativeContribution * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
                <el-table-column prop="importance" label="重要性" width="120">
                  <template #default="{ row }">
                    <el-tag :type="getImportanceType(row.importance)">
                      {{ row.importance }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>

        <!-- 因子稳定性分析 -->
        <el-card v-if="analysisConfig.type === 'stability'" class="result-card" shadow="hover">
          <template #header>
            <h3><el-icon><TrendCharts /></el-icon> 因子稳定性分析结果</h3>
          </template>
          
          <div class="stability-analysis">
            <div class="stability-chart">
              <h4>因子IC时序图</h4>
              <div ref="stabilityChart" class="chart-container"></div>
            </div>
            
            <el-divider />
            
            <div class="stability-metrics">
              <h4>稳定性指标</h4>
              <el-table :data="analysisResult.stabilityMetrics" border>
                <el-table-column prop="factorName" label="因子名称" width="200" />
                <el-table-column prop="icStability" label="IC稳定性" width="120">
                  <template #default="{ row }">
                    <span :class="getStabilityClass(row.icStability)">
                      {{ row.icStability.toFixed(4) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="decay" label="衰减率" width="120">
                  <template #default="{ row }">
                    {{ (row.decay * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
                <el-table-column prop="consistency" label="一致性" width="120">
                  <template #default="{ row }">
                    {{ (row.consistency * 100).toFixed(2) }}%
                  </template>
                </el-table-column>
                <el-table-column prop="stability" label="稳定性评级" width="120">
                  <template #default="{ row }">
                    <el-tag :type="getStabilityRatingType(row.stability)">
                      {{ row.stability }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Setting,
  Link,
  DataAnalysis,
  DataLine,
  TrendCharts
} from '@element-plus/icons-vue'
import { runFactorAnalysis, getAnalysisResult } from '@/api/factor'

// 响应式数据
const analyzing = ref(false)
const analysisResult = ref(null)
const correlationChart = ref(null)
const contributionPieChart = ref(null)
const contributionBarChart = ref(null)
const stabilityChart = ref(null)

// 分析配置
const analysisConfig = reactive({
  type: 'correlation',
  dateRange: [],
  fundPool: 'all',
  selectedFactors: []
})

// 因子选项
const factorOptions = ref([
  { id: 'size', name: '规模因子' },
  { id: 'value', name: '价值因子' },
  { id: 'profitability', name: '盈利因子' },
  { id: 'growth', name: '成长因子' },
  { id: 'leverage', name: '杠杆因子' },
  { id: 'liquidity', name: '流动性因子' },
  { id: 'volatility', name: '波动率因子' },
  { id: 'momentum', name: '动量因子' },
  { id: 'quality', name: '质量因子' },
  { id: 'beta', name: 'Beta因子' }
])

// 执行分析
const runAnalysis = async () => {
  if (analysisConfig.selectedFactors.length === 0) {
    ElMessage.warning('请至少选择一个因子')
    return
  }

  try {
    analyzing.value = true
    ElMessage.info('开始执行因子分析，请稍候...')
    
    const data = {
      type: analysisConfig.type,
      dateRange: analysisConfig.dateRange,
      fundPool: analysisConfig.fundPool,
      selectedFactors: analysisConfig.selectedFactors
    }
    
    const response = await runFactorAnalysis(data)
    if (response.code === 200) {
      analysisResult.value = response.data
      
      await nextTick()
      initCharts()
      
      ElMessage.success('因子分析完成！')
    }
    
  } catch (error) {
    console.error('因子分析失败:', error)
    ElMessage.error('因子分析失败')
  } finally {
    analyzing.value = false
  }
}

// 初始化图表
const initCharts = () => {
  if (typeof echarts === 'undefined') {
    console.warn('ECharts not loaded, charts will not be displayed')
    return
  }

  if (analysisConfig.type === 'correlation') {
    initCorrelationChart()
  } else if (analysisConfig.type === 'contribution') {
    initContributionCharts()
  } else if (analysisConfig.type === 'stability') {
    initStabilityChart()
  }
}

// 初始化相关性热力图
const initCorrelationChart = () => {
  if (!correlationChart.value) return

  const chart = echarts.init(correlationChart.value)
  const factors = analysisResult.value.factors
  const data = []

  factors.forEach((factor1, i) => {
    factors.forEach((factor2, j) => {
      const value = analysisResult.value.correlationMatrix[i][factor2]
      data.push([i, j, value])
    })
  })

  const option = {
    tooltip: {
      position: 'top',
      formatter: function (params) {
        return `${factors[params.data[0]]} vs ${factors[params.data[1]]}<br/>相关系数: ${params.data[2].toFixed(4)}`
      }
    },
    grid: {
      height: '50%',
      top: '10%'
    },
    xAxis: {
      type: 'category',
      data: factors,
      splitArea: {
        show: true
      }
    },
    yAxis: {
      type: 'category',
      data: factors,
      splitArea: {
        show: true
      }
    },
    visualMap: {
      min: -1,
      max: 1,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '15%',
      inRange: {
        color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
      }
    },
    series: [{
      name: '相关系数',
      type: 'heatmap',
      data: data,
      label: {
        show: true,
        formatter: function (params) {
          return params.data[2].toFixed(2)
        }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }

  chart.setOption(option)
}

// 初始化贡献度图表
const initContributionCharts = () => {
  // 饼图
  if (contributionPieChart.value) {
    const pieChart = echarts.init(contributionPieChart.value)
    const pieData = analysisResult.value.contributionData.map(item => ({
      name: item.factorName,
      value: item.contribution
    }))

    const pieOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      series: [{
        name: '因子贡献度',
        type: 'pie',
        radius: '50%',
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }

    pieChart.setOption(pieOption)
  }

  // 柱状图
  if (contributionBarChart.value) {
    const barChart = echarts.init(contributionBarChart.value)
    const barData = analysisResult.value.contributionData

    const barOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: barData.map(item => item.factorName),
        axisLabel: {
          interval: 0,
          rotate: 45
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}%'
        }
      },
      series: [{
        name: '贡献度',
        type: 'bar',
        data: barData.map(item => (item.contribution * 100).toFixed(2)),
        itemStyle: {
          color: '#409EFF'
        }
      }]
    }

    barChart.setOption(barOption)
  }
}

// 初始化稳定性图表
const initStabilityChart = () => {
  if (!stabilityChart.value) return

  const chart = echarts.init(stabilityChart.value)
  
  // 模拟时序数据
  const dates = []
  const seriesData = {}
  
  for (let i = 0; i < 30; i++) {
    const date = new Date()
    date.setDate(date.getDate() - 29 + i)
    dates.push(date.toISOString().split('T')[0])
  }

  analysisResult.value.stabilityMetrics.forEach(metric => {
    seriesData[metric.factorName] = dates.map(() => Math.random() * 0.2 - 0.1)
  })

  const series = Object.keys(seriesData).map(factorName => ({
    name: factorName,
    type: 'line',
    data: seriesData[factorName],
    smooth: true
  }))

  const option = {
    title: {
      text: '因子IC时序变化'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: Object.keys(seriesData),
      bottom: 10
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
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: series
  }

  chart.setOption(option)
}

// 样式类方法
const getCorrelationClass = (value) => {
  if (Math.abs(value) > 0.7) return 'high-correlation'
  if (Math.abs(value) > 0.3) return 'medium-correlation'
  return 'low-correlation'
}

const getICClass = (value) => {
  return value > 0 ? 'positive' : 'negative'
}

const getIRClass = (value) => {
  if (value > 0.5) return 'high-ir'
  if (value > 0) return 'medium-ir'
  return 'low-ir'
}

const getPValueClass = (value) => {
  return value < 0.05 ? 'significant' : 'not-significant'
}

const getEffectivenessType = (effectiveness) => {
  return effectiveness === '有效' ? 'success' : 'danger'
}

const getImportanceType = (importance) => {
  const typeMap = { '高': 'danger', '中': 'warning', '低': 'info' }
  return typeMap[importance] || 'info'
}

const getStabilityClass = (value) => {
  if (value > 0.7) return 'high-stability'
  if (value > 0.4) return 'medium-stability'
  return 'low-stability'
}

const getStabilityRatingType = (rating) => {
  const typeMap = { '稳定': 'success', '一般': 'warning', '不稳定': 'danger' }
  return typeMap[rating] || 'info'
}

// 格式化相关系数
const formatCorrelation = (value) => {
  if (value === undefined || value === null) {
    return '0.0000'
  }
  return Number(value).toFixed(4)
}

// 生命周期
onMounted(() => {
  // 设置默认时间范围
  const endDate = new Date()
  const startDate = new Date()
  startDate.setFullYear(endDate.getFullYear() - 1)
  
  analysisConfig.dateRange = [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
  
  // 默认选择前4个因子
  analysisConfig.selectedFactors = factorOptions.value.slice(0, 4).map(f => f.id)
})
</script>

<style scoped>
.factor-analysis-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.config-card {
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

.factor-selection {
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 16px;
  background: #FAFAFA;
}

.result-card {
  margin-bottom: 20px;
}

.result-card h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.correlation-analysis h4,
.effectiveness-analysis h4,
.contribution-analysis h4,
.stability-analysis h4 {
  margin-bottom: 16px;
  color: #303133;
}

/* 相关性样式 */
.high-correlation {
  color: #F56C6C;
  font-weight: bold;
}

.medium-correlation {
  color: #E6A23C;
  font-weight: bold;
}

.low-correlation {
  color: #909399;
}

/* IC值样式 */
.positive {
  color: #67C23A;
}

.negative {
  color: #F56C6C;
}

/* IR值样式 */
.high-ir {
  color: #67C23A;
  font-weight: bold;
}

.medium-ir {
  color: #E6A23C;
}

.low-ir {
  color: #F56C6C;
}

/* p值样式 */
.significant {
  color: #67C23A;
  font-weight: bold;
}

.not-significant {
  color: #909399;
}

/* 稳定性样式 */
.high-stability {
  color: #67C23A;
  font-weight: bold;
}

.medium-stability {
  color: #E6A23C;
}

.low-stability {
  color: #F56C6C;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-checkbox-group) {
  width: 100%;
}

:deep(.el-checkbox) {
  margin-right: 0;
  margin-bottom: 12px;
  width: 100%;
}
</style> 