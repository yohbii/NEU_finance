// --- 数据模型定义 ---
type StrategyType = '大类资产' | 'FOF组合' | '基金指数' | '择时组合';
type StrategyStatus = '运行中' | '已暂停' | '回测中';

interface Strategy {
  id: string;
  name: string;
  type: StrategyType;
  annualizedReturn: number;
  status: StrategyStatus;
}

// --- 静态模拟数据 ---
const MOCK_STRATEGIES: Strategy[] = [
  { id: 's1', name: '稳健型资产配置', type: '大类资产', annualizedReturn: 8.7, status: '运行中' },
  { id: 's2', name: '全明星基金经理FOF', type: 'FOF组合', annualizedReturn: 15.2, status: '运行中' },
  { id: 's3', name: '沪深300指数增强', type: '基金指数', annualizedReturn: 12.5, status: '已暂停' },
  { id: 's4', name: '双均线择时模型', type: '择时组合', annualizedReturn: 18.9, status: '回测中' },
];

const STRATEGY_TYPES: { type: StrategyType, description: string, steps: number }[] = [
  { type: '大类资产', description: '通过经典模型配置大类资产', steps: 4 },
  { type: 'FOF组合', description: '精选基金构建组合', steps: 3 },
  { type: '基金指数', description: '跟踪并增强指数表现', steps: 4 },
  { type: '择时组合', description: '根据市场信号进行买卖', steps: 3 },
];

// --- UI 组件 ---

/**
 * 策略创建向导
 */
@Component
struct StrategyCreationWizard {
  @Link showWizard: boolean;
  @Prop strategyType: StrategyType;
  @Prop totalSteps: number;

  @State currentStep: number = 0;

  build() {
    Column() {
      // 1. 顶部栏
      Row() {
        Image($r("app.media.ic_close")).width(24).height(24).onClick(() => this.showWizard = false)
        Text(`创建${this.strategyType}`).fontSize(20).fontWeight(FontWeight.Bold).layoutWeight(1).textAlign(TextAlign.Center)
        // 占位，保持标题居中
        Image($r("app.media.ic_close")).width(24).height(24).opacity(0)
      }
      .width('100%').padding(15).backgroundColor(Color.White)
      .shadow({ radius: 4, color: '#1F000000', offsetY: 2 })

      // 2. 步进器
      Stepper({ index: this.currentStep }) {
        ForEach(Array.from({ length: this.totalSteps }, (_, i) => i + 1), (stepNum: number) => {
          StepperItem() { Text(`步骤 ${stepNum}`) }
        })
      }.margin(15)

      // 3. 动态内容区
      Column() {
        // 使用 switch 根据当前步骤显示不同内容
        // 这里仅作示意
        switch (this.currentStep) {
          case 0:
            Text("第一步：填写基本信息").fontSize(18)
            TextInput({placeholder: '策略名称'})
            break;
          case 1:
            Text("第二步：选择标的/因子").fontSize(18)
            break;
          case 2:
            Text("第三步：设置权重/规则").fontSize(18)
            break;
          case 3:
            Text("第四步：配置再平衡").fontSize(18)
            break;
        }
      }.layoutWeight(1).justifyContent(FlexAlign.Center)


      // 4. 底部导航按钮
      Row({ space: 10 }) {
        if (this.currentStep > 0) {
          Button("上一步").onClick(() => this.currentStep--).layoutWeight(1).type(ButtonType.Normal)
        }
        Button(this.currentStep === this.totalSteps - 1 ? "完成创建" : "下一步")
          .onClick(() => {
            if (this.currentStep === this.totalSteps - 1) {
              this.showWizard = false; // 完成
            } else {
              this.currentStep++; // 下一步
            }
          })
          .layoutWeight(1).type(ButtonType.Capsule)
      }
      .width('100%').padding(15)

    }
    .width('100%').height('100%').backgroundColor('#f5f5f5')
  }
}

/**
 * 主页面
 */
@Entry
@Component
struct StrategyManagementPage {
  @State strategies: Strategy[] = MOCK_STRATEGIES;
  @State showCreationSheet: boolean = false; // 控制底部选择面板
  @State showWizard: boolean = false; // 控制创建向导
  @State selectedStrategyToCreate: { type: StrategyType, steps: number } | null = null;

  // 策略状态对应的颜色
  private statusColor(status: StrategyStatus): Color {
    switch (status) {
      case '运行中': return Color.Green;
      case '已暂停': return Color.Gray;
      case '回测中': return Color.Blue;
    }
  }

  build() {
    Stack() {
      Column() {
        // 页面标题
        Text("策略管理").fontSize(28).fontWeight(FontWeight.Bold).padding(15).alignSelf(FlexAlign.Start)
        // 策略列表
        List({ space: 15 }) {
          ForEach(this.strategies, (strategy: Strategy) => {
            ListItem() {
              this.StrategyCard(strategy)
            }
          })
        }.padding({ horizontal: 15 })

      }.width('100%').height('100%')

      // 创建按钮
      Button({ type: ButtonType.Circle }) {
        Text("+").fontSize(30).fontColor(Color.White)
      }
      .width(56).height(56).position({ x: '85%', y: '88%' })
      .shadow({ radius: 6, color: '#33000000' })
      .onClick(() => { this.showCreationSheet = true })

      // 策略创建向导 (条件渲染)
      if (this.showWizard && this.selectedStrategyToCreate) {
        StrategyCreationWizard({
          showWizard: $showWizard,
          strategyType: this.selectedStrategyToCreate.type,
          totalSteps: this.selectedStrategyToCreate.steps
        })
          .transition(TransitionEffect.move(TransitionEdge.BOTTOM))
      }
    }
    .sheet(isPresented: $showCreationSheet) { // 底部选择面板
      this.CreationTypeSheet()
    }
    .animation({ duration: 300, curve: Curve.EaseInOut })
  }

  // 单个策略卡片
  @Builder StrategyCard(strategy: Strategy) {
    Column({ space: 12 }) {
      // 卡片头部
      Row() {
        Text(strategy.name).fontSize(18).fontWeight(FontWeight.Bold).layoutWeight(1)
        Text(strategy.type).fontSize(12).padding({ horizontal: 8, vertical: 4 })
          .backgroundColor('#f0f0f0').borderRadius(8)
      }
      // 卡片中部
      Row() {
        Column {
          Text("年化收益").fontSize(12).fontColor(Color.Gray)
          Text(`${strategy.annualizedReturn > 0 ? '+' : ''}${strategy.annualizedReturn.toFixed(1)}%`)
            .fontSize(20).fontWeight(FontWeight.Bold)
            .fontColor(strategy.annualizedReturn > 0 ? Color.Red : Color.Green)
        }.alignItems(HorizontalAlign.Start)

        Row({ space: 5 }) {
          Circle().fill(this.statusColor(strategy.status)).width(8).height(8)
          Text(strategy.status).fontSize(14)
        }.alignItems(VerticalAlign.Center)
      }.justifyContent(FlexAlign.SpaceBetween)
      // 卡片底部操作栏
      Divider().margin({ vertical: 8 })
      Row({ space: 10 }) {
        Button("详情").layoutWeight(1).type(ButtonType.Normal)
        Button("回测").layoutWeight(1).type(ButtonType.Normal)
        Button("调仓").layoutWeight(1).type(ButtonType.Normal)
      }.width('100%')
    }
    .padding(15).backgroundColor(Color.White).borderRadius(12)
  }


  // 创建类型选择面板
  @Builder CreationTypeSheet() {
    Column({ space: 15 }) {
      Text("选择要创建的策略类型").fontSize(20).fontWeight(FontWeight.Bold)
      ForEach(STRATEGY_TYPES, (item) => {
        Column({ space: 5 }) {
          Text(item.type).fontSize(18).fontWeight(FontWeight.Medium)
          Text(item.description).fontSize(14).fontColor(Color.Gray)
        }
        .width('100%').padding(15).backgroundColor('#f5f5f5').borderRadius(10)
        .onClick(() => {
          this.showCreationSheet = false;
          this.selectedStrategyToCreate = item;
          // 延迟一小段时间再显示向导，避免与sheet关闭动画冲突
          setTimeout(() => {
            this.showWizard = true;
          }, 300)
        })
      })
    }
    .padding(20).width('100%')
  }
}