<template>
  <div class="strategy-list-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>策略管理</span>
          <el-button type="primary" @click="handleCreate" :icon="Plus">
            新增策略
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="策略类型">
            <el-select 
              v-model="searchForm.strategyType" 
              placeholder="请选择策略类型"
              clearable
              style="width: 200px;"
            >
              <el-option 
                v-for="option in strategyTypeOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="请输入策略名称关键词"
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
        <el-table-column prop="strategyName" label="策略名称" width="200" fixed="left" show-overflow-tooltip />
        <el-table-column prop="strategyType" label="策略类型" width="150" />
        <el-table-column label="风险等级" width="120">
          <template #default="{ row }">
            <el-rate 
              v-model="row.riskLevel" 
              disabled 
              show-score 
              text-color="#ff9900"
              score-template="{value} 级"
            />
          </template>
        </el-table-column>
        <el-table-column label="目标收益率" width="120">
          <template #default="{ row }">
            {{ row.targetReturn ? `${(row.targetReturn * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="最大回撤" width="120">
          <template #default="{ row }">
            {{ row.maxDrawdown ? `${(row.maxDrawdown * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="rebalanceFrequency" label="再平衡频率" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150" />
        <el-table-column label="操作" width="250" fixed="right">
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
              type="warning"
              @click="handleBacktest(row)"
              :icon="TrendCharts"
            >
              回测
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

    <!-- 策略详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="modalTitle" width="900px">
      <div v-if="currentStrategy" class="strategy-detail-content">
        <el-form :model="currentStrategy" label-width="120px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="策略名称">
                <el-input v-model="currentStrategy.strategyName" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="策略类型">
                <el-input v-model="currentStrategy.strategyType" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="风险等级">
                <el-rate 
                  v-model="currentStrategy.riskLevel" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                  score-template="{value} 级"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="目标收益率">
                <el-input 
                  :value="currentStrategy.targetReturn ? `${(currentStrategy.targetReturn * 100).toFixed(2)}%` : '-'" 
                  readonly 
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="最大回撤">
                <el-input 
                  :value="currentStrategy.maxDrawdown ? `${(currentStrategy.maxDrawdown * 100).toFixed(2)}%` : '-'" 
                  readonly 
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="再平衡频率">
                <el-input v-model="currentStrategy.rebalanceFrequency" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="策略描述">
            <el-input 
              v-model="currentStrategy.description" 
              type="textarea" 
              readonly 
              :rows="3" 
            />
          </el-form-item>
        </el-form>
        
        <!-- 持仓信息 -->
        <el-divider>
          <el-icon><Wallet /></el-icon>
          持仓信息
        </el-divider>
        <el-table
          v-if="strategyHoldings.length > 0"
          :data="strategyHoldings"
          size="small"
          border
        >
          <el-table-column prop="fundCode" label="基金代码" width="120" />
          <el-table-column prop="fundName" label="基金名称" show-overflow-tooltip />
          <el-table-column label="权重" width="100">
            <template #default="{ row }">
              {{ row.weight ? `${(row.weight * 100).toFixed(2)}%` : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="holdingAmount" label="持仓金额" width="120" />
          <el-table-column prop="updateDate" label="更新日期" width="120" />
        </el-table>
        <el-empty v-else description="暂无持仓数据" />
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑策略弹窗 -->
    <el-dialog 
      v-model="formVisible" 
      :title="isEdit ? '编辑策略' : '新增策略'" 
      width="800px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef"
        :model="strategyForm" 
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="策略名称" prop="strategyName">
              <el-input 
                v-model="strategyForm.strategyName" 
                placeholder="请输入策略名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="策略类型" prop="strategyType">
              <el-select 
                v-model="strategyForm.strategyType" 
                placeholder="请选择策略类型"
                style="width: 100%"
              >
                <el-option 
                  v-for="option in strategyTypeOptions" 
                  :key="option.value" 
                  :value="option.value"
                  :label="option.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-rate 
                v-model="strategyForm.riskLevel" 
                show-score 
                text-color="#ff9900"
                score-template="{value} 级"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标收益率" prop="targetReturn">
              <el-input-number
                v-model="strategyForm.targetReturn"
                :min="0"
                :max="100"
                :precision="2"
                :step="0.1"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">%</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最大回撤" prop="maxDrawdown">
              <el-input-number
                v-model="strategyForm.maxDrawdown"
                :min="0"
                :max="100"
                :precision="2"
                :step="0.1"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">%</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="再平衡频率" prop="rebalanceFrequency">
              <el-select 
                v-model="strategyForm.rebalanceFrequency" 
                placeholder="请选择再平衡频率"
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
        </el-row>
        <el-form-item label="策略描述" prop="description">
          <el-input 
            v-model="strategyForm.description" 
            type="textarea" 
            placeholder="请输入策略描述"
            :rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  RefreshRight, 
  View, 
  Edit, 
  Delete,
  Plus,
  TrendCharts,
  Wallet
} from '@element-plus/icons-vue'
import { getStrategyList, getStrategyDetail, deleteStrategy, getStrategyHoldings } from '@/api/strategy'
import { useRouter } from 'vue-router'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const formVisible = ref(false)
const isEdit = ref(false)
const currentStrategy = ref(null)
const strategyHoldings = ref([])
const formRef = ref(null)

// 搜索表单
const searchForm = reactive({
  strategyType: '',
  keyword: ''
})

// 策略表单
const strategyForm = reactive({
  strategyName: '',
  strategyType: '',
  riskLevel: 3,
  targetReturn: null,
  maxDrawdown: null,
  rebalanceFrequency: '',
  description: ''
})

// 表单验证规则
const formRules = {
  strategyName: [
    { required: true, message: '请输入策略名称', trigger: 'blur' },
    { min: 2, max: 100, message: '策略名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  strategyType: [
    { required: true, message: '请选择策略类型', trigger: 'change' }
  ],
  riskLevel: [
    { required: true, message: '请选择风险等级', trigger: 'change' }
  ],
  rebalanceFrequency: [
    { required: true, message: '请选择再平衡频率', trigger: 'change' }
  ]
}

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 策略类型选项
const strategyTypeOptions = ref([
  { label: '大类资产配置', value: '大类资产配置' },
  { label: 'FOF组合', value: 'FOF组合' },
  { label: '基金指数组合', value: '基金指数组合' },
  { label: '股票策略', value: '股票策略' },
  { label: '债券策略', value: '债券策略' }
])

// 计算属性
const modalTitle = computed(() => {
  return currentStrategy.value ? `策略详情 - ${currentStrategy.value.strategyName}` : '策略详情'
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
    
    const response = await getStrategyList(params)
    if (response && response.data) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载策略列表失败:', error)
    ElMessage.error('加载策略列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
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
    const response = await getStrategyDetail(row.id)
    if (response && response.data) {
      currentStrategy.value = response.data
      
      // 加载持仓信息
      const holdingsResponse = await getStrategyHoldings(row.id)
      if (holdingsResponse && holdingsResponse.data) {
        strategyHoldings.value = holdingsResponse.data
      }
      
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取策略详情失败:', error)
    ElMessage.error('获取策略详情失败')
    // 如果获取详情失败，直接显示当前行数据
    currentStrategy.value = row
    strategyHoldings.value = []
    detailVisible.value = true
  }
}

const handleCreate = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const response = await getStrategyDetail(row.id)
    if (response && response.data) {
      isEdit.value = true
      Object.assign(strategyForm, {
        id: response.data.id,
        strategyName: response.data.strategyName,
        strategyType: response.data.strategyType,
        riskLevel: response.data.riskLevel,
        targetReturn: response.data.targetReturn ? response.data.targetReturn * 100 : null,
        maxDrawdown: response.data.maxDrawdown ? response.data.maxDrawdown * 100 : null,
        rebalanceFrequency: response.data.rebalanceFrequency,
        description: response.data.description
      })
      formVisible.value = true
    }
  } catch (error) {
    console.error('获取策略详情失败:', error)
    ElMessage.error('获取策略详情失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const formData = {
      ...strategyForm,
      targetReturn: strategyForm.targetReturn ? strategyForm.targetReturn / 100 : null,
      maxDrawdown: strategyForm.maxDrawdown ? strategyForm.maxDrawdown / 100 : null
    }
    
    if (isEdit.value) {
      await updateStrategy(formData.id, formData)
      ElMessage.success('更新策略成功')
    } else {
      await createStrategy(formData)
      ElMessage.success('创建策略成功')
    }
    
    formVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(isEdit.value ? '更新策略失败' : '创建策略失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.assign(strategyForm, {
    strategyName: '',
    strategyType: '',
    riskLevel: 3,
    targetReturn: null,
    maxDrawdown: null,
    rebalanceFrequency: '',
    description: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除策略 "${row.strategyName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteStrategy(row.id)
    ElMessage.success('删除策略成功')
    loadData()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除策略失败:', error)
    ElMessage.error('删除策略失败')
  }
}

const handleBacktest = (row) => {
  // 跳转到策略回测页面
  const router = useRouter()
  router.push(`/strategy/${row.id}/backtest`)
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.strategy-list-page {
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

.strategy-detail-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
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
</style> 