<template>
  <div class="custom-strategy-page">
    <!-- Header -->
    <div class="page-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/strategy/list' }">策略管理</el-breadcrumb-item>
        <el-breadcrumb-item>自定义策略</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button type="primary" @click="runBacktest" :loading="loading" :icon="Promotion" size="large">
        运行回测
      </el-button>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Left Panel: Code Editor and Params -->
      <div class="left-panel">
        <el-card class="editor-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>Python 策略代码</span>
            </div>
          </template>
          <codemirror
            v-model="form.code"
            placeholder="## 请在此处编写Python策略代码..."
            :style="{ height: '100%' }"
            :autofocus="true"
            :indent-with-tab="true"
            :tab-size="4"
            :extensions="extensions"
          />
        </el-card>
      </div>

      <!-- Right Panel: Params and Results -->
      <div class="right-panel">
        <div class="params-card">
           <el-divider>回测参数</el-divider>
            <el-form :model="form" label-position="top">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Symbol">
                    <el-input v-model="form.symbol" placeholder="例如: MOCK" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="初始资金">
                    <el-input-number v-model="form.initial_capital" :min="1000" style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="开始日期">
                    <el-date-picker v-model="form.start_date" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="结束日期">
                    <el-date-picker v-model="form.end_date" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
        </div>
        <el-card class="result-card" shadow="never">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="回测结果" name="results">
              <div v-if="result" class="result-content">
                <div class="metrics-grid">
                  <div class="metric-item">
                    <span class="metric-label">累计收益率</span>
                    <span class="metric-value positive">{{ (result.cumulative_return * 100).toFixed(2) }}%</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">年化收益率</span>
                    <span class="metric-value positive">{{ (result.annualized_return * 100).toFixed(2) }}%</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">最大回撤</span>
                    <span class="metric-value negative">{{ (result.max_drawdown * 100).toFixed(2) }}%</span>
                  </div>
                   <div class="metric-item">
                    <span class="metric-label">夏普比率</span>
                    <span class="metric-value">{{ result.sharpe_ratio.toFixed(2) }}</span>
                  </div>
                </div>
                <div ref="chartRef" class="chart-area"></div>
              </div>
              <el-empty v-else description="等待回测结果" />
            </el-tab-pane>
            <el-tab-pane label="日志" name="logs">
              <el-empty description="暂无日志" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { Codemirror } from 'vue-codemirror'
import { python } from '@codemirror/lang-python'
import { oneDark } from '@codemirror/theme-one-dark'

const extensions = [python(), oneDark]
const activeTab = ref('results')

const form = reactive({
  code: `def generate_signal(price_df, params):\n    # 示例: 简单动量策略，收盘价比前一天高则买入\n    signal = (price_df['close'].diff() > 0).astype(int)\n    return signal`,
  symbol: 'MOCK',
  start_date: '2023-01-01',
  end_date: '2023-12-31',
  initial_capital: 1000000
})

const loading = ref(false)
const result = ref(null)
const chartRef = ref(null)
let chartInstance = null

const runBacktest = async () => {
  loading.value = true
  result.value = null; 
  activeTab.value = 'results';
  try {
    const res = await fetch('/api/strategy/run', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    })
    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.detail || '回测服务出错');
    }
    const data = await res.json()
    if (data.success) {
      result.value = data.result
      ElMessage.success('回测成功！')
      await nextTick()
      renderChart()
    } else {
      throw new Error(data.detail || '回测失败');
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!result.value || !chartRef.value) return
  if (chartInstance) {
    chartInstance.dispose();
  }
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption({
    title: { text: '策略净值曲线', left: 'center', textStyle: { fontWeight: 'normal', fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: result.value.dates },
    yAxis: { type: 'value', scale: true },
    grid: { left: '10%', right: '5%', bottom: '10%' },
    series: [{
      data: result.value.equity_curve,
      type: 'line',
      smooth: true,
      name: '净值',
      symbol: 'none',
      lineStyle: { color: '#409EFF', width: 2 }
    }]
  })
}

onMounted(() => {
    // Optional: Pre-render chart container if needed
});
</script>

<style scoped>
.custom-strategy-page {
  background: #f0f2f5;
  padding: 24px;
  height: calc(100vh - 50px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.main-content {
  display: flex;
  gap: 24px;
  flex-grow: 1;
  height: 100%;
}

.left-panel {
  width: 60%;
  display: flex;
  flex-direction: column;
}
.right-panel {
  width: 40%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.editor-card {
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  height: 100%;
  padding: 0;
}

.params-card {
  padding: 0 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
}

.result-card {
  width: 100%;
  flex-grow: 1;
}

.result-content {
  padding-top: 10px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.metric-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
  padding: 10px 15px;
  border-radius: 4px;
}
.metric-label {
  color: #606266;
}
.metric-value {
  font-weight: bold;
  font-size: 1.1em;
}
.positive { color: #67c23a; }
.negative { color: #f56c6c; }

.chart-area {
  width: 100%;
  height: 250px; /* Adjusted height */
}
</style> 