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
      <div class="top-content">
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

        <!-- Right Panel: Params -->
        <div class="right-panel">
          <div class="params-card">
             <el-divider>回测参数</el-divider>
              <el-form :model="form" label-position="top">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="Symbol">
                      <el-input v-model="form.symbol" placeholder="例如: 0000001" />
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
        </div>
      </div>

      <el-card class="result-card" shadow="never">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="回测结果" name="results">
            <div v-if="result" class="result-content">
              <div class="overview-title">收益概述</div>
              <div class="metrics-grid">
                <div class="metric-item">
                  <span class="metric-label">策略收益</span>
                  <span class="metric-value positive">{{ (result.strategy_return * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">策略年化收益</span>
                  <span class="metric-value positive">{{ (result.annualized_return * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">超额收益</span>
                  <span class="metric-value positive">{{ (result.excess_return * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">基准收益</span>
                  <span class="metric-value negative">{{ (result.benchmark_return * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">阿尔法</span>
                  <span class="metric-value">{{ result.alpha.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">贝塔</span>
                  <span class="metric-value">{{ result.beta.toFixed(3) }}</span>
                </div>
                 <div class="metric-item">
                  <span class="metric-label">夏普比率</span>
                  <span class="metric-value">{{ result.sharpe_ratio.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">胜率</span>
                  <span class="metric-value">{{ result.win_rate.toFixed(3) }}</span>
                </div>
                 <div class="metric-item">
                  <span class="metric-label">盈亏比</span>
                  <span class="metric-value">{{ result.profit_loss_ratio.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">最大回撤</span>
                  <span class="metric-value negative">{{ (result.max_drawdown * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">索提诺比率</span>
                  <span class="metric-value">{{ result.sortino_ratio.toFixed(3) }}</span>
                </div>
              </div>
              <div class="metrics-grid">
                 <div class="metric-item">
                  <span class="metric-label">日均超额收益</span>
                  <span class="metric-value positive">{{ (result.avg_daily_excess_return * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">超额收益最大回撤</span>
                  <span class="metric-value negative">{{ (result.max_drawdown_excess * 100).toFixed(2) }}%</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">超额收益夏普比率</span>
                  <span class="metric-value">{{ result.sharpe_ratio_excess.toFixed(3) }}</span>
                </div>
                 <div class="metric-item">
                  <span class="metric-label">日胜率</span>
                  <span class="metric-value">{{ result.daily_win_rate.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">盈利次数</span>
                  <span class="metric-value">{{ result.winning_trades }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">亏损次数</span>
                  <span class="metric-value">{{ result.losing_trades }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">信息比率</span>
                  <span class="metric-value">{{ result.information_ratio.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">策略波动率</span>
                  <span class="metric-value">{{ result.strategy_volatility.toFixed(3) }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">基准波动率</span>
                  <span class="metric-value">{{ result.benchmark_volatility.toFixed(3) }}</span>
                </div>
                <div class="metric-item wide">
                  <span class="metric-label">最大回撤区间</span>
                  <span class="metric-value">{{ result.max_drawdown_period }}</span>
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
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
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
  symbol: '000001',
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

  // 模拟API调用和延迟
  await new Promise(resolve => setTimeout(resolve, 1000));

  try {
    const dataPoints = 500; // 增加数据点

    // 生成更真实的随机游走数据
    const generateRandomWalk = (length, volatility, trend) => {
      const data = [1];
      for (let i = 1; i < length; i++) {
        const randomFactor = (Math.random() - 0.49) * volatility;
        const nextValue = data[i - 1] * (1 + randomFactor + trend);
        data.push(nextValue);
      }
      return data;
    };

    // 生成日期
    const dates = [];
    let currentDate = new Date(form.start_date);
    for (let i = 0; i < dataPoints; i++) {
      dates.push(currentDate.toISOString().split('T')[0]);
      currentDate.setDate(currentDate.getDate() + 1);
    }
    
    const equity_curve = generateRandomWalk(dataPoints, 0.05, 0.0008);
    const benchmark_curve = generateRandomWalk(dataPoints, 0.04, 0.0002);

    const daily_returns = [];
    for (let i = 0; i < equity_curve.length; i++) {
      if (i === 0) {
        daily_returns.push(0);
      } else {
        daily_returns.push(equity_curve[i] / equity_curve[i - 1] - 1);
      }
    }

    // 使用固定的假数据
    const data = {
      success: true,
      result: {
        strategy_return: 0.3151,
        annualized_return: 0.1520,
        excess_return: 0.2769,
        benchmark_return: 0.0299,
        alpha: 0.135,
        beta: 0.930,
        sharpe_ratio: 0.309,
        win_rate: 0.546,
        profit_loss_ratio: 1.238,
        max_drawdown: 0.3754,
        sortino_ratio: 0.397,
        avg_daily_excess_return: 0.0007,
        max_drawdown_excess: 0.3780,
        sharpe_ratio_excess: 0.295,
        daily_win_rate: 0.558,
        winning_trades: 137,
        losing_trades: 114,
        information_ratio: 0.426,
        strategy_volatility: 0.363,
        benchmark_volatility: 0.183,
        max_drawdown_period: '2023/11/29,2024/09/18',
        dates: dates,
        equity_curve: equity_curve,
        benchmark_curve: benchmark_curve,
        daily_returns: daily_returns,
      }
    };

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
  const upColor = '#ec0000';
  const downColor = '#00da3c';

  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['策略收益', '沪深300'],
      top: 10
    },
    grid: [
      {
        left: '10%',
        right: '8%',
        height: '50%'
      },
      {
        left: '10%',
        right: '8%',
        top: '65%',
        height: '16%'
      }
    ],
    xAxis: [
      {
        type: 'category',
        data: result.value.dates,
        scale: true,
        boundaryGap: false,
        axisLine: { onZero: false },
      },
      {
        type: 'category',
        gridIndex: 1,
        data: result.value.dates,
        boundaryGap: false,
        axisLine: { onZero: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
      }
    ],
    yAxis: [
      {
        scale: true,
        axisLabel: {
          formatter: (value) => (value * 100).toFixed(0) + '%'
        },
        splitArea: {
          show: true
        }
      },
      {
        scale: true,
        gridIndex: 1,
        splitNumber: 2,
        axisLabel: { show: false },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '策略收益',
        type: 'line',
        data: result.value.equity_curve,
        smooth: true,
        lineStyle: {
          width: 2
        }
      },
      {
        name: '沪深300',
        type: 'line',
        data: result.value.benchmark_curve,
        smooth: true,
        lineStyle: {
          width: 2
        }
      },
      {
        name: '每日盈亏',
        type: 'bar',
        xAxisIndex: 1,
        yAxisIndex: 1,
        data: result.value.daily_returns.map(item => ({
          value: item,
          itemStyle: {
            color: item > 0 ? upColor : downColor
          }
        }))
      }
    ]
  });
}

watch(activeTab, (newValue) => {
  if (newValue === 'results' && chartInstance) {
    nextTick(() => {
      chartInstance.resize();
    });
  }
});

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
  flex-direction: column;
  gap: 24px;
  flex-grow: 1;
  height: 100%;
  overflow-y: auto;
}

.top-content {
  display: flex;
  gap: 24px;
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
  min-height: 400px;
}

:deep(.el-card__body) {
  height: 100%;
  padding: 0;
}

.params-card {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
}

.params-card .el-divider {
  margin: 0 0 20px;
}

.result-card {
  width: 100%;
}

.result-content {
  padding: 20px;
}

.overview-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.metric-item {
  display: flex;
  flex-direction: column;
}

.metric-item.wide {
  grid-column: span 2;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.metric-value.positive {
  color: #f56c6c;
}

.metric-value.negative {
  color: #67c23a;
}

.chart-area {
  width: 100%;
  height: 400px;
  margin-top: 20px;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}
</style>
