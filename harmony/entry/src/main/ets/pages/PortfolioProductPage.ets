// --- 数据模型定义 ---
type ProductStatus = 'DRAFT' | 'PENDING_APPROVAL' | 'PUBLISHED';
type RiskLevel = 'R1' | 'R2' | 'R3' | 'R4' | 'R5';

interface PortfolioProduct {
  id: string;
  name: string;
  riskLevel: RiskLevel;
  strategyName: string; // 关联的策略名称
  status: ProductStatus;
  creator: string;
}

// --- 静态模拟数据 ---
const MOCK_PRODUCTS: PortfolioProduct[] = [
  { id: 'p1', name: '积极进取型成长组合', riskLevel: 'R4', strategyName: '全明星基金经理FOF', status: 'DRAFT', creator: '投顾A' },
  { id: 'p2', name: '稳健理财月月红', riskLevel: 'R2', strategyName: '稳健型资产配置', status: 'PENDING_APPROVAL', creator: '投顾B' },
  { id: 'p3', name: '沪深300指数增强pro', riskLevel: 'R3', strategyName: '沪深300指数增强', status: 'PUBLISHED', creator: '投顾A' },
  { id: 'p4', name: '灵活择时宝', riskLevel: 'R5', strategyName: '双均线择时模型', status: 'PUBLISHED', creator: '投顾C' },
];


// --- UI 组件 ---

/**
 * 审核详情页 (模拟)
 */
@Component
struct ApprovalDetailPage {
  @Link productToApprove: PortfolioProduct | null;

  build() {
    Column() {
      // 顶部栏
      Row({ space: 15 }) {
        Text('❌').fontSize(20).onClick(() => this.productToApprove = null)
        Blank()
        Text("审核详情").fontSize(20).fontWeight(FontWeight.Bold)
        Blank()
      }.width('100%').padding(15).backgroundColor(Color.White)

      // 详情内容
      Scroll() {
        if (this.productToApprove) {
          Column({ space: 10 }) {
            Text(this.productToApprove.name).fontSize(24).fontWeight(FontWeight.Bold)
            Text(`风险等级: ${this.productToApprove.riskLevel}`)
            Text(`关联策略: ${this.productToApprove.strategyName}`)
            Divider().margin({vertical: 10})
            Text("成分基金列表、产品参数等详细信息...").fontColor(Color.Gray)
          }.padding(20).alignItems(HorizontalAlign.Start)
        }
      }.layoutWeight(1)

      // 审核操作栏
      Row({ space: 10 }) {
        Button("驳回").type(ButtonType.Normal).backgroundColor('#FFD2D2').fontColor(Color.Red).layoutWeight(1)
        Button("通过").type(ButtonType.Capsule).layoutWeight(1).onClick(() => this.productToApprove = null)
      }
      .padding(15).width('100%').border({ width: { top: 1 }, color: '#f0f0f0' })
    }
    .width('100%').height('100%').backgroundColor('#f5f5f5')
  }
}

/**
 * 主页面
 */
@Entry
@Component
struct PortfolioProductPage {
  @State selectedTabIndex: number = 0;
  @State productToApprove: PortfolioProduct | null = null;

  // 根据Tab筛选数据
  private getProductsByStatus(status: ProductStatus): PortfolioProduct[] {
    return MOCK_PRODUCTS.filter(p => p.status === status);
  }

  // 状态文本和颜色
  private statusInfo(status: ProductStatus): { text: string, color: Color } {
    switch (status) {
      case 'DRAFT': return { text: '草稿', color: Color.Gray };
      case 'PENDING_APPROVAL': return { text: '待审核', color: Color.Orange };
      case 'PUBLISHED': return { text: '已上架', color: Color.Green };
    }
  }

  build() {
    Stack() {
      Column() {
        Text("组合产品管理").fontSize(28).fontWeight(FontWeight.Bold).padding(15).alignSelf(FlexAlign.Start)
        Tabs({ barPosition: BarPosition.Start }) {
          Tab("我的草稿") { this.ProductList(this.getProductsByStatus('DRAFT')) }
          Tab("待我审核") { this.ProductList(this.getProductsByStatus('PENDING_APPROVAL')) }
          Tab("已上架") { this.ProductList(this.getProductsByStatus('PUBLISHED')) }
        }.onChange(index => this.selectedTabIndex = index)
      }
      .width('100%').height('100%')
      .backgroundColor('#f5f5f5')

      // 创建按钮 (只在第一个tab显示)
      if (this.selectedTabIndex === 0) {
        Button({ type: ButtonType.Circle }) {
          Text("+").fontSize(30).fontColor(Color.White)
        }
        .width(56).height(56).position({ x: '85%', y: '88%' })
        .shadow({ radius: 6, color: '#33000000' })
        // .onClick(() => { /* 跳转到创建向导页 */ })
      }

      // 审核详情页 (条件渲染)
      if (this.productToApprove) {
        ApprovalDetailPage({ productToApprove: $productToApprove })
          .transition(TransitionEffect.move(TransitionEdge.BOTTOM))
      }
    }.animation({ duration: 300, curve: Curve.EaseInOut })
  }

  // 产品列表
  @Builder ProductList(products: PortfolioProduct[]) {
    List({ space: 15 }) {
      ForEach(products, (product: PortfolioProduct) => {
        ListItem() {
          this.ProductCard(product)
        }
      }, product => product.id)
    }.padding({ horizontal: 15, bottom: 80 }) // 底部留出空间给FAB
  }

  // 单个产品卡片
  @Builder ProductCard(product: PortfolioProduct) {
    let sInfo = this.statusInfo(product.status);
    Column({ space: 12 }) {
      // 卡片头部
      Row() {
        Text(product.name).fontSize(18).fontWeight(FontWeight.Bold).layoutWeight(1)
        Row({ space: 5 }) {
          Circle().fill(sInfo.color).width(8).height(8)
          Text(sInfo.text).fontSize(12).fontColor(sInfo.color)
        }.alignItems(VerticalAlign.Center)
      }
      // 卡片中部
      Column({ space: 5 }) {
        Text(`风险等级: ${product.riskLevel}`).fontSize(14).fontColor(Color.Gray)
        Text(`关联策略: ${product.strategyName}`).fontSize(14).fontColor(Color.Gray)
      }.alignItems(HorizontalAlign.Start).width('100%')
      // 卡片底部操作栏 (动态按钮)
      Divider().margin({ vertical: 8 })
      this.ActionButtons(product)
    }
    .padding(15).backgroundColor(Color.White).borderRadius(12)
  }

  // 动态生成操作按钮
  @Builder ActionButtons(product: PortfolioProduct) {
    Row({ space: 10 }) {
      // 使用 switch 根据状态生成不同的按钮
      switch (product.status) {
        case 'DRAFT':
          Button("提交审核").layoutWeight(1).type(ButtonType.Capsule)
          Button("编辑").layoutWeight(1).type(ButtonType.Normal)
          Button("删除").layoutWeight(1).type(ButtonType.Normal).fontColor(Color.Red)
          break;
        case 'PENDING_APPROVAL':
          Button("审核").layoutWeight(1).type(ButtonType.Capsule).onClick(() => {
            this.productToApprove = product;
          })
          Button("查看详情").layoutWeight(1).type(ButtonType.Normal)
          break;
        case 'PUBLISHED':
          Button("查看详情").layoutWeight(1).type(ButtonType.Normal)
          break;
      }
    }.width('100%')
  }
}