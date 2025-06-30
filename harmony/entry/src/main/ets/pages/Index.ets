// 数据模型定义
interface NavItemData {
  icon: string;
  text: string;
}
interface StatCardData {
  title: string;
  value: string;
  change: string;
  isPositive: boolean;
}
interface StrategyData {
  title: string;
  type: string;
  performance: string;
  isPositive: boolean;
}
interface TaskData {
  title: string;
  time: string;
  priority: '高' | '中' | '低';
}
interface QuickActionData {
  emoji: string;
  text: string;
  color: ResourceColor;
}

// 主入口组件
@Entry
@Component
struct Index {
  // 此 State 变量现在主要用于通知框架数据变化，Tabs的切换由其自身管理
  @State selectedIndex: number = 0;
  private controller: TabsController = new TabsController();

  private navItems: NavItemData[] = [
    { icon: '💼', text: '工作台' },
    { icon: '🔬', text: '基金研究' },
    { icon: '⚙️', text: '因子管理' },
    { icon: '📈', text: '策略管理' },
    { icon: '📦', text: '组合产品' },
    { icon: '🔄', text: '交易管理' },
  ];

  build() {
    // 根组件直接使用 Tabs，这是最标准的顶部导航布局
    Tabs({ barPosition: BarPosition.Start, controller: this.controller }) {
      ForEach(this.navItems, (item: NavItemData, index: number) => {
        // 每个 ForEach 循环项生成一个 Tab

        // 设置单个 Tab 标签的样式
        .padding({ left: 15, right: 15, top: 12, bottom: 12 })
        .id(`tab${index}`)

      }, (item: NavItemData) => item.text) // 使用 item.text 作为唯一的 key
    }
    .barMode(BarMode.Scrollable)
    .onChange((index: number) => {
      this.selectedIndex = index;
    })
    .width('100%')
    .height('100%')
    .shadow({ radius: 6, color: '#1F000000', offsetY: 2 })
  }
}

// MainContent 组件，现在只负责渲染“工作台”的UI，不关心布局
@Component
struct MainContent {
  private statCards: StatCardData[] = [
    { title: '创业板指', value: '2,789.34', change: '+1.24%', isPositive: true },
    { title: '沪深300', value: '4,123.67', change: '+0.56%', isPositive: true },
  ];
  private strategies: StrategyData[] = [
    { title: '稳健成长策略', type: '混合型', performance: '+8.45%', isPositive: true },
    { title: '价值投资策略', type: '股票型', performance: '+12.78%', isPositive: true },
    { title: '平衡配置策略', type: 'FOF', performance: '+6.23%', isPositive: true },
    { title: '量化选股策略', type: '股票型', performance: '-2.15%', isPositive: false },
  ];
  private tasks: TaskData[] = [
    { title: '处理交易差错单', time: '4小时前', priority: '中' },
    { title: '更新因子库数据', time: '1天前', priority: '低' },
    { title: '策略回测报告审核', time: '2天前', priority: '中' },
  ];
  private quickActions: QuickActionData[] = [
    { emoji: '➕', text: '创建基金组合', color: '#3498db' },
    { emoji: '📊', text: '策略回测', color: '#95a5a6' },
    { emoji: '🔍', text: '因子分析', color: '#2ecc71' },
    { emoji: '▶️', text: '交易执行', color: '#f39c12' },
  ];
  build() {
    Column({ space: $r('app.float.page_padding') }) {
      this.Header()
      ForEach(this.statCards, (item: StatCardData) => {
        this.StatCard(item)
      }, item => item.title)
      this.StrategyCard(this.strategies)
      this.TaskCard(this.tasks)
      this.QuickActionCard(this.quickActions)
    }
    .padding($r('app.float.page_padding'))
  }

  @Builder Header() {
    Row() {
      Text('工作台')
        .fontSize(28).fontWeight(FontWeight.Bold).fontColor($r('app.color.font_primary'))
      Row({ space: 8 }) {
        Text('👨‍💼').fontSize(28)
        Text('投顾经理').fontSize(16).fontColor($r('app.color.font_primary'))
      }
    }
    .width('100%').justifyContent(FlexAlign.SpaceBetween).alignItems(VerticalAlign.Center)
  }

  @Builder StatCard(item: StatCardData) {
    Column({ space: 8 }) {
      Text(item.title).fontSize(16).fontColor($r('app.color.font_secondary'))
      Text(item.value).fontSize(28).fontWeight(FontWeight.Bold).fontColor($r('app.color.font_primary'))
      Text(item.change).fontSize(14).fontColor(item.isPositive ? $r('app.color.positive_green') : $r('app.color.negative_red'))
    }
    .width('100%').alignItems(HorizontalAlign.Start).padding(20).backgroundColor($r('app.color.card_bg')).borderRadius($r('app.float.card_radius'))
  }

  @Builder StrategyCard(items: StrategyData[]) {
    Column({ space: 15 }) {
      Text('策略表现').fontSize(18).fontWeight(FontWeight.Bold)
      ForEach(items, (item: StrategyData) => {
        Row() {
          Column({ space: 4 }) {
            Text(item.title).fontSize(16).fontWeight(FontWeight.Medium)
            Text(item.type).fontSize(12).fontColor($r('app.color.font_secondary'))
          }.alignItems(HorizontalAlign.Start)
          Text(item.performance).fontSize(16).fontWeight(FontWeight.Medium)
            .fontColor(item.isPositive ? $r('app.color.positive_green') : $r('app.color.negative_red'))
        }
        .justifyContent(FlexAlign.SpaceBetween).width('100%')
      }, item => item.title)
    }
    .width('100%').alignItems(HorizontalAlign.Start).padding(20).backgroundColor($r('app.color.card_bg')).borderRadius($r('app.float.card_radius'))
  }

  @Builder TaskCard(items: TaskData[]) {
    Column({ space: 15 }) {
      Text('待办事项').fontSize(18).fontWeight(FontWeight.Bold)
      ForEach(items, (item: TaskData) => {
        Row() {
          Column({ space: 4 }) {
            Text(item.title).fontSize(16)
            Text(item.time).fontSize(12).fontColor($r('app.color.font_secondary'))
          }.alignItems(HorizontalAlign.Start)
          Text(item.priority).padding({ left: 8, right: 8, top: 2, bottom: 2 }).fontSize(12)
            .fontColor(item.priority === '高' ? '#e74c3c' : '#f39c12')
            .backgroundColor(item.priority === '高' ? '#fdeded' : '#fef5e7').borderRadius(4)
        }
        .justifyContent(FlexAlign.SpaceBetween).width('100%')
      }, item => item.title)
    }
    .width('100%').alignItems(HorizontalAlign.Start).padding(20).backgroundColor($r('app.color.card_bg')).borderRadius($r('app.float.card_radius'))
  }

  @Builder QuickActionCard(items: QuickActionData[]) {
    Column({ space: 12 }) {
      Text('快捷操作').fontSize(18).fontWeight(FontWeight.Bold)
      ForEach(items, (item: QuickActionData) => {
        Button() {
          Row({ space: 10 }) {
            Text(item.emoji).fontSize(20)
            Text(item.text).fontSize(16).fontColor(Color.White)
          }
        }
        .width('100%').height(48).backgroundColor(item.color).borderRadius(8)
      }, item => item.text)
    }
    .width('100%').alignItems(HorizontalAlign.Start).padding(20).backgroundColor($r('app.color.card_bg')).borderRadius($r('app.float.card_radius'))
  }
}