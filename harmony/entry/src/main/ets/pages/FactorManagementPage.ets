type FactorSource = 'DATABASE' | 'PYTHON';

interface Factor {
  id: string;
  name: string;
  code: string;
  source: FactorSource;
}

// 树节点模型
interface TreeNode {
  id: string;
  name: string;
  isCategory: boolean; // 是否是分类节点
  children?: TreeNode[];
  factor?: Factor; // 如果是叶子节点，则包含因子信息
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
  children: [
    {
      id: 'cat1', name: '价值因子', isCategory: true, children: [
      { id: 'leaf1', name: 'BP估值因子', isCategory: false, factor: MOCK_FACTORS[3] }
    ]
    },
    {
      id: 'cat2', name: '动量因子', isCategory: true, children: [
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
    // 根据来源显示不同图标
    Text(factor.source === 'DATABASE' ? '💾' : '🐍')
      .fontSize(24)
    Column({ space: 4 }) {
      Text(factor.name).fontSize(16).fontWeight(FontWeight.Medium)
      Text(factor.code).fontSize(12).fontColor(Color.Gray)
    }.alignItems(HorizontalAlign.Start).layoutWeight(1)
    // 操作按钮
    Image($r("app.media.ic_more")) // 假设有 "更多" 图标
      .width(24).height(24)
  }
  .padding(15).backgroundColor(Color.White).borderRadius(12)
}

/**
 * 递归构建因子树的UI
 */
@Builder
function TreeView(node: TreeNode) {
  if (node.isCategory) {
    // 如果是分类，使用可折叠组件
    DisclosureGroup() {
      // 标题
      Row({space: 8}) {
        Image($r("app.media.ic_folder")) // 假设有文件夹图标
          .width(20).height(20)
        Text(node.name).fontSize(16).fontWeight(FontWeight.Bold)
      }
    } content: {
      // 内容，递归渲染子节点
      Column() {
        if (node.children) {
          ForEach(node.children, (childNode: TreeNode) => {
            TreeView(childNode) // 递归调用
          })
        }
      }.padding({ left: 20 }) // 子节点向右缩进
    }
  } else if (node.factor) {
    // 如果是叶子节点（因子），直接渲染因子列表项
    FactorListItem(node.factor)
  }
}


/**
 * 创建新因子的页面 (模拟)
 */
@Component
struct CreateFactorPage {
  @Link isCreating: boolean; // 双向绑定，用于关闭页面
  @State factorName: string = '';
  @State factorCode: string = '';
  @State selectedSource: FactorSource = 'DATABASE';
  @State pythonScript: string = '# 在此输入Python代码\n\n'

  build() {
    Column() {
      // 1. 顶部导航栏
      Row() {
        Image($r("app.media.ic_close")).width(24).height(24).onClick(() => this.isCreating = false)
        Text("创建新因子").fontSize(20).fontWeight(FontWeight.Bold).layoutWeight(1).textAlign(TextAlign.Center)
        Text("保存").fontSize(16).fontColor(Color.Blue).onClick(() => this.isCreating = false)
      }
      .width('100%').padding(15).backgroundColor(Color.White)
      .shadow({ radius: 4, color: '#1F000000', offsetY: 2 })

      // 2. 表单内容
      Scroll() {
        Column({space: 20}) {
          TextInput({ placeholder: '因子名称，如：市净率' }).onChange(val => this.factorName = val)
          TextInput({ placeholder: '因子编码，如：PB_RATIO' }).onChange(val => this.factorCode = val)

          // 因子来源选择
          Column({space: 10}) {
            Text("因子来源").fontSize(16).fontWeight(FontWeight.Medium)
            RadioGroup() {
              Radio({ value: 'DATABASE', group: 'source' }).onChange(isChecked => { if(isChecked) this.selectedSource = 'DATABASE' })
              Text("数据库")
              Radio({ value: 'PYTHON', group: 'source' }).onChange(isChecked => { if(isChecked) this.selectedSource = 'PYTHON' })
              Text("Python脚本")
            }.select(this.selectedSource).direction(Axis.Horizontal)
          }.alignItems(HorizontalAlign.Start)

          // 根据选择动态显示UI
          if (this.selectedSource === 'DATABASE') {
            Column({space: 10}) {
              TextInput({ placeholder: '数据源名称' })
              TextInput({ placeholder: '数据表名' })
              TextInput({ placeholder: '字段名' })
            }.transition(TransitionEffect.OPACITY.animation({duration: 300})) // 添加过渡动画
          } else {
            TextArea({ placeholder: "输入Python脚本...", text: this.pythonScript})
              .height(200)
              .onChange(val => this.pythonScript = val)
              .transition(TransitionEffect.OPACITY.animation({duration: 300}))
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
  @State selectedTreeName: number = 0;
  @State isCreating: boolean = false; // 控制创建页面的显示

  private tabsController: TabsController = new TabsController();
  private treeOptions: string[] = ["主因子树", "量化投顾树", "特色数据树"];

  build() {
    Stack() {
      Column() {
        // 顶部导航
        Tabs({ barPosition: BarPosition.Start, controller: this.tabsController }) {
          Tab("因子列表") { this.FactorList() }
          Tab("因子树") { this.FactorTree() }
          Tab("风格因子") { this.StyleFactor() }
        }.onChange((index) => { this.selectedTabIndex = index })
      }.width('100%').height('100%')

      // 创建因子悬浮按钮 (只在第一个tab显示)
      if (this.selectedTabIndex === 0 && !this.isCreating) {
        Button({ type: ButtonType.Circle }) {
          Text("+").fontSize(30).fontColor(Color.White)
        }
        .width(56).height(56)
        .position({ x: '85%', y: '88%' })
        .onClick(() => { this.isCreating = true })
        .shadow({ radius: 6, color: '#33000000' })
      }

      // 创建因子页面 (条件渲染，实现页面覆盖效果)
      if (this.isCreating) {
        CreateFactorPage({ isCreating: $isCreating })
          .transition(TransitionEffect.move(TransitionEdge.BOTTOM)) // 从底部滑入的动画
      }
    }.animation({ duration: 300, curve: Curve.EaseInOut })
  }

  // 因子列表 Tab
  @Builder FactorList() {
    Column() {
      TextInput({ placeholder: '搜索因子...' }).margin(15)
      List({ space: 10 }) {
        ForEach(this.factors, (factor: Factor) => {
          ListItem() {
            FactorListItem(factor)
          }
        })
      }.padding({ horizontal: 15 })
    }
  }

  // 因子树 Tab
  @Builder FactorTree() {
    Column() {
      // 因子树选择器
      Select(this.treeOptions)
        .selected(this.selectedTreeName)
        .onSelect( (index, value) => {
          this.selectedTreeName = index;
          // 在真实应用中，这里会根据 value 重新加载不同的因子树数据
        })
        .margin(15)

      // 树状图
      Scroll() {
        TreeView(this.factorTree)
      }
      .padding({ horizontal: 15 })
    }
  }

  // 风格因子 Tab (简化占位)
  @Builder StyleFactor() {
    Column() {
      Text("风格因子管理页面").fontSize(20).margin(20)
      Text("此处用于管理通过多因子加权创建的风格分类。").padding({horizontal: 20})
    }.width('100%')
  }
}
