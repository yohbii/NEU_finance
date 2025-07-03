@Entry
@Component
struct IndexPage {
  build() {
    Scroll() {
      Column() {
        // 统计卡片区域
        Flex({ direction: FlexDirection.Row, wrap: FlexWrap.Wrap }) {
          this.summaryCard('🎒', '2,458', '管理基金')
          this.summaryCard('📈', '156', '活跃策略')
          this.summaryCard('📦', '89', '在管产品')
          this.summaryCard('💹', '234', '今日交易')
        }
        .margin(12)

        // 市场概览区域
        Column() {
          Text('市场概览')
            .fontSize(20)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 8 })
          Text('本部分展示了关键市场指数的最新动态，帮助您快速把握市场整体走势。所有数据均为实时更新。')
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 12 })
          Row({ space: 12 }) {
            this.marketCard('上证指数', '3,247.52', '  +0.85%', '#E53935')
            this.marketCard('深证成指', '12,456.78', '  -0.32%', '#43A047')
          }
        }
        .backgroundColor(Color.White)
        .borderRadius(12)
        .padding(16)
        .margin({ left: 12, right: 12, bottom: 16 })

        // 策略表现区域
        Column() {
          Text('策略表现')
            .fontSize(20)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 8 })
          Text('通过下方的折线图，您可以直观地追踪核心投资策略组合的近期收益表现，并与基准指数进行对比。')
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 12 })

          // 调用新的 performanceChart 构建器
          this.performanceChart()

          // 图例
          Row({ space: 24 }) {
            this.chartLegend('#007DFF', '核心策略')
            this.chartLegend('#CCCCCC', '基准指数')
          }
          .margin({ top: 16 })
          .justifyContent(FlexAlign.Center)

        }
        .backgroundColor(Color.White)
        .borderRadius(12)
        .padding(16)
        .margin({ left: 12, right: 12, bottom: 16 })

        // 待办事项区域
        Column() {
          Text('待办事项')
            .fontSize(20)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 8 })
          Text('这里汇总了您当前需要处理的紧急和重要任务，请及时跟进。')
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 12 })
          this.todoItem('审核基金产品上架申请', '2小时前', '高', '#E53935')
          this.todoItem('处理交易系统异常单', '4小时前', '中', '#FF9800')
        }
        .backgroundColor(Color.White)
        .borderRadius(12)
        .padding(16)
        .margin({ left: 12, right: 12, bottom: 32 })
      }
    }
    .backgroundColor('#F5F5F5')
  }

  /** 📦 统计卡片 */
  @Builder
  private summaryCard(icon: string, count: string, label: string) {
    Column() {
      Row({ space: 12 }) {
        Text(icon).fontSize(28)
        Column() {
          Text(count).fontSize(24).fontWeight(FontWeight.Bold)
          Text(label).fontSize(14).fontColor('#666')
        }
        .alignItems(HorizontalAlign.Start)
      }
    }
    .width('46%')
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ right: 8, bottom: 12 })
  }

  /** 📉 市场概览卡片 */
  @Builder
  private marketCard(name: string, value: string, change: string, color: string) {
    Column() {
      Text(name).fontSize(14).fontColor('#888').margin({bottom: 8})
      Text(value).fontSize(20).fontWeight(FontWeight.Bold)
      Text(change).fontSize(14).fontColor(color)
    }
    .alignItems(HorizontalAlign.Start)
    .width('46%')
    .padding(12)
    .backgroundColor('#FAFAFA')
    .border({ width: 1, color: '#E0E0E0' })
    .borderRadius(8)
    .margin({ bottom: 8 })
  }

  /** 📈 策略表现图表 (已修正语法) */
  @Builder
  private performanceChart() {
    Stack() {
      // 模拟基准指数折线 (灰色)
      Polyline()
        .points([
          [0, 180],
          [50, 160],
          [100, 170],
          [150, 140],
          [200, 130],
          [250, 110],
          [300, 100]
        ])
        .stroke(Color.Grey)
        .strokeWidth(2)
        .strokeOpacity(0.5)

      // 模拟核心策略折线 (蓝色)
      Polyline()
        .points([
          [0, 220],
          [50, 190],
          [100, 150],
          [150, 160],
          [200, 80],
          [250, 60],
          [300, 40]
        ])
        .stroke('#007DFF')
        .strokeWidth(3)
    }
    .width('100%')
    .height(200)
    .backgroundColor('#F7F7F7')
    .borderRadius(8)
  }

  /** 🎨 图表图例 */
  @Builder
  private chartLegend(color: string, text: string) {
    Row({ space: 8 }) {
      Rect({ width: 14, height: 14 }).fill(color).radius(2)
      Text(text).fontSize(12).fontColor('#333')
    }
    .alignItems(VerticalAlign.Center)
  }

  /** 📋 待办事项卡片 */
  @Builder
  private todoItem(task: string, time: string, level: string, color: string) {
    Row({ space: 12 }) {
      Column() {
        Text(task).fontSize(16).fontWeight(FontWeight.Medium)
        Text(time).fontSize(12).fontColor('#999')
      }.alignItems(HorizontalAlign.Start)
      .layoutWeight(1)

      Text(level)
        .fontSize(12)
        .padding({ left: 8, right: 8, top: 3, bottom: 3 })
        .backgroundColor(color)
        .fontColor(Color.White)
        .borderRadius(4)
    }
    .width('100%')
    .margin({ bottom: 12 })
    .padding({ bottom: 12 })
    .border({ width: { bottom: 1 }, color: '#F0F0F0' })
  }
}

