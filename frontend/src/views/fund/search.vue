<template>
  <div class="fund-search-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h3><el-icon><Search /></el-icon> 基金筛选</h3>
          <el-button type="primary" @click="resetFilters" :icon="RefreshRight">
            重置筛选
          </el-button>
        </div>
      </template>
      
      <!-- 基础筛选条件 -->
      <div class="filter-section">
        <h4 class="section-title">基础筛选</h4>
        <el-form :model="filterForm" label-width="100px" class="filter-form">
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="基金类型">
                <el-select
                  v-model="filterForm.fundType"
                  placeholder="请选择基金类型"
                  multiple
                  collapse-tags
                  collapse-tags-tooltip
                  style="width: 100%"
                >
                  <el-option
                    v-for="option in fundTypeOptions"
                    :key="option.value"
                    :value="option.value"
                    :label="option.label"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="基金公司">
                <el-select
                  v-model="filterForm.fundCompany"
                  placeholder="请选择基金公司"
                  multiple
                  collapse-tags
                  collapse-tags-tooltip
                  filterable
                  style="width: 100%"
                >
                  <el-option
                    v-for="option in fundCompanyOptions"
                    :key="option.value"
                    :value="option.value"
                    :label="option.label"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="风险等级">
                <el-select
                  v-model="filterForm.riskLevel"
                  placeholder="请选择风险等级"
                  multiple
                  style="width: 100%"
                >
                  <el-option
                    v-for="option in riskLevelOptions"
                    :key="option.value"
                    :value="option.value"
                    :label="option.label"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="成立时间">
                <el-date-picker
                  v-model="filterForm.establishDateRange"
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
              <el-form-item label="最小投资">
                <el-input-number
                  v-model="filterForm.minInvestment"
                  :min="0"
                  :max="1000000"
                  :step="1000"
                  placeholder="最小投资金额"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="基金经理">
                <el-input
                  v-model="filterForm.fundManager"
                  placeholder="请输入基金经理姓名"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      
      <!-- 高级筛选条件 -->
      <el-divider />
      <div class="filter-section">
        <div class="section-header">
          <h4 class="section-title">高级筛选</h4>
          <el-switch
            v-model="showAdvancedFilters"
            active-text="展开"
            inactive-text="收起"
          />
        </div>
        
        <el-collapse-transition>
          <div v-show="showAdvancedFilters">
            <el-form :model="filterForm" label-width="120px" class="filter-form">
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="年化收益率">
                    <div class="range-input">
                      <el-input-number
                        v-model="filterForm.returnRange[0]"
                        :precision="2"
                        placeholder="最小值"
                        style="width: 45%"
                      />
                      <span class="range-separator">至</span>
                      <el-input-number
                        v-model="filterForm.returnRange[1]"
                        :precision="2"
                        placeholder="最大值"
                        style="width: 45%"
                      />
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="最大回撤">
                    <div class="range-input">
                      <el-input-number
                        v-model="filterForm.maxDrawdownRange[0]"
                        :precision="2"
                        placeholder="最小值"
                        style="width: 45%"
                      />
                      <span class="range-separator">至</span>
                      <el-input-number
                        v-model="filterForm.maxDrawdownRange[1]"
                        :precision="2"
                        placeholder="最大值"
                        style="width: 45%"
                      />
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="夏普比率">
                    <div class="range-input">
                      <el-input-number
                        v-model="filterForm.sharpeRatioRange[0]"
                        :precision="2"
                        placeholder="最小值"
                        style="width: 45%"
                      />
                      <span class="range-separator">至</span>
                      <el-input-number
                        v-model="filterForm.sharpeRatioRange[1]"
                        :precision="2"
                        placeholder="最大值"
                        style="width: 45%"
                      />
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="管理费率">
                    <div class="range-input">
                      <el-input-number
                        v-model="filterForm.managementFeeRange[0]"
                        :precision="4"
                        :max="0.05"
                        placeholder="最小值"
                        style="width: 45%"
                      />
                      <span class="range-separator">至</span>
                      <el-input-number
                        v-model="filterForm.managementFeeRange[1]"
                        :precision="4"
                        :max="0.05"
                        placeholder="最大值"
                        style="width: 45%"
                      />
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-collapse-transition>
      </div>
      
      <!-- 筛选操作按钮 -->
      <div class="filter-actions">
        <el-button type="primary" @click="handleSearch" :loading="searching" :icon="Search">
          开始筛选
        </el-button>
        <el-button @click="resetFilters" :icon="RefreshRight">
          重置条件
        </el-button>
        <el-button type="info" @click="saveFilter" :icon="Collection">
          保存筛选
        </el-button>
      </div>
    </el-card>
    
    <!-- 筛选结果 -->
    <el-card v-if="searchResults.length > 0 || hasSearched" shadow="hover" class="results-card">
      <template #header>
        <div class="card-header">
          <h3><el-icon><List /></el-icon> 筛选结果</h3>
          <div class="result-info">
            <span>共找到 {{ totalResults }} 只基金</span>
            <el-button-group>
              <el-button :type="sortType === 'return' ? 'primary' : ''" @click="sortBy('return')">
                按收益排序
              </el-button>
              <el-button :type="sortType === 'risk' ? 'primary' : ''" @click="sortBy('risk')">
                按风险排序
              </el-button>
              <el-button :type="sortType === 'sharpe' ? 'primary' : ''" @click="sortBy('sharpe')">
                按夏普比率排序
              </el-button>
            </el-button-group>
          </div>
        </div>
      </template>
      
      <el-table
        :data="searchResults"
        v-loading="searching"
        stripe
        border
        style="width: 100%"
        @row-click="viewFundDetail"
      >
        <el-table-column prop="fundCode" label="基金代码" width="120" />
        <el-table-column prop="fundName" label="基金名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fundType" label="基金类型" width="120" />
        <el-table-column prop="fundCompany" label="基金公司" width="150" show-overflow-tooltip />
        <el-table-column label="年化收益率" width="120">
          <template #default="{ row }">
            <span :class="getReturnClass(row.annualReturn)">
              {{ formatPercentage(row.annualReturn) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="最大回撤" width="120">
          <template #default="{ row }">
            <span class="negative">
              {{ formatPercentage(row.maxDrawdown) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="夏普比率" width="120">
          <template #default="{ row }">
            {{ row.sharpeRatio ? row.sharpeRatio.toFixed(4) : '--' }}
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.riskLevel" disabled size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="viewFundDetail(row)">
              详情
            </el-button>
            <el-button type="success" size="small" @click.stop="addToPortfolio(row)">
              加入组合
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalResults"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <el-empty v-if="hasSearched && searchResults.length === 0" description="未找到符合条件的基金" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search,
  RefreshRight,
  Collection,
  List
} from '@element-plus/icons-vue'
import { getFundList, getFundFilterOptions, getFundCompanies, getFundManagers } from '@/api/fund'

// 路由
const router = useRouter()

// 响应式数据
const searching = ref(false)
const hasSearched = ref(false)
const showAdvancedFilters = ref(false)
const searchResults = ref([])
const totalResults = ref(0)
const sortType = ref('')

// 筛选表单
const filterForm = reactive({
  fundType: [],
  fundCompany: [],
  riskLevel: [],
  establishDateRange: [],
  minInvestment: null,
  fundManager: '',
  returnRange: [null, null],
  maxDrawdownRange: [null, null],
  sharpeRatioRange: [null, null],
  managementFeeRange: [null, null]
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20
})

// 选项数据
const fundTypeOptions = ref([])
const fundCompanyOptions = ref([])
const riskLevelOptions = ref([
  { label: '1级 - 低风险', value: 1 },
  { label: '2级 - 中低风险', value: 2 },
  { label: '3级 - 中等风险', value: 3 },
  { label: '4级 - 中高风险', value: 4 },
  { label: '5级 - 高风险', value: 5 }
])

// 加载筛选选项
const loadFilterOptions = async () => {
  try {
    const response = await getFundFilterOptions()
    if (response.code === 200) {
      fundTypeOptions.value = response.data.fundTypes.map(type => ({
        label: type,
        value: type
      }))
      // fundCompanyOptions 在 loadFundCompanies 中单独加载
    }
  } catch (error) {
    console.error('加载筛选选项失败:', error)
  }
}

// 执行筛选
const handleSearch = async () => {
  try {
    searching.value = true
    hasSearched.value = true
    
    // 处理参数，将数组转换为单个值或逗号分隔的字符串
    const params = {
      fundType: Array.isArray(filterForm.fundType) && filterForm.fundType.length > 0 ? filterForm.fundType[0] : null,
      fundCompany: Array.isArray(filterForm.fundCompany) && filterForm.fundCompany.length > 0 ? filterForm.fundCompany[0] : null,
      fundManager: filterForm.fundManager || null,
      riskLevel: Array.isArray(filterForm.riskLevel) && filterForm.riskLevel.length > 0 ? filterForm.riskLevel[0] : null,
      minInvestment: filterForm.minInvestment || null,
      keyword: filterForm.keyword || null,
      current: pagination.current,
      size: pagination.size
    }
    
    const response = await getFundList(params)
    if (response.code === 200) {
      searchResults.value = response.data.records || []
      totalResults.value = response.data.total || 0
      
      // 应用高级筛选条件
      if (showAdvancedFilters.value) {
        searchResults.value = applyAdvancedFilters(searchResults.value)
        totalResults.value = searchResults.value.length
      }
      
      ElMessage.success(`找到 ${totalResults.value} 只符合条件的基金`)
    }
  } catch (error) {
    console.error('筛选失败:', error)
    ElMessage.error('筛选失败')
  } finally {
    searching.value = false
  }
}

// 应用高级筛选条件
const applyAdvancedFilters = (funds) => {
  return funds.filter(fund => {
    // 年化收益率筛选
    if (filterForm.returnRange[0] !== null && fund.annualReturn < filterForm.returnRange[0]) {
      return false
    }
    if (filterForm.returnRange[1] !== null && fund.annualReturn > filterForm.returnRange[1]) {
      return false
    }
    
    // 最大回撤筛选
    if (filterForm.maxDrawdownRange[0] !== null && fund.maxDrawdown < filterForm.maxDrawdownRange[0]) {
      return false
    }
    if (filterForm.maxDrawdownRange[1] !== null && fund.maxDrawdown > filterForm.maxDrawdownRange[1]) {
      return false
    }
    
    // 夏普比率筛选
    if (filterForm.sharpeRatioRange[0] !== null && fund.sharpeRatio < filterForm.sharpeRatioRange[0]) {
      return false
    }
    if (filterForm.sharpeRatioRange[1] !== null && fund.sharpeRatio > filterForm.sharpeRatioRange[1]) {
      return false
    }
    
    // 管理费率筛选
    if (filterForm.managementFeeRange[0] !== null && fund.managementFee < filterForm.managementFeeRange[0]) {
      return false
    }
    if (filterForm.managementFeeRange[1] !== null && fund.managementFee > filterForm.managementFeeRange[1]) {
      return false
    }
    
    return true
  })
}

// 重置筛选条件
const resetFilters = () => {
  // 明确重置每个字段，避免类型混乱
  filterForm.fundType = []
  filterForm.fundCompany = []
  filterForm.riskLevel = []
  filterForm.establishDateRange = []
  filterForm.minInvestment = null
  filterForm.fundManager = ''
  filterForm.returnRange = [null, null]
  filterForm.maxDrawdownRange = [null, null]
  filterForm.sharpeRatioRange = [null, null]
  filterForm.managementFeeRange = [null, null]
  
  searchResults.value = []
  hasSearched.value = false
  totalResults.value = 0
  sortType.value = ''
  pagination.current = 1
}

// 排序
const sortBy = (type) => {
  sortType.value = type
  
  searchResults.value.sort((a, b) => {
    switch (type) {
      case 'return':
        return (b.annualReturn || 0) - (a.annualReturn || 0)
      case 'risk':
        return (a.maxDrawdown || 0) - (b.maxDrawdown || 0)
      case 'sharpe':
        return (b.sharpeRatio || 0) - (a.sharpeRatio || 0)
      default:
        return 0
    }
  })
}

// 查看基金详情
const viewFundDetail = (fund) => {
  router.push(`/fund/${fund.id}/detail`)
}

// 加入组合
const addToPortfolio = (fund) => {
  ElMessage.success(`已将 ${fund.fundName} 加入候选组合`)
}

// 保存筛选条件
const saveFilter = () => {
  ElMessage.success('筛选条件已保存')
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  if (hasSearched.value) {
    handleSearch()
  }
}

const handleCurrentChange = (current) => {
  pagination.current = current
  if (hasSearched.value) {
    handleSearch()
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

// 加载基金公司选项
const loadFundCompanies = async () => {
  try {
    const response = await getFundCompanies()
    if (response.code === 200) {
      // 将字符串数组转换为选项格式
      fundCompanyOptions.value = (response.data || []).map(company => ({
        label: company,
        value: company
      }))
    }
  } catch (error) {
    console.error('加载基金公司失败:', error)
  }
}

// 加载基金经理选项
const loadFundManagers = async () => {
  try {
    const response = await getFundManagers()
    if (response.code === 200) {
      // 基金经理列表只用于显示，不直接赋值给表单
      console.log('基金经理列表:', response.data)
    }
  } catch (error) {
    console.error('加载基金经理失败:', error)
  }
}

// 生命周期
onMounted(() => {
  loadFilterOptions()
  loadFundCompanies()
  loadFundManagers()
})
</script>

<style scoped>
.fund-search-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
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

.filter-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.filter-form {
  margin-bottom: 0;
}

.range-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.range-separator {
  color: #909399;
  font-size: 14px;
}

.filter-actions {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #EBEEF5;
  margin-top: 20px;
}

.filter-actions .el-button {
  margin: 0 8px;
}

.results-card {
  margin-top: 20px;
}

.result-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.result-info span {
  color: #606266;
  font-size: 14px;
}

.positive {
  color: #67C23A;
  font-weight: bold;
}

.negative {
  color: #F56C6C;
  font-weight: bold;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table .el-table__row:hover) {
  background: #f5f7fa;
}
</style> 