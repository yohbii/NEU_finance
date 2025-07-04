<template>
  <div class="product-approval-page">
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/product/list' }">组合产品</el-breadcrumb-item>
        <el-breadcrumb-item>产品审核</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="approval-content">
      <!-- 审核统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ approvalStats.pending }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon approved">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ approvalStats.approved }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon rejected">
                <el-icon><Close /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ approvalStats.rejected }}</div>
                <div class="stat-label">已拒绝</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon total">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ approvalStats.total }}</div>
                <div class="stat-label">总计</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 审核列表 -->
      <el-card class="approval-list-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3><el-icon><List /></el-icon> 产品审核列表</h3>
            <div class="header-actions">
              <el-select v-model="filterStatus" placeholder="审核状态" style="width: 150px;">
                <el-option label="全部" value="" />
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
              <el-button @click="loadApprovalList" :icon="RefreshRight">刷新</el-button>
            </div>
          </div>
        </template>

        <el-table
          :data="approvalList"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="productCode" label="产品代码" width="120" />
          <el-table-column prop="productName" label="产品名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="productType" label="产品类型" width="120" />
          <el-table-column prop="submitter" label="提交人" width="120" />
          <el-table-column prop="submitTime" label="提交时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.submitTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="审核状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewer" label="审核人" width="120" />
          <el-table-column prop="reviewTime" label="审核时间" width="180">
            <template #default="{ row }">
              {{ row.reviewTime ? formatDateTime(row.reviewTime) : '--' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="viewDetail(row)">
                详情
              </el-button>
              <el-button 
                v-if="row.status === 'pending'" 
                type="success" 
                size="small" 
                @click="approveProduct(row)"
              >
                审核
              </el-button>
              <el-button 
                v-if="row.status === 'pending'" 
                type="danger" 
                size="small" 
                @click="rejectProduct(row)"
              >
                拒绝
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
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 产品详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="产品审核详情"
      width="1000px"
      :close-on-click-modal="false"
    >
      <div v-if="currentProduct" class="product-detail">
        <!-- 产品基本信息 -->
        <el-card class="detail-section">
          <template #header>
            <h4><el-icon><InfoFilled /></el-icon> 产品基本信息</h4>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="产品代码">
              {{ currentProduct.productCode }}
            </el-descriptions-item>
            <el-descriptions-item label="产品名称">
              {{ currentProduct.productName }}
            </el-descriptions-item>
            <el-descriptions-item label="产品类型">
              {{ currentProduct.productType }}
            </el-descriptions-item>
            <el-descriptions-item label="风险等级">
              <el-rate v-model="currentProduct.riskLevel" disabled show-score />
            </el-descriptions-item>
            <el-descriptions-item label="最小投资金额">
              {{ currentProduct.minInvestment ? `${currentProduct.minInvestment}元` : '--' }}
            </el-descriptions-item>
            <el-descriptions-item label="管理费率">
              {{ currentProduct.managementFee ? `${(currentProduct.managementFee * 100).toFixed(2)}%` : '--' }}
            </el-descriptions-item>
          </el-descriptions>
          <el-divider />
          <div class="description-section">
            <h5>产品描述</h5>
            <p>{{ currentProduct.description || '暂无描述' }}</p>
          </div>
        </el-card>

        <!-- 合规检查 -->
        <el-card class="detail-section">
          <template #header>
            <h4><el-icon><Lock /></el-icon> 合规性检查</h4>
          </template>
          <div class="compliance-checks">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="check-item">
                  <el-icon class="check-icon success"><CircleCheck /></el-icon>
                  <span>产品信息完整性检查</span>
                  <el-tag type="success" size="small">通过</el-tag>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="check-item">
                  <el-icon class="check-icon success"><CircleCheck /></el-icon>
                  <span>风险等级评估</span>
                  <el-tag type="success" size="small">通过</el-tag>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="check-item">
                  <el-icon class="check-icon success"><CircleCheck /></el-icon>
                  <span>投资策略合规性</span>
                  <el-tag type="success" size="small">通过</el-tag>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="check-item">
                  <el-icon class="check-icon warning"><Warning /></el-icon>
                  <span>费率合理性检查</span>
                  <el-tag type="warning" size="small">需关注</el-tag>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>

        <!-- 审核流程 -->
        <el-card class="detail-section">
          <template #header>
            <h4><el-icon><Operation /></el-icon> 审核流程</h4>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in approvalSteps"
              :key="index"
              :timestamp="step.timestamp"
              :type="step.type"
              :icon="step.icon"
            >
              <h4>{{ step.title }}</h4>
              <p>{{ step.description }}</p>
              <p v-if="step.reviewer"><strong>操作人：</strong>{{ step.reviewer }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button 
            v-if="currentProduct && currentProduct.status === 'pending'" 
            type="success" 
            @click="approveProduct(currentProduct)"
          >
            通过审核
          </el-button>
          <el-button 
            v-if="currentProduct && currentProduct.status === 'pending'" 
            type="danger" 
            @click="rejectProduct(currentProduct)"
          >
            拒绝审核
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核操作弹窗 -->
    <el-dialog
      v-model="approvalVisible"
      :title="approvalAction === 'approve' ? '审核通过' : '审核拒绝'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="审核结果">
          <el-tag :type="approvalAction === 'approve' ? 'success' : 'danger'" size="large">
            {{ approvalAction === 'approve' ? '通过' : '拒绝' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item v-if="approvalAction === 'reject'" label="拒绝原因" prop="rejectReason">
          <el-select v-model="approvalForm.rejectReason" placeholder="请选择拒绝原因" style="width: 100%">
            <el-option label="产品信息不完整" value="incomplete_info" />
            <el-option label="风险等级不匹配" value="risk_mismatch" />
            <el-option label="投资策略不合规" value="strategy_non_compliant" />
            <el-option label="费率设置不合理" value="unreasonable_fee" />
            <el-option label="其他原因" value="other" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="approvalVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitApproval" 
            :loading="submitting"
          >
            提交审核
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Clock,
  Check,
  Close,
  Document,
  List,
  RefreshRight,
  InfoFilled,
  Lock,
  Operation,
  CircleCheck,
  Warning
} from '@element-plus/icons-vue'
import { getApprovalList, submitApproval as submitApprovalAPI, getApprovalStats } from '@/api/product'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const detailVisible = ref(false)
const approvalVisible = ref(false)
const filterStatus = ref('')
const approvalAction = ref('')
const currentProduct = ref(null)
const approvalFormRef = ref(null)

// 审核统计
const approvalStats = reactive({
  pending: 12,
  approved: 45,
  rejected: 8,
  total: 65
})

// 审核列表
const approvalList = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 审核表单
const approvalForm = reactive({
  comment: '',
  rejectReason: ''
})

// 表单验证规则
const approvalRules = {
  comment: [
    { required: true, message: '请输入审核意见', trigger: 'blur' }
  ],
  rejectReason: [
    { required: true, message: '请选择拒绝原因', trigger: 'change' }
  ]
}

// 审核流程步骤
const approvalSteps = ref([
  {
    title: '产品提交',
    description: '产品信息已提交，等待审核',
    timestamp: '2024-01-15 10:30:00',
    type: 'primary',
    icon: 'Document',
    reviewer: '张三'
  },
  {
    title: '合规检查',
    description: '系统自动进行合规性检查',
    timestamp: '2024-01-15 10:35:00',
    type: 'success',
    icon: 'Shield',
    reviewer: '系统'
  },
  {
    title: '等待审核',
    description: '等待审核人员进行人工审核',
    timestamp: '2024-01-15 10:40:00',
    type: 'warning',
    icon: 'Clock',
    reviewer: '--'
  }
])

// 加载审核列表
const loadApprovalList = async () => {
  try {
    loading.value = true
    
    const params = {
      current: pagination.current,
      size: pagination.size,
      status: filterStatus.value
    }
    
    const response = await getApprovalList(params)
    if (response.code === 200) {
      approvalList.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
    
  } catch (error) {
    console.error('加载审核列表失败:', error)
    ElMessage.error('加载审核列表失败')
  } finally {
    loading.value = false
  }
}

// 加载审核统计
const loadApprovalStats = async () => {
  try {
    const response = await getApprovalStats()
    if (response.code === 200) {
      Object.assign(approvalStats, response.data)
    }
  } catch (error) {
    console.error('加载审核统计失败:', error)
  }
}

// 查看产品详情
const viewDetail = (product) => {
  currentProduct.value = product
  detailVisible.value = true
}

// 审核产品
const approveProduct = (product) => {
  currentProduct.value = product
  approvalAction.value = 'approve'
  approvalForm.comment = ''
  approvalForm.rejectReason = ''
  approvalVisible.value = true
}

// 拒绝产品
const rejectProduct = (product) => {
  currentProduct.value = product
  approvalAction.value = 'reject'
  approvalForm.comment = ''
  approvalForm.rejectReason = ''
  approvalVisible.value = true
}

// 提交审核
const submitApproval = async () => {
  try {
    const valid = await approvalFormRef.value.validate()
    if (!valid) return
    
    submitting.value = true
    
    const data = {
      productId: currentProduct.value.id,
      action: approvalAction.value,
      comment: approvalForm.comment,
      rejectReason: approvalForm.rejectReason
    }
    
    const response = await submitApprovalAPI(data)
    if (response.code === 200) {
      ElMessage.success(`产品${approvalAction.value === 'approve' ? '审核通过' : '审核拒绝'}`)
      approvalVisible.value = false
      detailVisible.value = false
      loadApprovalList()
      loadApprovalStats()
    }
    
  } catch (error) {
    console.error('提交审核失败:', error)
    ElMessage.error('提交审核失败')
  } finally {
    submitting.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return textMap[status] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  loadApprovalList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadApprovalList()
}

// 生命周期
onMounted(() => {
  loadApprovalList()
  loadApprovalStats()
})
</script>

<style scoped>
.product-approval-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: #E6A23C;
}

.stat-icon.approved {
  background: #67C23A;
}

.stat-icon.rejected {
  background: #F56C6C;
}

.stat-icon.total {
  background: #409EFF;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.approval-list-card {
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

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.product-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4, .detail-section h5 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.description-section {
  margin-top: 16px;
}

.description-section h5 {
  margin-bottom: 8px;
}

.description-section p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.compliance-checks {
  padding: 16px 0;
}

.check-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  margin-bottom: 12px;
  background: #FAFAFA;
}

.check-icon {
  font-size: 20px;
}

.check-icon.success {
  color: #67C23A;
}

.check-icon.warning {
  color: #E6A23C;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 12px;
}
</style> 