<template>
  <div class="trade-rebalance-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3><el-icon><TrendCharts /></el-icon> 交易记录列表</h3>
          <div class="header-actions">
            <el-button type="info" @click="goToExecution" :icon="Operation">
              交易执行
            </el-button>
            <el-button type="primary" @click="handleCreate" :icon="Plus">
              新增交易
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="组合ID">
            <el-input-number 
              v-model="searchForm.portfolioId" 
              placeholder="请输入组合ID"
              style="width: 150px;"
              :min="1"
            />
          </el-form-item>
          <el-form-item label="资产代码">
            <el-input 
              v-model="searchForm.assetCode" 
              placeholder="请输入资产代码"
              clearable
              style="width: 150px;"
            />
          </el-form-item>
          <el-form-item label="交易类型">
            <el-select 
              v-model="searchForm.tradeType" 
              placeholder="请选择交易类型"
              clearable
              style="width: 150px;"
            >
              <el-option 
                v-for="option in tradeTypeOptions" 
                :key="option.value" 
                :value="option.value"
                :label="option.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="交易日期">
            <el-date-picker
              v-model="searchForm.tradeDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 280px;"
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
        <el-table-column prop="tradeNo" label="交易编号" width="150" fixed="left" />
        <el-table-column prop="portfolioId" label="组合ID" width="100" />
        <el-table-column prop="assetCode" label="资产代码" width="120" />
        <el-table-column label="交易类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.tradeType === 1 ? 'success' : 'danger'">
              {{ getTradeTypeText(row.tradeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额" width="120">
          <template #default="{ row }">
            {{ row.tradeAmount ? `${row.tradeAmount.toFixed(2)}元` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="交易份额" width="120">
          <template #default="{ row }">
            {{ row.tradeShares ? row.tradeShares.toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="交易价格" width="120">
          <template #default="{ row }">
            {{ row.tradePrice ? `${row.tradePrice.toFixed(4)}元` : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="交易状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTradeStatusType(row.tradeStatus)">
              {{ getTradeStatusText(row.tradeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tradeDate" label="交易日期" width="120" />
        <el-table-column prop="settlementDate" label="结算日期" width="120" />
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

    <!-- 交易详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="modalTitle" width="800px">
      <div v-if="currentTrade" class="trade-detail-content">
        <el-form :model="currentTrade" label-width="120px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="交易编号">
                <el-input v-model="currentTrade.tradeNo" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="组合ID">
                <el-input v-model="currentTrade.portfolioId" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="资产代码">
                <el-input v-model="currentTrade.assetCode" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易类型">
                <el-input :value="getTradeTypeText(currentTrade.tradeType)" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="交易金额">
                <el-input :value="currentTrade.tradeAmount ? `${currentTrade.tradeAmount.toFixed(2)}元` : '-'" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易份额">
                <el-input :value="currentTrade.tradeShares ? currentTrade.tradeShares.toFixed(4) : '-'" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="交易价格">
                <el-input :value="currentTrade.tradePrice ? `${currentTrade.tradePrice.toFixed(4)}元` : '-'" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易状态">
                <el-input :value="getTradeStatusText(currentTrade.tradeStatus)" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="交易日期">
                <el-input v-model="currentTrade.tradeDate" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交易费用">
                <el-input :value="currentTrade.tradeFee ? `${currentTrade.tradeFee.toFixed(2)}元` : '-'" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注">
            <el-input v-model="currentTrade.remark" type="textarea" readonly :rows="3" />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑交易弹窗 -->
    <el-dialog 
      v-model="formVisible" 
      :title="isEdit ? '编辑交易记录' : '新增交易记录'" 
      width="800px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef"
        :model="tradeForm" 
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="组合ID" prop="portfolioId">
              <el-input-number
                v-model="tradeForm.portfolioId"
                :min="1"
                placeholder="请输入组合ID"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基金ID" prop="fundId">
              <el-input-number
                v-model="tradeForm.fundId"
                :min="1"
                placeholder="请输入基金ID"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="交易类型" prop="tradeType">
              <el-select 
                v-model="tradeForm.tradeType" 
                placeholder="请选择交易类型"
                style="width: 100%"
              >
                <el-option 
                  v-for="option in tradeTypeOptions" 
                  :key="option.value" 
                  :value="option.value"
                  :label="option.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易日期" prop="tradeDate">
              <el-date-picker
                v-model="tradeForm.tradeDate"
                type="date"
                placeholder="请选择交易日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="交易金额" prop="tradeAmount">
              <el-input-number
                v-model="tradeForm.tradeAmount"
                :min="0"
                :precision="2"
                :step="100"
                placeholder="请输入交易金额"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">元</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易份额">
              <el-input-number
                v-model="tradeForm.tradeShares"
                :min="0"
                :precision="4"
                :step="100"
                placeholder="请输入交易份额"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="交易价格">
              <el-input-number
                v-model="tradeForm.tradePrice"
                :min="0"
                :precision="4"
                :step="0.01"
                placeholder="请输入交易价格"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">元</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易费用">
              <el-input-number
                v-model="tradeForm.tradeFee"
                :min="0"
                :precision="2"
                :step="1"
                placeholder="请输入交易费用"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">元</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input 
            v-model="tradeForm.remark" 
            type="textarea" 
            placeholder="请输入备注信息"
            :rows="3"
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
  Operation
} from '@element-plus/icons-vue'
import { 
  getTradeList, 
  getTradeDetail, 
  createTrade, 
  updateTrade, 
  deleteTrade 
} from '@/api/trade'
import { useRouter } from 'vue-router'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const formVisible = ref(false)
const isEdit = ref(false)
const currentTrade = ref(null)
const formRef = ref(null)
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  portfolioId: null,
  assetCode: '',
  tradeType: null,
  tradeDateRange: []
})

// 交易表单
const tradeForm = reactive({
  portfolioId: null,
  fundId: null,
  tradeType: null,
  tradeAmount: null,
  tradeShares: null,
  tradePrice: null,
  tradeFee: null,
  tradeDate: '',
  remark: ''
})

// 表单验证规则
const formRules = {
  portfolioId: [
    { required: true, message: '请输入组合ID', trigger: 'blur' }
  ],
  fundId: [
    { required: true, message: '请输入基金ID', trigger: 'blur' }
  ],
  tradeType: [
    { required: true, message: '请选择交易类型', trigger: 'change' }
  ],
  tradeAmount: [
    { required: true, message: '请输入交易金额', trigger: 'blur' }
  ],
  tradeDate: [
    { required: true, message: '请选择交易日期', trigger: 'change' }
  ]
}

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 交易类型选项
const tradeTypeOptions = ref([
  { label: '买入', value: 1 },
  { label: '卖出', value: 2 }
])

// 计算属性
const modalTitle = computed(() => {
  return currentTrade.value ? `交易详情 - ${currentTrade.value.tradeNo}` : '交易详情'
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      portfolioId: searchForm.portfolioId,
      assetCode: searchForm.assetCode,
      tradeType: searchForm.tradeType
    }
    
    // 处理日期范围
    if (searchForm.tradeDateRange && searchForm.tradeDateRange.length === 2) {
      // 格式化日期为 YYYY-MM-DD 格式
      const startDate = new Date(searchForm.tradeDateRange[0])
      const endDate = new Date(searchForm.tradeDateRange[1])
      params.startDate = startDate.toISOString().split('T')[0]
      params.endDate = endDate.toISOString().split('T')[0]
    }
    
    const response = await getTradeList(params)
    if (response && response.data) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载交易记录失败:', error)
    ElMessage.error('加载交易记录失败')
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
    if (key === 'tradeDateRange') {
      searchForm[key] = []
    } else if (key === 'portfolioId' || key === 'tradeType') {
      searchForm[key] = null
    } else {
      searchForm[key] = ''
    }
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

const handleView = (row) => {
  ElMessage.info('交易详情功能开发中...')
}

const handleCreate = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const response = await getTradeDetail(row.id)
    if (response && response.data) {
      isEdit.value = true
      Object.assign(tradeForm, {
        id: response.data.id,
        portfolioId: response.data.portfolioId,
        fundId: response.data.fundId,
        tradeType: response.data.tradeType,
        tradeAmount: response.data.tradeAmount,
        tradeShares: response.data.tradeShares,
        tradePrice: response.data.tradePrice,
        tradeFee: response.data.tradeFee,
        tradeDate: response.data.tradeDate,
        remark: response.data.remark
      })
      formVisible.value = true
    }
  } catch (error) {
    console.error('获取交易详情失败:', error)
    ElMessage.error('获取交易详情失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await updateTrade(tradeForm.id, tradeForm)
      ElMessage.success('更新交易记录成功')
    } else {
      await createTrade(tradeForm)
      ElMessage.success('创建交易记录成功')
    }
    
    formVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(isEdit.value ? '更新交易记录失败' : '创建交易记录失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.assign(tradeForm, {
    portfolioId: null,
    fundId: null,
    tradeType: null,
    tradeAmount: null,
    tradeShares: null,
    tradePrice: null,
    tradeFee: null,
    tradeDate: '',
    remark: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除交易记录 ${row.tradeNo} 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteTrade(row.id)
    ElMessage.success('删除交易记录成功')
    loadData() // 重新加载数据
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除交易记录失败:', error)
    ElMessage.error('删除交易记录失败')
  }
}

const getTradeTypeText = (type) => {
  const types = { 1: '买入', 2: '卖出' }
  return types[type] || '-'
}

const getTradeStatusText = (status) => {
  const statuses = { 1: '待确认', 2: '已确认', 3: '已完成', 4: '已取消' }
  return statuses[status] || '-'
}

const getTradeStatusType = (status) => {
  const types = { 1: 'warning', 2: 'info', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

// 跳转到交易执行
const goToExecution = () => {
  router.push('/trade/execution')
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.trade-rebalance-page {
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

.trade-detail-content {
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
</style> 