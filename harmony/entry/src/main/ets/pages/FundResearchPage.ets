// 为了代码的可读性，我们先定义好需要用到的数据结构
interface Fund {
  id: string;
  name: string;
  code: string;
  netValue: number;
  dailyChange: number;
  tags: string[];
  isSelected: boolean;
}

interface FundCompany {
  id: string;
  name: string;
  logo: string; // 用 Emoji 代替
  scale: string;
  fundCount: number;
  isSelected: boolean;
}

interface FundManager {
  id:string;
  name: string;
  avatar: string; // 用 Emoji 代替
  experience: string;
  masterpiece: string;
  isSelected: boolean;
}

// 筛选器标签模型
interface FilterCategory {
  title: string;
  tags: string[];
  isExpanded: boolean;
}

// --- 静态模拟数据 ---
// 在真实应用中，这些数据应该通过网络请求从你的后端获取
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
 */
@Component
struct FundProfilePage {
  private fundName: string = "基金画像";

  build() {
    Column() {
      // 在真实应用中，这里会展示复杂的图表和数据卡片
      Text(this.fundName)
        .fontSize(24)
        .fontWeight(FontWeight.Bold)
      Text('此处展示基金的详细信息、图表、持仓等...').margin({top: 20})
    }
    .width('100%').height('100%').justifyContent(FlexAlign.Center)
  }
}

/**
 * 筛选器面板
 */
@Component
struct FilterPanel {
  // @Prop 用于接收父组件单向传递过来的数据
  @Prop filterCategories: FilterCategory[];
  // @Link 用于双向绑定控制面板显示/隐藏的状态变量
  @Link isFilterPanelShown: boolean;

  build() {
    Column() {
      // 面板标题
      Row() {
        Text("筛选标签").fontSize(20).fontWeight(FontWeight.Bold)
        Blank() // 占位符，将关闭按钮推到最右侧
        Image($r("app.media.ic_close")) // 假设有关闭图标
          .width(24).height(24).onClick(() => this.isFilterPanelShown = false)
      }
      .width('100%').padding(15)

      // 筛选标签列表
      Scroll() {
        Column({ space: 10 }) {
          ForEach(this.filterCategories, (category: FilterCategory) => {
            // 可折叠的分类
            DisclosureGroup({
              isExpanded: category.isExpanded,
              onToggle: (isExpanded: boolean) => { category.isExpanded = isExpanded }
            }) {
              // 分类标题
              Text(category.title).fontSize(16).fontWeight(FontWeight.Medium)
            } content: {
              // 标签内容，使用流式布局
              Flex({ wrap: FlexWrap.Wrap, spacing: 8 }) {
                ForEach(category.tags, (tag: string) => {
                  Text(tag)
                    .padding({ horizontal: 12, vertical: 6 })
                    .backgroundColor('#f0f0f0')
                    .borderRadius(15)
                })
              }.padding({top: 10})
            }
          })
        }.padding({ horizontal: 15 })
      }
      .layoutWeight(1)

      // 底部按钮
      Row({ space: 10 }) {
        Button("重置").type(ButtonType.Normal).backgroundColor('#e0e0e0').fontColor(Color.Black).layoutWeight(1)
        Button("确定").type(ButtonType.Capsule).layoutWeight(1).onClick(() => this.isFilterPanelShown = false)
      }
      .width('100%').padding(15)
    }
    .width('90%')
    .height('100%')
    .backgroundColor(Color.White)
    .align(Alignment.End) // 让面板从右侧滑入
  }
}

/**
 * 主页面
 */
@Entry
@Component
struct FundResearchPage {
  @State selectedTabIndex: number = 0;
  @State funds: Fund[] = MOCK_FUNDS;
  @State companies: FundCompany[] = MOCK_COMPANIES;
  @State managers: FundManager[] = MOCK_MANAGERS;
  @State isFilterPanelShown: boolean = false;
  @State selectedCount: number = 0;

  // 筛选器数据
  @State filterCategories: FilterCategory[] = [
    { title: '业绩表现', tags: ['近1年领先', '夏普比率高', '回撤控制好'], isExpanded: true },
    { title: '风险度量', tags: ['低波动', '高风险'], isExpanded: false },
    { title: '基金经理风格', tags: ['成长型', '价值型', '平衡型'], isExpanded: false }
  ];

  private tabsController: TabsController = new TabsController();

  // 计算当前选中的项目数量
  private updateSelectedCount() {
    let count = 0;
    if(this.selectedTabIndex === 0) {
      count = this.funds.filter(f => f.isSelected).length;
    } else if (this.selectedTabIndex === 1) {
      count = this.companies.filter(c => c.isSelected).length;
    } else {
      count = this.managers.filter(m => m.isSelected).length;
    }
    this.selectedCount = count;
  }


  build() {
    Stack() { // 使用 Stack 堆叠布局，方便放置筛选器面板和悬浮按钮
      Column() {
        // 1. 顶部导航
        Tabs({ barPosition: BarPosition.Start, controller: this.tabsController }) {
          Tab("全部基金") { this.FundList() }
          Tab("基金公司") { this.CompanyList() }
          Tab("基金经理") { this.ManagerList() }
        }
        .onChange((index) => {
          this.selectedTabIndex = index;
          this.updateSelectedCount(); // 切换tab时重新计算选中数量
        })
        .layoutWeight(1)

      }
      .width('100%')
      .height('100%')

      // 2. 筛选器面板 (条件渲染)
      if (this.isFilterPanelShown) {
        // 半透明遮罩层
        Column()
          .width('100%').height('100%').backgroundColor(0x66000000)
          .onClick(() => this.isFilterPanelShown = false)
        // 筛选器
        FilterPanel({
          filterCategories: this.filterCategories,
          isFilterPanelShown: $isFilterPanelShown
        })
      }

      // 3. 悬浮操作按钮
      this.FloatingActionButton()
    }
  }

  // 构建器，用于创建可复用的UI片段

  // 搜索和筛选栏
  @Builder SearchAndFilterBar() {
    Row({ space: 10 }) {
      TextInput({ placeholder: '搜索代码/名称...' }).layoutWeight(1)
      Button() {
        Row({ space: 5 }) {
          Image($r("app.media.ic_filter")) // 假设有筛选图标
            .width(16).height(16).fillColor(Color.Black)
          Text("筛选")
        }
      }
      .type(ButtonType.Normal).backgroundColor('#f0f0f0')
      .onClick(() => { this.isFilterPanelShown = true })
    }
    .padding(15)
  }

  // 全部基金列表
  @Builder FundList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        ForEach(this.funds, (fund: Fund) => {
          ListItem() {
            Row() {
              Toggle({ type: ToggleType.Checkbox, isOn: fund.isSelected })
                .onChange((isOn) => {
                  fund.isSelected = isOn;
                  this.updateSelectedCount();
                })
              Column({ space: 5 }) {
                Text(fund.name).fontSize(16).fontWeight(FontWeight.Bold)
                Row({ space: 8 }) {
                  Text(fund.code).fontColor(Color.Gray)
                  Text(`净值: ${fund.netValue.toFixed(3)}`).fontColor(Color.Gray)
                }
              }.alignItems(HorizontalAlign.Start).layoutWeight(1)

              Text(`${fund.dailyChange > 0 ? '+' : ''}${fund.dailyChange.toFixed(2)}%`)
                .fontColor(fund.dailyChange > 0 ? Color.Red : Color.Green)
                .fontSize(16).fontWeight(FontWeight.Bold)
            }
            .padding(15).backgroundColor(Color.White).borderRadius(12)
          }
          .onClick(() => {
            // 跳转到基金画像页，这里用一个弹窗代替
            AlertDialog.show({ title: '基金画像', message: `即将跳转到 ${fund.name} 的详情页。`})
          })
        })
      }.padding({horizontal: 15})
    }
  }

  // 基金公司列表
  @Builder CompanyList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        ForEach(this.companies, (company: FundCompany) => {
          ListItem() {
            Row() {
              Toggle({ type: ToggleType.Checkbox, isOn: company.isSelected })
                .onChange((isOn) => {
                  company.isSelected = isOn;
                  this.updateSelectedCount();
                })
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
        })
      }.padding({horizontal: 15})
    }
  }

  // 基金经理列表
  @Builder ManagerList() {
    Column() {
      this.SearchAndFilterBar()
      List({ space: 10 }) {
        ForEach(this.managers, (manager: FundManager) => {
          ListItem() {
            Row() {
              Toggle({ type: ToggleType.Checkbox, isOn: manager.isSelected })
                .onChange((isOn) => {
                  manager.isSelected = isOn;
                  this.updateSelectedCount();
                })
              Text(manager.avatar).fontSize(30)
              Column({ space: 5 }) {
                Text(manager.name).fontSize(16).fontWeight(FontWeight.Bold)
                Text(`从业: ${manager.experience} | 代表作: ${manager.masterpiece}`).fontColor(Color.Gray).fontSize(12)
              }.alignItems(HorizontalAlign.Start).layoutWeight(1).margin({ left: 15 })
            }.padding(15).backgroundColor(Color.White).borderRadius(12)
          }
        })
      }.padding({horizontal: 15})
    }
  }

  // 悬浮操作按钮
  @Builder FloatingActionButton() {
    Button() {
      Text(`已选 ${this.selectedCount} | 保存为组合`)
    }
    .type(ButtonType.Capsule)
    .width(200)
    .position({ x: '50%', y: '90%' }) // 定位在屏幕下方
    .translate({ x: '-50%' }) // 水平居中
    .enabled(this.selectedCount > 0) // 当有选中项时才可用
    .opacity(this.selectedCount > 0 ? 1 : 0) // 未选中时透明
    .animation({ duration: 300, curve: Curve.EaseInOut }) // 添加显示/隐藏动画
  }
}