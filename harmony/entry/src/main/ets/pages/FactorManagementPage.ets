// --- 数据模型定义 ---
type FactorSource = 'DATABASE' | 'PYTHON';

interface Factor {
  id: string;
  name: string;
  code: string;
  source: FactorSource;
}

// [优化] 为树节点添加 isExpanded 属性，用于保存其展开状态
interface TreeNode {
  id: string;
  name: string;
  isCategory: boolean;
  children?: TreeNode[];
  factor?: Factor;
  isExpanded?: boolean; // 用于 DisclosureGroup
}

// --- 静态模拟数据 ---
const MOCK_FACTORS: Factor[] = [
  { id: 'f1', name: '近一月收益率', code: 'RETURN_1M', source: 'DATABASE' },
  { id: 'f2', name: '市值（对数）', code: 'LOG_MKT_CAP', source: 'DATABASE' },
  { id: 'f3', name: '波动率（年化）', code: 'VOLATILITY_ANNUAL', source: 'PYTHON' },
  { id: 'f4', name: 'BP估值因子', code: 'BP_RATIO', source: 'PYTHON' },
];

const MOCK_FACTOR_TREE: TreeNode = {
  id: 'root',
  name: '主因子树',
  isCategory: true,
  isExpanded: true, // [优化] 根节点默认展开
  children: [
    {
      id: 'cat1', name: '价值因子', isCategory: true, isExpanded: false, children: [
      { id: 'leaf1', name: 'BP估值因子', isCategory: false, factor: MOCK_FACTORS[3] }
    ]
    },
    {
      id: 'cat2', name: '动量因子', isCategory: true, isExpanded: true, children: [
      { id: 'leaf2', name: '近一月收益率', isCategory: false, factor: MOCK_FACTORS[0] }
    ]
    },
    { id: 'leaf3', name: '波动率（年化）', isCategory: false, factor: MOCK_FACTORS[2] }
  ]
};

// --- UI 组件 ---

/**
 * 因子列表中的单个列表项
 */
@Builder
function FactorListItem(factor: Factor) {
  Row({ space: 12 }) {
    // [修正] 使用 Emoji 替代图标
    Text(factor.source === 'DATABASE' ? '💾' : '🐍')
      .fontSize(24)
    Column({ space: 4 }) {
      Text(factor.name).fontSize(16).fontWeight(FontWeight.Medium)
      Text(factor.code).fontSize(12).fontColor(Color.Gray)
    }.alignItems(HorizontalAlign.Start).layoutWeight(1)
    // [修正] 使用 Emoji 替代图标
    Text('⋮').fontSize(24).fontWeight(FontWeight.Bold).fontColor(Color.Gray)
  }
  .padding(15).backgroundColor(Color.White).borderRadius(12)
}

/**
 * 递归构建因子树的UI
 */
@Builder
function TreeView(node: TreeNode) {
  if (node.isCategory) {
    // [优化] 绑定 isExpanded 状态，并提供 onToggle 回调来更新状态
    DisclosureGroup({
      isExpanded: node.isExpanded,
      onToggle: (isExpanded: boolean) => {
        node.isExpanded = isExpanded;
      }
    }) {
      Row({ space: 8 }) {
        // [修正] 使用 Emoji 替代图标
        Text('📁').fontSize(20)
        Text(node.name).fontSize(16).fontWeight(FontWeight.Bold)
      }
    } content: {
      Column() {
        if (node.children) {
          // [优化] 为 ForEach 添加 key
          ForEach(node.children, (childNode: TreeNode) => {
            TreeView(childNode) // 递归调用
          }, (childNode: TreeNode) => childNode.id)
        }
      }.padding({ left: 20 })
    }
  } else if (node.factor) {
    FactorListItem(node.factor)
  }
}


/**
 * 创建新因子的页面
 */
@Component
struct CreateFactorPage {
  @Link isCreating: boolean;
  @State factorName: string = '';
  @State factorCode: string = '';
  @State selectedSource: FactorSource = 'DATABASE';
  @State pythonScript: string = '# 在此输入Python代码\n# 例如: df["new_factor"] = df["price"] / df["earnings"]';

  build() {
    Column() {
      // 1. 顶部导航栏
      Row({ space: 15 }) {
        // [修正] 使用 Emoji 替代图标
        Text('❌').fontSize(20).onClick(() => this.isCreating = false)
        // [优化] 使用 Blank 组件确保标题居中
        Blank()
        Text("创建新因子").fontSize(20).fontWeight(FontWeight.Bold)
        Blank()
        Text("保存").fontSize(16).fontColor(Color.Blue).onClick(() => this.isCreating = false)
      }
      .width('100%').padding(15).backgroundColor(Color.White)
      .shadow({ radius: 4, color: '#1F000000', offsetY: 2 })

      // 2. 表单内容
      Scroll() {
        Column({ space: 20 }) {
          TextInput({ placeholder: '因子名称，如：市净率' }).onChange(val => this.factorName = val)
          TextInput({ placeholder: '因子编码，如：PB_RATIO' }).onChange(val => this.factorCode = val)

          Column({ space: 10 }) {
            Text("因子来源").fontSize(16).fontWeight(FontWeight.Medium)
            // [修正] 使用新的 RadioGroup 写法，更简洁、标准
            RadioGroup({
              value: this.selectedSource,
              group: 'source',
              onChange: (value: string) => {
                this.selectedSource = value as FactorSource;
              }
            }) {
              Row({ space: 5 }) { Radio({ value: 'DATABASE' }); Text("数据库") }
              Row({ space: 5 }) { Radio({ value: 'PYTHON' }); Text("Python脚本") }
            }.direction(Axis.Horizontal).width('100%').justifyContent(FlexAlign.SpaceAround)
          }.alignItems(HorizontalAlign.Start)

          if (this.selectedSource === 'DATABASE') {
            Column({ space: 10 }) {
              TextInput({ placeholder: '数据源名称' })
              TextInput({ placeholder: '数据表名' })
              TextInput({ placeholder: '字段名' })
            }.transition(TransitionEffect.OPACITY.animation({ duration: 300 }))
          } else {
            TextArea({ placeholder: "输入Python脚本...", text: this.pythonScript })
              .height(200).fontFamily("Consolas, monaco, monospace") // [优化] 使用等宽字体
              .onChange(val => this.pythonScript = val)
              .transition(TransitionEffect.OPACITY.animation({ duration: 300 }))
          }
        }.padding(15).width('100%')
      }
      .layoutWeight(1)
    }
    .width('100%').height('100%').backgroundColor('#f5f5f5')
  }
}


/**
 * 主页面
 */
@Entry
@Component
struct FactorManagementPage {
  @State selectedTabIndex: number = 0;
  @State factors: Factor[] = MOCK_FACTORS;
  @State factorTree: TreeNode = MOCK_FACTOR_TREE;
  @State selectedTreeNameIndex: number = 0;
  @State isCreating: boolean = false;
  private tabsController: TabsController = new TabsController();
  private treeOptions: string[] = ["主因子树", "量化投顾树", "特色数据树"];

  build() {
    Stack() {
      Column() {
        Tabs({ barPosition: BarPosition.Start, controller: this.tabsController }) {
          Tab("因子列表") { this.FactorList() }
          Tab("因子树") { this.FactorTree() }
          Tab("风格因子") { this.StyleFactor() }
        }.onChange((index) => { this.selectedTabIndex = index })
      }.width('100%').height('100%')

      if (this.selectedTabIndex === 0 && !this.isCreating) {
        Button({ type: ButtonType.Circle }) {
          Text("+").fontSize(30).fontColor(Color.White)
        }
        .width(56).height(56).position({ x: '85%', y: '88%' })
        .onClick(() => { this.isCreating = true }).shadow({ radius: 6, color: '#33000000' })
      }

      if (this.isCreating) {
        CreateFactorPage({ isCreating: $isCreating })
          .transition(TransitionEffect.move(TransitionEdge.BOTTOM))
      }
    }.animation({ duration: 300, curve: Curve.EaseInOut })
  }

  @Builder FactorList() {
    Column() {
      TextInput({ placeholder: '搜索因子...' }).margin(15)
      List({ space: 10 }) {
        // [优化] 为 ForEach 添加 key
        ForEach(this.factors, (factor: Factor) => {
          ListItem() { FactorListItem(factor) }
        }, (factor: Factor) => factor.id)
      }.padding({ horizontal: 15 })
    }.backgroundColor('#f5f5f5') // [优化] 统一背景色
  }

  @Builder FactorTree() {
    Column() {
      Select(this.treeOptions)
        .selected(this.selectedTreeNameIndex)
        .onSelect((index, value) => { this.selectedTreeNameIndex = index; })
        .margin(15)
      Scroll() {
        TreeView(this.factorTree)
      }.padding({ horizontal: 15 })
    }.backgroundColor('#f5f5f5') // [优化] 统一背景色
  }

  @Builder StyleFactor() {
    Column({ space: 10 }) {
      Text("风格因子管理页面").fontSize(20)
      Text("此处用于管理通过多因子加权创建的风格分类。").padding({ horizontal: 20 }).fontColor(Color.Gray)
    }
    .width('100%').height('100%').justifyContent(FlexAlign.Center)
    .backgroundColor('#f5f5f5') // [优化] 统一背景色
  }
}