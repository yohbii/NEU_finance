<template>
  <div class="product-list-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3><el-icon><Coin /></el-icon> 组合产品列表</h3>
          <div class="header-actions">
            <el-button type="info" @click="goToApproval" :icon="Document">
              审核管理
            </el-button>
            <el-button type="primary" @click="handleCreate" :icon="Plus">
              新增产品
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="产品类型">
          <el-select 
            v-model="searchForm.productType" 
            placeholder="请选择产品类型"
            clearable
            style="width: 200px;"
          >
            <el-option 
              v-for="option in productTypeOptions" 
              :key="option.value" 
              :value="option.value"
              :label="option.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select 
            v-model="searchForm.riskLevel" 
            placeholder="请选择风险等级"
            clearable
            style="width: 200px;"
          >
            <el-option 
              v-for="option in riskLevelOptions" 
              :key="option.value" 
              :value="option.value"
              :label="option.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="请输入产品名称关键词"
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
      
      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="productCode" label="产品代码" width="120" />
        <el-table-column prop="productName" label="产品名称" width="200" show-overflow-tooltip />
        <el-table-column prop="productType" label="产品类型" width="120" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)">
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturn" label="预期收益" width="120">
          <template #default="{ row }">
            {{ row.expectedReturn ? `${(row.expectedReturn * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="managementFee" label="管理费率" width="120">
          <template #default="{ row }">
            {{ row.managementFee ? `${(row.managementFee * 100).toFixed(2)}%` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="minInvestment" label="最小投资" width="120">
          <template #default="{ row }">
            {{ row.minInvestment ? `${row.minInvestment}元` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)" :icon="View">
              查看
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)" :icon="Edit">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">
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

    <!-- 产品详情弹窗 -->
    <d-modal
      v-model:visible="detailVisible"
      :title="modalTitle"
      width="800px"
      :footer="false"
    >
      <d-form 
        v-if="currentProduct"
        :model="currentProduct" 
        label-width="120px"
        readonly
      >
        <d-row :gutter="16">
          <d-col :span="12">
            <d-form-item label="产品代码">
              <d-input v-model="currentProduct.productCode" readonly />
            </d-form-item>
          </d-col>
          <d-col :span="12">
            <d-form-item label="产品名称">
              <d-input v-model="currentProduct.productName" readonly />
            </d-form-item>
          </d-col>
        </d-row>
        <d-row :gutter="16">
          <d-col :span="12">
            <d-form-item label="产品类型">
              <d-input v-model="currentProduct.productType" readonly />
            </d-form-item>
          </d-col>
          <d-col :span="12">
            <d-form-item label="风险等级">
              <d-rate v-model="currentProduct.riskLevel" readonly />
            </d-form-item>
          </d-col>
        </d-row>
        <d-row :gutter="16">
          <d-col :span="12">
            <d-form-item label="最小投资金额">
              <d-input v-model="currentProduct.minInvestAmount" readonly />
            </d-form-item>
          </d-col>
          <d-col :span="12">
            <d-form-item label="管理费率">
              <d-input v-model="currentProduct.managementFeeRate" readonly />
            </d-form-item>
          </d-col>
        </d-row>
        <d-form-item label="产品描述">
          <d-textarea v-model="currentProduct.description" readonly :rows="3" />
        </d-form-item>
        <d-form-item label="投资策略">
          <d-textarea v-model="currentProduct.investmentStrategy" readonly :rows="3" />
        </d-form-item>
      </d-form>
    </d-modal>

    <!-- 新增/编辑产品弹窗 -->
    <el-dialog 
      v-model="formVisible" 
      :title="isEdit ? '编辑产品' : '新增产品'" 
      width="800px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef"
        :model="productForm" 
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品代码" prop="productCode">
              <el-input 
                v-model="productForm.productCode" 
                placeholder="请输入产品代码"
                maxlength="50"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input 
                v-model="productForm.productName" 
                placeholder="请输入产品名称"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品类型" prop="productType">
              <el-select 
                v-model="productForm.productType" 
                placeholder="请选择产品类型"
                style="width: 100%"
              >
                <el-option 
                  v-for="option in productTypeOptions" 
                  :key="option.value" 
                  :value="option.value"
                  :label="option.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-rate 
                v-model="productForm.riskLevel" 
                show-score 
                text-color="#ff9900"
                score-template="{value} 级"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最小投资金额" prop="minInvestment">
              <el-input-number
                v-model="productForm.minInvestment"
                :min="0"
                :precision="2"
                :step="1000"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">元</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="管理费率" prop="managementFee">
              <el-input-number
                v-model="productForm.managementFee"
                :min="0"
                :max="10"
                :precision="4"
                :step="0.001"
                style="width: 100%"
              />
              <span style="margin-left: 8px; color: #666;">%</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="关联策略">
              <el-select 
                v-model="productForm.strategyId" 
                placeholder="请选择关联策略"
                clearable
                style="width: 100%"
              >
                <el-option 
                  v-for="strategy in strategyOptions" 
                  :key="strategy.id" 
                  :value="strategy.id"
                  :label="strategy.strategyName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品状态">
              <el-switch
                v-model="productForm.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="停用"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="产品描述" prop="description">
          <el-input 
            v-model="productForm.description" 
            type="textarea" 
            placeholder="请输入产品描述"
            :rows="4"
            maxlength="1000"
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
import { Plus, Search, RefreshRight, View, Edit, Delete, Document } from '@element-plus/icons-vue'
import { 
  getProductList, 
  getProductDetail, 
  createProduct, 
  updateProduct, 
  deleteProduct 
} from '@/api/product'
import { getStrategyList } from '@/api/strategy'
import { useRouter } from 'vue-router'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const formVisible = ref(false)
const isEdit = ref(false)
const currentProduct = ref(null)
const formRef = ref(null)
const strategyOptions = ref([])
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  productType: '',
  riskLevel: null,
  keyword: ''
})

// 产品表单
const productForm = reactive({
  productCode: '',
  productName: '',
  productType: '',
  riskLevel: 3,
  minInvestment: null,
  managementFee: null,
  strategyId: null,
  status: 1,
  description: ''
})

// 表单验证规则
const formRules = {
  productCode: [
    { required: true, message: '请输入产品代码', trigger: 'blur' },
    { min: 2, max: 50, message: '产品代码长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 2, max: 200, message: '产品名称长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  productType: [
    { required: true, message: '请选择产品类型', trigger: 'change' }
  ],
  riskLevel: [
    { required: true, message: '请选择风险等级', trigger: 'change' }
  ],
  minInvestment: [
    { required: true, message: '请输入最小投资金额', trigger: 'blur' }
  ],
  managementFee: [
    { required: true, message: '请输入管理费率', trigger: 'blur' }
  ]
}

// 分页信息
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 产品类型选项
const productTypeOptions = ref([
  { label: 'FOF', value: 'FOF' },
  { label: '资产配置', value: 'ASSET_ALLOCATION' },
  { label: '指数基金', value: 'INDEX_FUND' },
  { label: '量化产品', value: 'QUANTITATIVE' },
  { label: '固收产品', value: 'FIXED_INCOME' }
])

// 风险等级选项
const riskLevelOptions = ref([
  { label: '1级 - 保守型', value: 1 },
  { label: '2级 - 稳健型', value: 2 },
  { label: '3级 - 平衡型', value: 3 },
  { label: '4级 - 积极型', value: 4 },
  { label: '5级 - 激进型', value: 5 }
])

// 计算属性
const modalTitle = computed(() => {
  return currentProduct.value ? `产品详情 - ${currentProduct.value.productName}` : '产品详情'
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      productType: searchForm.productType,
      riskLevel: searchForm.riskLevel,
      keyword: searchForm.keyword
    }
    
    const response = await getProductList(params)
    if (response && response.data) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载产品列表失败:', error)
    ElMessage.error('加载产品列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const loadStrategies = async () => {
  try {
    const response = await getStrategyList({ current: 1, size: 100 })
    if (response && response.data) {
      strategyOptions.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载策略列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.productType = ''
  searchForm.riskLevel = null
  searchForm.keyword = ''
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
  ElMessage.info('产品详情功能开发中...')
}

const handleCreate = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

const handleEdit = async (row) => {
  try {
    const response = await getProductDetail(row.id)
    if (response && response.data) {
      isEdit.value = true
      Object.assign(productForm, {
        id: response.data.id,
        productCode: response.data.productCode,
        productName: response.data.productName,
        productType: response.data.productType,
        riskLevel: response.data.riskLevel,
        minInvestment: response.data.minInvestment,
        managementFee: response.data.managementFee ? response.data.managementFee * 100 : null,
        strategyId: response.data.strategyId,
        status: response.data.status,
        description: response.data.description
      })
      formVisible.value = true
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    ElMessage.error('获取产品详情失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const formData = {
      ...productForm,
      managementFee: productForm.managementFee ? productForm.managementFee / 100 : null
    }
    
    if (isEdit.value) {
      await updateProduct(formData.id, formData)
      ElMessage.success('更新产品成功')
    } else {
      await createProduct(formData)
      ElMessage.success('创建产品成功')
    }
    
    formVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(isEdit.value ? '更新产品失败' : '创建产品失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.assign(productForm, {
    productCode: '',
    productName: '',
    productType: '',
    riskLevel: 3,
    minInvestment: null,
    managementFee: null,
    strategyId: null,
    status: 1,
    description: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除产品 "${row.productName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteProduct(row.id)
    ElMessage.success('删除产品成功')
    loadData()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除产品失败:', error)
    ElMessage.error('删除产品失败')
  }
}

const getRiskLevelText = (level) => {
  const levels = { 1: '保守型', 2: '稳健型', 3: '平衡型', 4: '积极型', 5: '激进型' }
  return levels[level] || '-'
}

const getRiskLevelType = (level) => {
  const types = { 1: 'success', 2: 'info', 3: 'warning', 4: 'danger', 5: 'danger' }
  return types[level] || 'info'
}

// 跳转到审核管理
const goToApproval = () => {
  router.push('/product/approval')
}

// 生命周期
onMounted(() => {
  loadData()
  loadStrategies()
})
</script>

<style scoped>
.product-list-page {
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