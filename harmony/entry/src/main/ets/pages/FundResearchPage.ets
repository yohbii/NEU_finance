// --- 数据模型定义 ---
// 定义了应用中使用的核心数据结构，增强了代码的可读性和类型安全。

/**
 * 单个基金的数据模型
 */
interface Fund {
  id: string; // 唯一标识符，用于列表渲染的key
  name: string;
  code: string;
  netValue: number;
  dailyChange: number;
  tags: string[];
  isSelected: boolean; // 用于标记是否被用户勾选
}

/**
 * 基金公司的数据模型
 */
interface FundCompany {
  id: string;
  name: string;
  logo: string; // 使用 Emoji 字符串代替图片资源，方便移植
  scale: string;
  fundCount: number;
  isSelected: boolean;
}

/**
 * 基金经理的数据模型
 */
interface FundManager {
  id: string;
  name: string;
  avatar: string; // 使用 Emoji 字符串
  experience: string;
  masterpiece: string;
  isSelected: boolean;
}

/**
 * 筛选器分类的数据模型
 */
interface FilterCategory {
  title: string;
  tags: string[];
  isExpanded: boolean; // 控制该分类在UI上是否展开
}

// --- 静态模拟数据 ---
// 在真实应用中，这些数据应该通过API从后端服务获取。
const MOCK_FUNDS: Fund[] = [
  { id: 'f1', name: '某某成长混合A', code: '001234', netValue: 2.345, dailyChange: 1.23, tags: ['高成长', '消费'], isSelected: false },
  { id: 'f2', name: '某某价值优选', code: '005678', netValue: 1.876, dailyChange: -0.54, tags: ['价值型', '大盘'], isSelected: false },
  { id: 'f3', name: '某某科技先锋', code: '009012', netValue: 3.102, dailyChange: 2.05, tags: ['科技', '中小盘'], isSelected: false },
];
const MOCK_COMPANIES: FundCompany[] = [
  { id: 'c1', name: '华夏基金', logo: '🏢', scale: '1.2万亿', fundCount: 350, isSelected: false },
  { id: 'c2', name: '易方达基金', logo: '🏢', scale: '1.5万亿', fundCount: 410, isSelected: false },
];
const MOCK_MANAGERS: FundManager[] = [
  { id: 'm1', name: '张三', avatar: '👨‍💼', experience: '10年', masterpiece: '某某成长混合A', isSelected: false },
  { id: 'm2', name: '李四', avatar: '👩‍💼', experience: '8年', masterpiece: '某某价值优选', isSelected: false },
];


// --- UI 组件 ---

/**
 * 基金画像页 (详情页) - 这是一个简化的占位页面
 * @Component 表明这是一个可复用的UI组件。
 */
@Component
struct FundProfilePage {
  private fundName: string = "基金画像";
  build() {
    Column() {
      // 在真实应用中，这里会展示复杂的图表和数据卡片
      Text(this.fundName).fontSize(24).fontWeight(FontWeight.Bold)
      Text('此处展示基金的详细信息、图表、持仓等...').margin({ top: 20 })
    }
    .width('100%').height('100%').justifyContent(FlexAlign.Center)
  }
}

/**
 * 筛选器面板组件，从侧边滑出。
 */
@Component
struct FilterPanel {
  // @Prop 用于接收父组件单向传递过来的数据，子组件不可修改。
  @Prop filterCategories: FilterCategory[];
  // @Link 用于双向绑定父组件的状态变量，子组件的修改会同步回父组件。
  @Link isFilterPanelShown: boolean;

  build() {
    Column() {
      // 面板标题和关闭按钮
      Row() {
        Text("筛选标签").fontSize(20).fontWeight(FontWeight.Bold)
        Blank() // 占位符，将关闭按钮推到最右侧
        // [修正] 使用 Emoji 替代本地图片资源，避免因资源找不到而报错
        Text('❌').fontSize(20).onClick(() => this.isFilterPanelShown = false)
      }
      .width('100%').padding(15)

      // 筛选标签列表
      Scroll() {
        Column({ space: 10 }) {
          // [优化] 为 ForEach 添加 key，提升渲染性能
          ForEach(this.filterCategories, (category: FilterCategory) => {
            // DisclosureGroup: 可折叠的UI容器
            DisclosureGroup({
              isExpanded: category.isExpanded,
              onToggle: (isExpanded: boolean) => { category.isExpanded = isExpanded }
            }) {
              Text(category.title).fontSize(16).fontWeight(FontWeight.Medium)
            } content: {
              // Flex: 流式布局，用于展示标签云
              Flex({ wrap: FlexWrap.Wrap, spacing: 8, alignItems: ItemAlign.Start }) {
                // [优化] 为 ForEach 添加 key
                ForEach(category.tags, (tag: string) => {
                  Text(tag)
                    .padding({ horizontal: 12, vertical: 6 })
                    .backgroundColor('#f0f0f0')
                    .borderRadius(15)
                }, tag => tag)
              }.padding({ top: 10 })
            }
          }, category => category.title)
        }.padding({ horizontal: 15 })
      }
      .layoutWeight(1)

      // 底部操作按钮
      Row({ space: 10 }) {
        // [优化] 明确主次操作，重置按钮使用更普通的样式
        Button("重置").type(ButtonType.Normal).backgroundColor('#e0e0e0').fontColor(Color.Black).layoutWeight(1)
        Button("确定").type(ButtonType.Capsule).layoutWeight(1).onClick(() => this.isFilterPanelShown = false)
      }
      .width('100%').padding(15)
    }
    .width('90%').height('100%').backgroundColor(Color.White)
    .align(Alignment.End) // 指定组件在Stack中的对齐方式，实现从右侧滑入的效果
  }
}

/**
 * 主页面，作为应用的入口。
 * @Entry 表明这是应用的入口页面。
 */
@Entry
@Component
struct FundResearchPage {
  // @State 标记的变量是UI的状态，当其值改变时，UI会自动刷新。
  @State selectedTabIndex: number = 0;
  @State funds: Fund[] = MOCK_FUNDS;
  @State companies: FundCompany[] = MOCK_COMPANIES;
  @State managers: FundManager[] = MOCK_MANAGERS;
  @State isFilterPanelShown: boolean = false;
  @State selectedCount: number = 0;

  @State filterCategories: FilterCategory[] = [
    { title: '业绩表现', tags: ['近1年领先', '夏普比率高', '回撤控制好'], isExpanded: true },
    { title: '风险度量', tags: ['低波动', '高风险'], isExpanded: false },
    { title: '基金经理风格', tags: ['成长型', '价值型', '平衡型'], isExpanded: false }
  ];

  private tabsController: TabsController = new TabsController();

  /**
   * 计算当前选中的项目数量。这是一个私有方法，用于更新UI状态。
   */
  private updateSelectedCount() {
    let count = 0;
    switch (this.selectedTabIndex) {
      case 0:
        count = this.funds.filter(f => f.isSelected).length;
        break;
      case 1:
        count = this.companies.filter(c => c.isSelected).length;
        break;
      case 2:
        count = this.managers.filter(m => m.isSelected).length;
        break;
    }
    this.selectedCount = count;
  }

  build() {
    // Stack 是一种堆叠布局，后添加的组件会覆盖在先添加的组件之上。
    // 非常适合实现模态弹窗、悬浮按钮等效果。
    Stack() {
      Column() {
        Tabs({ barPosition: BarPosition.Start, controller: this.tabsController }) {
          Tab("全部基金") { this.FundList() }
          Tab("基金公司") { this.CompanyList() }
          Tab("基金经理") { this.ManagerList() }
        }
        .onChange((index) => {
          this.selectedTabIndex = index;
          this.updateSelectedCount(); // 切换tab时，重新计算选中项数量
        })
        .layoutWeight(1) // 使Tabs组件填满父容器的剩余空间
      }
      .width('100%').height('100%')

      // 根据 isFilterPanelShown 的值，条件性地渲染筛选器面板和遮罩层
      if (this.isFilterPanelShown) {
        Column() // 半透明遮罩层
          .width('100%').height('100%').backgroundColor(0x66000000)
          .onClick(() => this.isFilterPanelShown = false)
        FilterPanel({
          filterCategories: this.filterCategories,
          isFilterPanelShown: $isFilterPanelShown // 使用 $ 符号实现 @Link 的双向绑定
        })
      }

      this.FloatingActionButton()
    }
  }

  // @Builder 用于定义可复用的UI片段，可以像调用函数一样使用它。

  @Builder SearchAndFilterBar() {
    Row({ space: 10 }) {
      TextInput({ placeholder: '搜索代码/名称...' }).layoutWeight(1)
      // [优化] 调整按钮样式，使其更和谐
      Button() {
        Row({ space: 5 }) {
          // [修正] 使用 Emoji 替代图标
          Text('🔍').fontSize(14)
          Text("筛选")
        }
      }
      .type(ButtonType.Capsule).backgroundColor('#e8e8e8').fontColor(Color.Black)
      .onClick(() => { this.isFilterPanelShown = true })
    }
    .padding(15)
  }

  @Builder FundList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        // [优化] 为 ForEach 添加 key，提升渲染性能
        ForEach(this.funds, (fund: Fund) => {
          ListItem() {
            Row({ space: 10 }) { // 增加整体间距
              // [优化] 为 Toggle 添加右边距，使其不与内容紧贴
              Toggle({ type: ToggleType.Checkbox, isOn: fund.isSelected }).margin({ right: 5 })
                .onChange((isOn) => { fund.isSelected = isOn; this.updateSelectedCount(); })

              Column({ space: 5 }) {
                Text(fund.name).fontSize(16).fontWeight(FontWeight.Bold)
                Row({ space: 8 }) {
                  Text(fund.code).fontColor(Color.Gray)
                  Text(`净值: ${fund.netValue.toFixed(3)}`).fontColor(Color.Gray)
                }
              }.alignItems(HorizontalAlign.Start).layoutWeight(1)

              Text(`${fund.dailyChange > 0 ? '+' : ''}${fund.dailyChange.toFixed(2)}%`)
                .fontColor(fund.dailyChange > 0 ? Color.Red : Color.Green).fontSize(16).fontWeight(FontWeight.Bold)
            }.padding(15).backgroundColor(Color.White).borderRadius(12)
          }
          .onClick(() => { AlertDialog.show({ title: '基金画像', message: `即将跳转到 ${fund.name} 的详情页。` }) })
        }, (fund: Fund) => fund.id)
      }.padding({ horizontal: 15 })
    }.backgroundColor('#f5f5f5') // [优化] 添加背景色，突出卡片
  }

  @Builder CompanyList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        ForEach(this.companies, (company: FundCompany) => {
          ListItem() {
            Row() {
              Toggle({ type: ToggleType.Checkbox, isOn: company.isSelected }).margin({ right: 5 })
                .onChange((isOn) => { company.isSelected = isOn; this.updateSelectedCount(); })
              Text(company.logo).fontSize(30)
              Column({ space: 5 }) {
                Text(company.name).fontSize(16).fontWeight(FontWeight.Bold)
                Row({ space: 8 }) {
                  Text(`规模: ${company.scale}`).fontColor(Color.Gray)
                  Text(`基金数: ${company.fundCount}`).fontColor(Color.Gray)
                }
              }.alignItems(HorizontalAlign.Start).layoutWeight(1).margin({ left: 15 })
            }.padding(15).backgroundColor(Color.White).borderRadius(12)
          }
        }, (company: FundCompany) => company.id)
      }.padding({ horizontal: 15 })
    }.backgroundColor('#f5f5f5')
  }

  @Builder ManagerList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        ForEach(this.managers, (manager: FundManager) => {
          ListItem() {
            Row() {
              Toggle({ type: ToggleType.Checkbox, isOn: manager.isSelected }).margin({ right: 5 })
                .onChange((isOn) => { manager.isSelected = isOn; this.updateSelectedCount(); })
              Text(manager.avatar).fontSize(30)
              Column({ space: 5 }) {
                Text(manager.name).fontSize(16).fontWeight(FontWeight.Bold)
                Text(`从业: ${manager.experience} | 代表作: ${manager.masterpiece}`).fontColor(Color.Gray).fontSize(12)
              }.alignItems(HorizontalAlign.Start).layoutWeight(1).margin({ left: 15 })
            }.padding(15).backgroundColor(Color.White).borderRadius(12)
          }
        }, (manager: FundManager) => manager.id)
      }.padding({ horizontal: 15 })
    }.backgroundColor('#f5f5f5')
  }

  @Builder FloatingActionButton() {
    Button() {
      Text(`已选 ${this.selectedCount} | 保存为组合`)
    }
    .type(ButtonType.Capsule).width(200)
    // 使用 position + translate 的组合可以精确地将按钮居中放置
    .position({ x: '50%', y: '90%' }).translate({ x: '-50%' })
    .enabled(this.selectedCount > 0) // 当有选中项时按钮才可用
    // 根据选中数量动态改变透明度，实现淡入淡出效果
    .opacity(this.selectedCount > 0 ? 1 : 0)
    // 为 opacity 的变化添加动画，使其过渡更平滑
    .animation({ duration: 300, curve: Curve.EaseInOut })
  }
}