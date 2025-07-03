<template>
  <div class="factor-list-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3><el-icon><DataBoard /></el-icon> 因子列表</h3>
          <div class="header-actions">
            <el-button type="info" @click="goToAnalysis" :icon="TrendCharts">
              因子分析
            </el-button>
            <el-button type="primary" @click="handleCreate" :icon="Plus">
              新增因子
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="因子代码">
            <el-input 
              v-model="searchForm.factorCode" 
              placeholder="请输入因子代码"
              clearable
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="因子类型">
            <el-select 
              v-model="searchForm.factorType" 
              placeholder="请选择因子类型"
              clearable
              style="width: 200px;"
            >
              <el-option 
                v-for="option in factorTypeOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="因子分类">
            <el-select 
              v-model="searchForm.category" 
              placeholder="请选择因子分类"
              clearable
              style="width: 200px;"
            >
              <el-option 
                v-for="option in categoryOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="请输入因子名称关键词"
              clearable
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">
              查询
            </el-button>
            <el-button @click="handleReset" :icon="RefreshRight">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="factorCode" label="因子代码" width="120" fixed="left" />
        <el-table-column prop="factorName" label="因子名称" width="200" show-overflow-tooltip />
        <el-table-column prop="factorType" label="因子类型" width="120" />
        <el-table-column prop="category" label="因子分类" width="120" />
        <el-table-column prop="description" label="因子描述" width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              @click="handleView(row)"
              :icon="View"
            >
              查看
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleEdit(row)"
              :icon="Edit"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 因子分析弹窗 -->
    <el-dialog v-model="detailVisible" :title="modalTitle" width="1200px" top="5vh">
      <div v-if="currentFactor" class="factor-analysis-content">
        <!-- 顶部指标卡片 -->
        <div class="metrics-cards">
          <div class="metric-card">
            <div class="metric-label">IC均值</div>
            <div class="metric-value positive">0.001</div>
          </div>
          <div class="metric-card">
            <div class="metric-label">ICIR(IC信息比率)</div>
            <div class="metric-value positive">0.823</div>
          </div>
          <div class="metric-card">
            <div class="metric-label">胜率</div>
            <div class="metric-value positive">0.014</div>
          </div>
        </div>

        <!-- 因子详情 -->
        <div class="factor-info-section">
          <h3 class="section-title">因子详情</h3>
          <div class="factor-info-grid">
            <div class="info-item">
              <span class="info-label">因子代码:</span>
              <span class="info-value">{{ currentFactor.factorCode }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">因子名称:</span>
              <span class="info-value">{{ currentFactor.factorName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">因子类型:</span>
              <span class="info-value">{{ currentFactor.factorType }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">因子分类:</span>
              <span class="info-value">{{ currentFactor.category }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">计算公式:</span>
              <span class="info-value">{{ currentFactor.calculationFormula || '净利润与营业总收入之比 (TTM) /营业总收入 (TTM)' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">更新时间:</span>
              <span class="info-value">{{ formatDate(currentFactor.updatedAt) || '下一交易日每晚9:00更新' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据来源:</span>
              <span class="info-value">中位数去极值 -> 行业市值中性化 -> zscore标准化</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据频率:</span>
              <span class="info-value">加权方式按市值加权</span>
            </div>
          </div>
        </div>

        <!-- 组合构建配置 -->
        <div class="portfolio-config">
          <div class="config-row">
            <div class="config-item">
              <label>组合构建:</label>
              <el-select v-model="portfolioConfig.buildMethod" size="small" style="width: 120px;">
                <el-option label="纯多头组合" value="long_only" />
                <el-option label="多空组合" value="long_short" />
              </el-select>
            </div>
            <div class="config-item">
              <label>股票池:</label>
              <el-select v-model="portfolioConfig.stockPool" size="small" style="width: 100px;">
                <el-option label="中证500" value="zz500" />
                <el-option label="全A" value="all_a" />
              </el-select>
            </div>
            <div class="config-item">
              <label>回测区间:</label>
              <el-select v-model="portfolioConfig.period" size="small" style="width: 100px;">
                <el-option label="近1年" value="1y" />
                <el-option label="近3年" value="3y" />
              </el-select>
            </div>
            <div class="config-item">
              <label>过滤条件:</label>
              <el-input v-model="portfolioConfig.filter" size="small" style="width: 100px;" placeholder="无" />
            </div>
            <div class="config-item">
              <label>手续费及冲击:</label>
              <el-input v-model="portfolioConfig.cost" size="small" style="width: 80px;" placeholder="无" />
            </div>
            <el-button type="primary" size="small" @click="updateChart">确定</el-button>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="chart-section">
          <!-- 图表控制栏 -->
          <div class="chart-controls">
            <div class="chart-legend">
              <div class="legend-item">
                <span class="legend-dot" style="background-color: #1f77b4;"></span>
                <span>中证500 (000905)</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot" style="background-color: #ff7f0e;"></span>
                <span>最小方差组合</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot" style="background-color: #2ca02c;"></span>
                <span>最大方差组合</span>
              </div>
            </div>
            <div class="chart-options">
              <el-radio-group v-model="chartType" size="small">
                <el-radio-button label="普通图">普通图</el-radio-button>
                <el-radio-button label="对数轴">对数轴</el-radio-button>
                <el-radio-button label="全分位">全分位</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <!-- ECharts 图表容器 -->
          <div ref="factorChartRef" class="factor-chart"></div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  RefreshRight, 
  View, 
  Edit, 
  Delete,
  Plus,
  TrendCharts,
  DataBoard
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getFactorList, getFactorDetail, deleteFactor, getFactorOptions } from '@/api/factor'
import { useRouter } from 'vue-router'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentFactor = ref(null)
const factorTypeOptions = ref([])
const categoryOptions = ref([])
const router = useRouter()

// 图表相关
const factorChartRef = ref(null)
const factorChartInstance = ref(null)
const chartType = ref('普通图')

// 组合配置
const portfolioConfig = reactive({
  buildMethod: 'long_only',
  stockPool: 'zz500',
  period: '3y',
  filter: '',
  cost: ''
})

// 搜索表单
const searchForm = reactive({
  factorCode: '',
  factorType: '',
  category: '',
  keyword: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 计算属性
const modalTitle = computed(() => {
  return currentFactor.value ? `因子详情 - ${currentFactor.value.factorName}` : '因子详情'
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    
    const response = await getFactorList(params)
    if (response && response.data) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载因子列表失败:', error)
    ElMessage.error('加载因子列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const loadFilterOptions = async () => {
  try {
    const response = await getFactorOptions()
    if (response && response.data) {
      const options = response.data
      
      factorTypeOptions.value = (options.factorTypes || []).map(type => ({
        label: type,
        value: type
      }))
      
      categoryOptions.value = (options.categories || []).map(category => ({
        label: category,
        value: category
      }))
    }
  } catch (error) {
    console.error('加载筛选选项失败:', error)
    ElMessage.error('加载筛选选项失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.current = 1
  loadData()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleView = async (row) => {
  try {
    const response = await getFactorDetail(row.id)
    if (response && response.data) {
      currentFactor.value = response.data
      detailVisible.value = true
      
      // 等待弹窗渲染完成后初始化图表
      await nextTick()
      initFactorChart()
    }
  } catch (error) {
    console.error('获取因子详情失败:', error)
    ElMessage.error('获取因子详情失败')
    // 如果获取详情失败，直接显示当前行数据
    currentFactor.value = row
    detailVisible.value = true
    
    await nextTick()
    initFactorChart()
  }
}

// 初始化因子分析图表
const initFactorChart = () => {
  if (!factorChartRef.value) return
  
  // 销毁已存在的图表实例
  if (factorChartInstance.value) {
    factorChartInstance.value.dispose()
  }
  
  // 创建新的图表实例
  factorChartInstance.value = echarts.init(factorChartRef.value)
  
  // 生成模拟的时间序列数据
  const dates = []
  const benchmarkData = []
  const minVarianceData = []
  const maxVarianceData = []
  
  // 生成从2022年到2025年的数据
  const startDate = new Date('2022-09-01')
  const endDate = new Date('2025-01-01')
  const totalDays = Math.floor((endDate - startDate) / (1000 * 60 * 60 * 24))
  
  for (let i = 0; i < totalDays; i += 30) { // 每30天一个数据点
    const currentDate = new Date(startDate.getTime() + i * 24 * 60 * 60 * 1000)
    dates.push(currentDate.toISOString().slice(0, 7)) // YYYY-MM格式
    
    // 模拟不同的收益率走势
    const t = i / totalDays
    const volatility = 0.3
    
    // 基准指数（相对平稳）
    const benchmarkReturn = -0.2 + 0.1 * Math.sin(t * Math.PI * 4) + (Math.random() - 0.5) * volatility * 0.5
    benchmarkData.push(Number((benchmarkReturn * 100).toFixed(2)))
    
    // 最小方差组合（波动较小但收益更好）
    const minVarReturn = -0.15 + 0.25 * Math.sin(t * Math.PI * 3 + 1) + (Math.random() - 0.5) * volatility * 0.3
    minVarianceData.push(Number((minVarReturn * 100).toFixed(2)))
    
    // 最大方差组合（波动较大）
    const maxVarReturn = -0.3 + 0.4 * Math.sin(t * Math.PI * 5 + 2) + (Math.random() - 0.5) * volatility
    maxVarianceData.push(Number((maxVarReturn * 100).toFixed(2)))
  }
  
  const option = {
    title: {
      text: `${currentFactor.value?.factorName || '因子'} 收益率分析`,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(param => {
          const color = param.color
          const value = param.value
          result += `<span style="color:${color}">●</span> ${param.seriesName}: ${value}%<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['中证500 (000905)', '最小方差组合', '最大方差组合'],
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      scale: chartType.value === '对数轴',
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '中证500 (000905)',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: '#1f77b4',
          width: 2
        },
        data: benchmarkData
      },
      {
        name: '最小方差组合',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: '#ff7f0e',
          width: 2
        },
        data: minVarianceData
      },
      {
        name: '最大方差组合',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: '#2ca02c',
          width: 2
        },
        data: maxVarianceData
      }
    ]
  }
  
  factorChartInstance.value.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleFactorChartResize)
}

// 处理图表窗口大小变化
const handleFactorChartResize = () => {
  if (factorChartInstance.value) {
    factorChartInstance.value.resize()
  }
}

// 更新图表
const updateChart = () => {
  initFactorChart()
  ElMessage.success('图表已更新')
}

// 销毁图表
const destroyFactorChart = () => {
  if (factorChartInstance.value) {
    window.removeEventListener('resize', handleFactorChartResize)
    factorChartInstance.value.dispose()
    factorChartInstance.value = null
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleEdit = (row) => {
  // TODO: 跳转到编辑页面
  console.log('编辑因子:', row)
  ElMessage.info('编辑功能开发中...')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除因子 ${row.factorName} 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteFactor(row.id)
    ElMessage.success('删除因子成功')
    loadData() // 重新加载数据
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除因子失败:', error)
    ElMessage.error('删除因子失败')
  }
}

const handleCreate = () => {
  // TODO: 跳转到创建页面
  console.log('新增因子')
  ElMessage.info('新增功能开发中...')
}

const goToAnalysis = () => {
  router.push('/factor/analysis')
}

// 生命周期
onMounted(() => {
  loadData()
  loadFilterOptions()
})

onUnmounted(() => {
  destroyFactorChart()
})

// 监听弹窗关闭
const handleDialogClose = () => {
  detailVisible.value = false
  currentFactor.value = null
  destroyFactorChart()
}
</script>

<style scoped>
.factor-list-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 6px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.factor-detail-content {
  padding: 16px;
}

.dialog-footer {
  text-align: right;
  padding: 16px 0;
}

/* Element Plus 样式覆盖 */
:deep(.el-card__header) {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-radius: 8px 8px 0 0;
  padding: 16px 24px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: bold;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

:deep(.el-dialog) {
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

/* 因子分析内容样式 */
.factor-analysis-content {
  padding: 0;
  max-height: 70vh;
  overflow-y: auto;
}

/* 指标卡片 */
.metrics-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
}

.metric-card {
  flex: 1;
  text-align: center;
  color: white;
}

.metric-label {
  font-size: 14px;
  margin-bottom: 8px;
  opacity: 0.9;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
}

.metric-value.positive {
  color: #4ecdc4;
}

.metric-value.negative {
  color: #ff6b6b;
}

/* 因子详情区域 */
.factor-info-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.factor-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 0;
}

.info-label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
  flex-shrink: 0;
}

.info-value {
  color: #333;
  word-break: break-all;
}

/* 组合配置 */
.portfolio-config {
  margin-bottom: 24px;
  padding: 16px;
  background: #f0f2f5;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
}

.config-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.config-item label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

/* 图表区域 */
.chart-section {
  background: white;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.chart-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
  background: #fafafa;
}

.chart-legend {
  display: flex;
  gap: 24px;
  align-items: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.chart-options {
  display: flex;
  align-items: center;
}

.factor-chart {
  width: 100%;
  height: 400px;
}
</style> 