// --- 数据模型定义 ---
type TradeOrderStatus = 'PENDING_EXECUTION' | 'EXECUTED' | 'REJECTED';
type RebalancingTaskType = 'PORTFOLIO' | 'ERROR_HANDLING' | 'ACCOUNT_SPECIFIC';

interface TradeOrder {
  id: string;
  name: string;
  accountCount: number;
  estimatedAmount: string;
  initiator: string; // 发起人
  status: TradeOrderStatus;
}

interface SettlementSlip {
  id: string;
  account: string;
  fundName: string;
  tradeType: 'BUY' | 'SELL';
  amount: number;
  status: 'SUCCESS' | 'FAILED';
}

// --- 静态模拟数据 ---
const MOCK_ORDERS: TradeOrder[] = [
  { id: 'to1', name: '稳健组合2024Q2季度调仓', accountCount: 120, estimatedAmount: '1,500万', initiator: '投顾A', status: 'PENDING_EXECUTION' },
  { id: 'to2', name: '全明星FOF组合月度调仓', accountCount: 85, estimatedAmount: '980万', initiator: '投顾B', status: 'PENDING_EXECUTION' },
  { id: 'to3', name: '沪深300指数增强pro上新', accountCount: 350, estimatedAmount: '4,200万', initiator: '投顾A', status: 'EXECUTED' },
  { id: 'to4', name: '双均线择时-卖出信号', accountCount: 50, estimatedAmount: '600万', initiator: '系统', status: 'REJECTED' },
];

// --- UI 组件 ---

/**
 * 主页面
 */
@Entry
@Component
struct TradingManagementPage {
  @State selectedTabIndex: number = 0;
  @State selectedFilterIndex: number = 0;

  // 根据状态筛选交易单
  private getFilteredOrders(): TradeOrder[] {
    switch (this.selectedFilterIndex) {
      case 1: return MOCK_ORDERS.filter(o => o.status === 'PENDING_EXECUTION');
      case 2: return MOCK_ORDERS.filter(o => o.status === 'EXECUTED');
      case 3: return MOCK_ORDERS.filter(o => o.status === 'REJECTED');
      default: return MOCK_ORDERS;
    }
  }

  // 状态对应的文本和颜色
  private statusInfo(status: TradeOrderStatus): { text: string, color: Color } {
    switch (status) {
      case 'PENDING_EXECUTION': return { text: '待执行', color: Color.Orange };
      case 'EXECUTED': return { text: '已执行', color: Color.Green };
      case 'REJECTED': return { text: '已回撤', color: Color.Gray };
    }
  }

  build() {
    Column() {
      Text("交易管理").fontSize(28).fontWeight(FontWeight.Bold).padding(15).alignSelf(FlexAlign.Start)
      Tabs({ barPosition: BarPosition.Start }) {
        Tab("交易工作台") { this.TradingWorkbench() }
        Tab("交易单管理") { this.TradeOrderManagement() }
        Tab("调仓任务") { this.RebalancingTasks() }
        Tab("交割单查询") { this.SettlementLookup() }
      }.onChange(index => this.selectedTabIndex = index).layoutWeight(1)
    }
    .width('100%').height('100%').backgroundColor('#f5f5f5')
  }

  // 1. 交易工作台
  @Builder TradingWorkbench() {
    Column({ space: 20 }) {
      this.DashboardCard('待处理交易单', MOCK_ORDERS.filter(o => o.status === 'PENDING_EXECUTION').length.toString(), '立即处理')
      this.DashboardCard('待处理差错', '5', '立即处理', Color.Red) // 假设有5个差错
      // ...可以添加更多摘要信息
    }.padding(15)
  }

  // 工作台摘要卡片
  @Builder DashboardCard(title: string, value: string, buttonText: string, valueColor: Color = Color.Black) {
    Column({ space: 10 }) {
      Text(title).fontSize(16).fontColor(Color.Gray)
      Text(value).fontSize(40).fontWeight(FontWeight.Bold).fontColor(valueColor)
      Button(buttonText).width('100%').type(ButtonType.Capsule)
    }
    .padding(20).backgroundColor(Color.White).borderRadius(12)
  }

  // 2. 交易单管理
  @Builder TradeOrderManagement() {
    Column() {
      // 筛选器
      SegmentedControl({
        segments: [{ value: '全部' }, { value: '待执行' }, { value: '已执行' }, { value: '已回撤' }],
        selectedIndex: this.selectedFilterIndex
      })
        .onChange(event => this.selectedFilterIndex = event.index)
        .margin(15)

      List({ space: 15 }) {
        ForEach(this.getFilteredOrders(), (order: TradeOrder) => {
          ListItem() { this.TradeOrderCard(order) }
        }, order => order.id)
      }.padding({ horizontal: 15 })
    }
  }

  // 单个交易单卡片
  @Builder TradeOrderCard(order: TradeOrder) {
    let sInfo = this.statusInfo(order.status);
    Column({ space: 12 }) {
      Row() {
        Row({ space: 8 }) {
          Circle().fill(sInfo.color).width(10).height(10)
          Text(sInfo.text).fontSize(14).fontColor(sInfo.color).fontWeight(FontWeight.Bold)
        }.alignItems(VerticalAlign.Center)
        Blank()
        Text(`发起人: ${order.initiator}`).fontSize(12).fontColor(Color.Gray)
      }
      Text(order.name).fontSize(18).fontWeight(FontWeight.Bold).width('100%').textAlign(TextAlign.Start)
      Row({ space: 20 }) {
        Text(`涉及账户: ${order.accountCount}`).fontSize(14)
        Text(`预计金额: ${order.estimatedAmount}`).fontSize(14)
      }
      // 动态操作按钮
      if (order.status === 'PENDING_EXECUTION') {
        Divider().margin({ vertical: 8 })
        Row({ space: 10 }) {
          Button("一键回撤").layoutWeight(1).type(ButtonType.Normal).fontColor(Color.Red)
          Button("一键下单").layoutWeight(1).type(ButtonType.Capsule)
        }.width('100%')
      }
    }
    .padding(15).backgroundColor(Color.White).borderRadius(12)
  }

  // 3. 调仓任务
  @Builder RebalancingTasks() {
    Column({ space: 15 }) {
      // 此处可使用Tabs或SegmentedControl进行细分
      Text("组合调仓").fontSize(18).fontWeight(FontWeight.Medium)
      Text("此处列出由投顾发起的、还未生成交易单的顶层调仓计划。").fontColor(Color.Gray)
      Divider()
      Text("差错处理").fontSize(18).fontWeight(FontWeight.Medium)
      Text("某某组合调仓失败 (5个账户)").padding(10).backgroundColor(Color.White).borderRadius(8)
      Divider()
      Text("账户调仓").fontSize(18).fontWeight(FontWeight.Medium)
      TextInput({ placeholder: '输入财富账户ID进行独立调仓...' })
    }.padding(15).alignItems(HorizontalAlign.Start)
  }

  // 4. 交割单查询
  @Builder SettlementLookup() {
    Column({ space: 15 }) {
      TextInput({ placeholder: '按财富账户查询...' })
      TextInput({ placeholder: '按基金代码查询...' })
      Button("查询").width('100%')
      Text("查询结果列表将在此处显示...").fontColor(Color.Gray).margin({ top: 20 })
    }.padding(15)
  }
}