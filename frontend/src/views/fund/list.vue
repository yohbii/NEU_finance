<template>
  <div class="fund-list-page">
    <el-card>
      <template #header>
        <span>基金管理</span>
      </template>
      
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="基金代码">
            <el-input 
              v-model="searchForm.fundCode" 
              placeholder="请输入基金代码" 
              clearable
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="基金类型">
            <el-select 
              v-model="searchForm.fundType" 
              placeholder="请选择基金类型"
              clearable
              style="width: 200px;"
            >
              <el-option 
                v-for="option in fundTypeOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="基金公司">
            <el-select 
              v-model="searchForm.fundCompany" 
              placeholder="请选择基金公司"
              clearable
              style="width: 200px;"
            >
              <el-option 
                v-for="option in fundCompanyOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">查询</el-button>
            <el-button @click="handleReset" :icon="RefreshRight">重置</el-button>
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
        table-layout="auto"
      >
        <el-table-column prop="fundCode" label="基金代码" width="120" fixed="left" />
        <el-table-column prop="fundName" label="基金名称" width="200" show-overflow-tooltip />
        <el-table-column prop="fundType" label="基金类型" width="100" />
        <el-table-column prop="fundCompany" label="基金公司" width="120" show-overflow-tooltip />
        <el-table-column prop="fundManager" label="基金经理" width="100" />
        <el-table-column prop="establishDate" label="成立日期" width="120" />
        <el-table-column label="单位净值" width="120">
          <template #default="{ row }">
            {{ row.unitNetValue ? row.unitNetValue.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="累计净值" width="120">
          <template #default="{ row }">
            {{ row.accumulatedNetValue ? row.accumulatedNetValue.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="最小投资" width="120">
          <template #default="{ row }">
            {{ row.minInvestment ? `${row.minInvestment}元` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="管理费率" width="120">
          <template #default="{ row }">
            {{ row.managementFee ? `${(row.managementFee * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="风险等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)">
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
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
    
    <!-- 基金详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentFund ? currentFund.fundName : '基金详情'" width="1000px" :before-close="handleDetailClose">
      <div v-if="currentFund" class="fund-detail-content">
        <!-- 头部业绩卡片 -->
        <div class="performance-header">
          <div v-if="performanceData" class="performance-main">
            <div class="performance-item highlight">
              <div class="performance-value" :class="getReturnClass(performanceData.threeYearReturn)">
                {{ formatPercentage(performanceData.threeYearReturn) }}
              </div>
              <div class="performance-label">年化收益 (近3年)</div>
            </div>
            <div class="performance-item">
              <div class="performance-value" :class="getReturnClass(performanceData.ytdReturn)">
                {{ formatPercentage(performanceData.ytdReturn) }}
              </div>
              <div class="performance-label">累计收益</div>
            </div>
            <div class="performance-item">
              <div class="performance-value neutral">--</div>
              <div class="performance-label">日涨跌</div>
            </div>
          </div>
          
          <div v-else class="performance-main no-data">
            <div class="no-data-message">
              <el-icon class="no-data-icon"><Warning /></el-icon>
              <span>暂无业绩数据</span>
            </div>
          </div>
          
          <div class="performance-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="detail-label">今年以来</span>
                <span class="detail-value" :class="getReturnClass(performanceData?.ytdReturn)">
                  {{ performanceData ? formatPercentage(performanceData.ytdReturn) : '--' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">成立时长</span>
                <span class="detail-value">{{ getEstablishDuration() }}</span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="detail-label">最新净值</span>
                <span class="detail-value">{{ currentFund.unitNetValue ? currentFund.unitNetValue.toFixed(4) : '--' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">最大回撤</span>
                <span class="detail-value">
                  {{ performanceData ? formatPercentage(performanceData.maxDrawdown) : '--' }}
                </span>
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-item">
                <span class="detail-label">基金经理</span>
                <span class="detail-value">{{ currentFund.fundManager || '--' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">基金规模</span>
                <span class="detail-value">--</span>
              </div>
            </div>
          </div>
          
          <div class="fund-type-info">
            <span class="risk-label">{{ currentFund.fundType || '--' }}</span>
            <span class="fund-category">{{ currentFund.fundCode || '--' }}</span>
          </div>
        </div>

        <!-- 业绩曲线图 -->
        <div class="performance-chart-section">
          <h3 class="section-title">业绩曲线</h3>
          <div v-if="performanceData" class="chart-legend">
            <div class="legend-item">
              <span class="legend-line product-line"></span>
              <span class="legend-text" :class="getReturnClass(performanceData.ytdReturn)">
                本产品 {{ formatPercentage(performanceData.ytdReturn) }}
              </span>
            </div>
            <div class="legend-item">
              <span class="legend-line benchmark-line"></span>
              <span class="legend-text">沪深300指数 --</span>
            </div>
            <div class="legend-item">
              <span class="legend-line comparison-line"></span>
              <span class="legend-text">业绩比较基准 --</span>
            </div>
          </div>
          
          <div v-else class="chart-no-data">
            <el-empty description="暂无图表数据" />
          </div>
          
          <!-- 图表区域 -->
          <div v-if="performanceData" class="chart-container">
            <div ref="chartRef" class="performance-chart"></div>
          </div>
          
          <!-- 时间段选择 -->
          <div v-if="performanceData" class="time-period-tabs">
            <div class="period-tab active">
              今年以来<br/>
              <span class="period-return" :class="getReturnClass(performanceData.ytdReturn)">
                {{ formatPercentage(performanceData.ytdReturn) }}
              </span>
            </div>
            <div class="period-tab">
              近1月<br/>
              <span class="period-return" :class="getReturnClass(performanceData.oneMonthReturn)">
                {{ formatPercentage(performanceData.oneMonthReturn) }}
              </span>
            </div>
            <div class="period-tab">
              近3月<br/>
              <span class="period-return" :class="getReturnClass(performanceData.threeMonthReturn)">
                {{ formatPercentage(performanceData.threeMonthReturn) }}
              </span>
            </div>
            <div class="period-tab">
              近1年<br/>
              <span class="period-return" :class="getReturnClass(performanceData.oneYearReturn)">
                {{ formatPercentage(performanceData.oneYearReturn) }}
              </span>
            </div>
            <div class="period-tab">
              更多<br/><span class="period-more">所有时段</span>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="basic-info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">基金代码</span>
              <span class="info-value">{{ currentFund.fundCode }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">基金类型</span>
              <span class="info-value">{{ currentFund.fundType }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">基金公司</span>
              <span class="info-value">{{ currentFund.fundCompany }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">成立日期</span>
              <span class="info-value">{{ currentFund.establishDate }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">管理费率</span>
              <span class="info-value">{{ currentFund.managementFee ? `${(currentFund.managementFee * 100).toFixed(2)}%` : '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">风险等级</span>
              <span class="info-value">{{ getRiskLevelText(currentFund.riskLevel) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑基金弹窗 -->
    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑基金' : '新增基金'" width="800px" :before-close="handleEditClose">
      <div class="edit-form-content">
        <el-form :model="editForm" ref="editFormRef" label-width="120px" size="large">
          <!-- 基本信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><User /></el-icon>
              基本信息
            </div>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="基金代码" prop="fundCode" :rules="[{ required: true, message: '请输入基金代码' }]">
                  <el-input 
                    v-model="editForm.fundCode" 
                    placeholder="请输入基金代码" 
                    :disabled="!!editForm.id"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="基金名称" prop="fundName" :rules="[{ required: true, message: '请输入基金名称' }]">
                  <el-input v-model="editForm.fundName" placeholder="请输入基金名称" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="基金类型" prop="fundType">
                  <el-select v-model="editForm.fundType" placeholder="请选择基金类型" style="width: 100%;">
                    <el-option 
                      v-for="option in fundTypeOptions" 
                      :key="option.value" 
                      :value="option.value"
                      :label="option.label"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="基金公司" prop="fundCompany">
                  <el-select v-model="editForm.fundCompany" placeholder="请选择基金公司" style="width: 100%;">
                    <el-option 
                      v-for="option in fundCompanyOptions" 
                      :key="option.value" 
                      :value="option.value"
                      :label="option.label"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="基金经理">
                  <el-input v-model="editForm.fundManager" placeholder="请输入基金经理" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="成立日期">
                  <el-date-picker 
                    v-model="editForm.establishDate" 
                    type="date"
                    placeholder="请选择成立日期" 
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 净值信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><TrendCharts /></el-icon>
              净值信息
            </div>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="单位净值">
                  <el-input-number 
                    v-model="editForm.unitNetValue" 
                    placeholder="请输入单位净值" 
                    :precision="4"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="累计净值">
                  <el-input-number 
                    v-model="editForm.accumulatedNetValue" 
                    placeholder="请输入累计净值" 
                    :precision="4"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 投资信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Money /></el-icon>
              投资信息
            </div>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="最小投资金额">
                  <el-input-number 
                    v-model="editForm.minInvestment" 
                    placeholder="请输入最小投资金额"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="管理费率">
                  <el-input-number 
                    v-model="editForm.managementFee" 
                    placeholder="请输入管理费率" 
                    :precision="4"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="风险等级">
                  <el-select v-model="editForm.riskLevel" placeholder="请选择风险等级" style="width: 100%;">
                    <el-option :value="1" label="低风险" />
                    <el-option :value="2" label="中低风险" />
                    <el-option :value="3" label="中风险" />
                    <el-option :value="4" label="中高风险" />
                    <el-option :value="5" label="高风险" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%;">
                    <el-option :value="1" label="正常" />
                    <el-option :value="0" label="停用" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 描述信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Document /></el-icon>
              描述信息
            </div>
            <el-form-item label="基金描述">
              <el-input 
                v-model="editForm.description" 
                type="textarea"
                placeholder="请输入基金描述" 
                :rows="4"
                style="width: 100%;"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="editVisible = false">取消</el-button>
          <el-button type="primary" size="large" @click="handleSave" :loading="saveLoading">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  RefreshRight, 
  View, 
  Edit, 
  Delete,
  InfoFilled,
  TrendCharts,
  Money,
  Document,
  Setting,
  User,
  Warning
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getFundList, getFundById, deleteFund, getFundFilterOptions, createFund, updateFund, getFundPerformance } from '@/api/fund'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const editVisible = ref(false)
const currentFund = ref(null)
const performanceData = ref(null)
const fundTypeOptions = ref([])
const fundCompanyOptions = ref([])
const editFormRef = ref(null)
const chartRef = ref(null)
const chartInstance = ref(null)

// 搜索表单
const searchForm = reactive({
  fundCode: '',
  fundType: '',
  fundCompany: '',
  keyword: ''
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 编辑表单
const editForm = reactive({
  id: null,
  fundCode: '',
  fundName: '',
  fundType: '',
  fundCompany: '',
  fundManager: '',
  establishDate: '',
  unitNetValue: null,
  accumulatedNetValue: null,
  minInvestment: null,
  managementFee: null,
  riskLevel: null,
  status: 1,
  description: ''
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
    
    console.log('请求参数:', params)
    const response = await getFundList(params)
    console.log('API响应:', response)
    
    if (response && response.data) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
      console.log('设置表格数据:', tableData.value)
      console.log('分页总数:', pagination.total)
    }
  } catch (error) {
    console.error('加载基金列表失败:', error)
    ElMessage.error('加载基金列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const loadFilterOptions = async () => {
  try {
    const response = await getFundFilterOptions()
    if (response && response.data) {
      const options = response.data
      fundTypeOptions.value = (options.fundTypes || []).map(type => ({
        label: type,
        value: type
      }))
      fundCompanyOptions.value = (options.fundCompanies || []).map(company => ({
        label: company,
        value: company
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
    console.log('查看基金:', row)
    const [fundResponse, performanceResponse] = await Promise.all([
      getFundById(row.id),
      getFundPerformance(row.id)
    ])
    
    if (fundResponse && fundResponse.data) {
      currentFund.value = fundResponse.data
    }
    
    if (performanceResponse && performanceResponse.data) {
      performanceData.value = performanceResponse.data
    }
    
    detailVisible.value = true
    
    // 等待弹窗渲染完成后初始化图表
    if (performanceData.value) {
      await nextTick()
      initChart()
    }
  } catch (error) {
    console.error('获取基金详情失败:', error)
    ElMessage.error('获取基金详情失败')
    // 如果获取详情失败，直接显示当前行数据
    currentFund.value = row
    performanceData.value = null
    detailVisible.value = true
  }
}

const handleEdit = async (row) => {
  try {
    console.log('编辑基金:', row)
    const response = await getFundById(row.id)
    if (response && response.data) {
      const fundData = response.data
      // 填充编辑表单
      Object.keys(editForm).forEach(key => {
        if (fundData[key] !== undefined) {
          editForm[key] = fundData[key]
        }
      })
      editVisible.value = true
    }
  } catch (error) {
    console.error('获取基金详情失败:', error)
    ElMessage.error('获取基金详情失败')
    // 如果获取详情失败，直接使用当前行数据
    Object.keys(editForm).forEach(key => {
      if (row[key] !== undefined) {
        editForm[key] = row[key]
      }
    })
    editVisible.value = true
  }
}

const handleSave = async () => {
  if (!editFormRef.value) return
  
  try {
    // 表单验证
    const valid = await editFormRef.value.validate()
    if (!valid) return
    
    saveLoading.value = true
    
    if (editForm.id) {
      // 更新基金
      await updateFund(editForm.id, editForm)
      ElMessage.success('更新基金成功')
    } else {
      // 创建基金
      await createFund(editForm)
      ElMessage.success('创建基金成功')
    }
    
    editVisible.value = false
    loadData() // 重新加载数据
    
    // 重置表单
    resetEditForm()
  } catch (error) {
    console.error('保存基金失败:', error)
    ElMessage.error('保存基金失败')
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    console.log('删除基金:', row)
    await ElMessageBox.confirm('确定要删除这条基金记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteFund(row.id)
    ElMessage.success('删除基金成功')
    loadData() // 重新加载数据
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除基金失败:', error)
    ElMessage.error('删除基金失败')
  }
}

const handleDetailClose = () => {
  detailVisible.value = false
  currentFund.value = null
  performanceData.value = null
  destroyChart()
}

const handleEditClose = () => {
  editVisible.value = false
  resetEditForm()
}

const resetEditForm = () => {
  Object.keys(editForm).forEach(key => {
    if (key === 'status') {
      editForm[key] = 1
    } else if (key === 'id') {
      editForm[key] = null
    } else {
      editForm[key] = ''
    }
  })
}

const getRiskLevelText = (level) => {
  const levels = ['', '低风险', '中低风险', '中风险', '中高风险', '高风险']
  return levels[level] || '-'
}

const getRiskLevelType = (level) => {
  const types = ['', 'success', 'info', 'warning', 'danger', 'danger']
  return types[level] || 'info'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatPercentage = (value) => {
  if (value === null || value === undefined) return '-'
  const percentage = (value * 100).toFixed(2)
  return percentage >= 0 ? `+${percentage}%` : `${percentage}%`
}

const getReturnClass = (value) => {
  if (value === null || value === undefined) return ''
  return value >= 0 ? 'positive' : 'negative'
}

const getEstablishDuration = () => {
  if (!currentFund.value?.establishDate) return '--'
  
  const establishDate = new Date(currentFund.value.establishDate)
  const now = new Date()
  const diffTime = Math.abs(now - establishDate)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  const years = Math.floor(diffDays / 365)
  const remainingDays = diffDays % 365
  
  if (years > 0) {
    return `${years}年${remainingDays}天`
  } else {
    return `${remainingDays}天`
  }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value || !performanceData.value) return
  
  // 销毁已存在的图表实例
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
  
  // 创建新的图表实例
  chartInstance.value = echarts.init(chartRef.value)
  
  // 生成模拟的时间序列数据（基于业绩数据）
  const dates = []
  const netValues = []
  const benchmarkValues = []
  
  // 生成最近12个月的数据
  const now = new Date()
  for (let i = 11; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    dates.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }))
    
    // 基于实际业绩数据生成模拟净值走势
    const baseValue = 1.0
    const monthlyReturn = performanceData.value.oneYearReturn / 12 // 月化收益
    const volatilityFactor = (Math.random() - 0.5) * 0.1 // 随机波动
    const value = baseValue * (1 + (monthlyReturn + volatilityFactor) * (12 - i))
    
    netValues.push(Number(value.toFixed(4)))
    benchmarkValues.push(Number((value * 0.95 + Math.random() * 0.1).toFixed(4)))
  }
  
  const option = {
    title: {
      text: '净值走势图',
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
          const change = params.length > 1 && param.seriesIndex === 0 ? 
            ((value / netValues[0] - 1) * 100).toFixed(2) + '%' : ''
          result += `<span style="color:${color}">●</span> ${param.seriesName}: ${value} ${change ? '(' + change + ')' : ''}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['基金净值', '业绩基准'],
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
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
      }
    },
    yAxis: {
      type: 'value',
      scale: true,
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '基金净值',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          color: '#ff6b6b',
          width: 3
        },
        itemStyle: {
          color: '#ff6b6b'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(255, 107, 107, 0.3)'
            }, {
              offset: 1, color: 'rgba(255, 107, 107, 0.1)'
            }]
          }
        },
        data: netValues
      },
      {
        name: '业绩基准',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: {
          color: '#4a90e2',
          width: 2,
          type: 'dashed'
        },
        itemStyle: {
          color: '#4a90e2'
        },
        data: benchmarkValues
      }
    ]
  }
  
  chartInstance.value.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance.value) {
    chartInstance.value.resize()
  }
}

// 销毁图表
const destroyChart = () => {
  if (chartInstance.value) {
    window.removeEventListener('resize', handleResize)
    chartInstance.value.dispose()
    chartInstance.value = null
  }
}

// 生命周期
onMounted(() => {
  console.log('基金列表组件挂载，开始加载数据')
  loadData()
  loadFilterOptions()
})

onUnmounted(() => {
  destroyChart()
})
</script>

<style scoped>
.fund-list-page {
  padding: 20px;
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

/* 基金详情弹窗样式 */
.fund-detail-content {
  padding: 0;
  max-height: 70vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  border-radius: 12px;
}

/* 头部业绩区域 */
.performance-header {
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: white;
  padding: 24px;
  border-radius: 12px 12px 0 0;
  position: relative;
}

.performance-main {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
}

.performance-item {
  text-align: center;
}

.performance-item.highlight {
  transform: scale(1.1);
}

.performance-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.performance-value.positive {
  color: #ff6b6b;
}

.performance-value.negative {
  color: #4ecdc4;
}

.performance-value.neutral {
  color: #ffe66d;
}

.performance-label {
  font-size: 14px;
  opacity: 0.9;
}

.performance-details {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  width: 48%;
}

.detail-label {
  font-size: 14px;
  opacity: 0.8;
}

.detail-value {
  font-weight: bold;
}

.detail-value.positive {
  color: #ff6b6b;
}

.fund-type-info {
  position: absolute;
  top: 24px;
  right: 24px;
  text-align: right;
}

.risk-label {
  display: block;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.fund-category {
  font-size: 14px;
  opacity: 0.8;
}

/* 业绩曲线区域 */
.performance-chart-section {
  background: white;
  padding: 24px;
  margin: 0;
}

/* 无数据状态 */
.performance-main.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 120px;
}

.no-data-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
}

.no-data-icon {
  font-size: 24px;
}

.chart-no-data {
  padding: 40px 0;
}

.chart-placeholder {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
  border-left: 4px solid #4a90e2;
  padding-left: 12px;
}

.chart-legend {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  align-items: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-line {
  width: 20px;
  height: 2px;
  display: inline-block;
}

.legend-line.product-line {
  background: #ffa500;
}

.legend-line.benchmark-line {
  background: #ccc;
}

.legend-line.comparison-line {
  background: #4a90e2;
}

.legend-text {
  font-size: 14px;
}

.legend-text.positive {
  color: #ff6b6b;
}

.chart-container {
  position: relative;
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.chart-y-axis {
  position: absolute;
  left: 0;
  top: 16px;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 60px;
}

.y-label {
  font-size: 12px;
  color: #666;
  text-align: right;
  padding-right: 8px;
}

.chart-area {
  margin-left: 60px;
  margin-right: 20px;
  height: 200px;
  border-left: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
}

.chart-x-axis {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  margin-left: 60px;
  margin-right: 20px;
  font-size: 12px;
  color: #666;
}

.time-period-tabs {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.period-tab {
  text-align: center;
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
  line-height: 1.4;
}

.period-tab.active {
  background: #4a90e2;
  color: white;
}

.period-tab:hover {
  background: #e8f4fd;
}

.period-tab.active:hover {
  background: #357abd;
}

.period-return {
  font-weight: bold;
  font-size: 12px;
}

.period-return.positive {
  color: #ff6b6b;
}

.period-return.negative {
  color: #4ecdc4;
}

.period-tab.active .period-return {
  color: white;
}

.period-more {
  font-size: 12px;
  opacity: 0.8;
}

/* 基本信息区域 */
.basic-info-section {
  background: white;
  padding: 24px;
  border-radius: 0 0 12px 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #4a90e2;
}

.info-label {
  font-size: 14px;
  color: #666;
}

.info-value {
  font-weight: bold;
  color: #333;
}

/* 编辑表单样式 */
.edit-form-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 8px;
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

/* 无数据状态样式 */
.performance-main.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 120px;
}

.no-data-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
}

.no-data-icon {
  font-size: 24px;
}

.chart-no-data {
  padding: 40px 0;
}

.chart-placeholder {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.performance-chart {
  width: 100%;
  height: 400px;
}
</style>
