// FactorPage.ets
import { prompt } from '@kit.ArkUI';

interface BaseFactor { id: string; name:string; description: string; }
interface FactorTreeNode { id: string; name: string; children?: FactorTreeNode[]; factorId?: string; }
interface WeightedFactor { factor: BaseFactor; weight: number; }
interface SelectOption { value: string; }

const INITIAL_BASE_FACTORS: BaseFactor[] = [
  { id: 'f_pe', name: 'PE TTM', description: '市盈率(滚动)' },
  { id: 'f_pb', name: 'PB LF', description: '市净率(最新)' },
  { id: 'f_roe', name: 'ROE', description: '净资产收益率' },
  { id: 'f_momentum', name: 'Momentum_20D', description: '20日动量' },
  { id: 'f_volatility', name: 'Volatility_60D', description: '60日波动率' },
  { id: 'f_size', name: 'Market Cap', description: '总市值' },
  { id: 'f_beta', name: 'Beta_1Y', description: '一年Beta值' },
];
const QUANT_FACTOR_TREE: FactorTreeNode[] = [
  { id: 't1_value', name: '价值类', children: [{ id: 't1_pe', name: 'PE TTM', factorId: 'f_pe' }, { id: 't1_pb', name: 'PB LF', factorId: 'f_pb' },] },
  { id: 't1_quality', name: '质量类', children: [{ id: 't1_roe', name: 'ROE', factorId: 'f_roe' }] },
  { id: 't1_risk', name: '风险类', children: [{ id: 't1_vol', name: 'Volatility_60D', factorId: 'f_volatility' }, { id: 't1_beta', name: 'Beta_1Y', factorId: 'f_beta' }] }
];
const STYLE_FACTOR_TREE: FactorTreeNode[] = [
  { id: 't2_fundamental', name: '基本面风格', children: [
    { id: 't2_size', name: '规模因子', factorId: 'f_size' },
    { id: 't2_value', name: '价值因子', factorId: 'f_pe' },
  ]},
  { id: 't2_technical', name: '技术面风格', children: [
    { id: 't2_momentum', name: '动量因子', factorId: 'f_momentum' },
    { id: 't2_volatility', name: '波动率因子', factorId: 'f_volatility' }
  ]}
];
const FACTOR_TREE_OPTIONS: SelectOption[] = [{ value: '量化选股因子树' }, { value: '风格投资因子树' }];

@Entry
@Component
export struct FactorPage {
  @State currentTab: number = 0;
  @State selectedTreeIndex: number = 0;
  @State allBaseFactors: BaseFactor[] = INITIAL_BASE_FACTORS;
  @State newFactorName: string = '';
  @State newFactorDescription: string = '';
  @State selectedFactorsForCreation: WeightedFactor[] = [];

  @State searchKey: string = '';
  @State searchResults: BaseFactor[] = [];

  @State expandedNodeIds: string[] = [];

  aboutToAppear() { this.searchResults = [...this.allBaseFactors]; }

  toggleNodeExpansion(nodeId: string) {
    const index = this.expandedNodeIds.indexOf(nodeId);
    if (index > -1) {
      this.expandedNodeIds.splice(index, 1);
    } else {
      this.expandedNodeIds.push(nodeId);
    }
    // 重新赋值以触发UI更新
    this.expandedNodeIds = [...this.expandedNodeIds];
  }

  build() {
    Column() {
      Text('因子管理').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
      Tabs({ index: this.currentTab }) {
        TabContent() { this.FactorTreeView() }.tabBar('因子树管理')
        TabContent() { this.FactorSearchView() }.tabBar('搜索因子')
        TabContent() { this.CreateFactorView() }.tabBar('创建衍生因子')
      }
      .onChange(index => {
        this.currentTab = index;
        this.searchKey = '';
        this.searchResults = [...this.allBaseFactors];
      })
      .barMode(BarMode.Fixed).barWidth('100%').layoutWeight(1)
    }.width('100%').height('100%').backgroundColor('#f0f2f5')
  }

  @Builder
  FactorTreeView() {
    Column() {
      Select(FACTOR_TREE_OPTIONS)
        .value(FACTOR_TREE_OPTIONS[this.selectedTreeIndex].value).selected(this.selectedTreeIndex)
        .onSelect(index => {
          this.selectedTreeIndex = index;
          this.expandedNodeIds = [];
        })
        .fontColor('#000000').margin(15)

      List({ space: 5 }) {
        ForEach((this.selectedTreeIndex === 0 ? QUANT_FACTOR_TREE : STYLE_FACTOR_TREE), (node: FactorTreeNode) => {
          ListItem() { this.TreeNodeStyle(node, 0) }
        })
      }.layoutWeight(1).padding({ left: 15, right: 15 })
    }.width('100%')
  }

  @Builder
  TreeNodeStyle(item: FactorTreeNode, level: number) {
    Column() {
      Row() {
        if (item.children && item.children.length > 0) {
          Image($r('app.media.chen11')).width(16).height(16).margin({ right: 8 })
            .rotate({ angle: this.expandedNodeIds.includes(item.id) ? 90 : 0 }) // 展开时旋转90度
            .animation({ duration: 200, curve: Curve.EaseInOut }) // 平滑动画
        } else {
          Image($r('app.media.jin11')).width(16).height(16).margin({ right: 8 }) // 叶子节点图标不变
        }
        Text(item.name).fontSize(16)
      }
      .padding({ left: level * 20 }).height(40)
      .onClick(() => {
        if (item.children && item.children.length > 0) {
          this.toggleNodeExpansion(item.id);
        }
      })

      if (this.expandedNodeIds.includes(item.id) && item.children && item.children.length > 0) {
        Column() {
          ForEach(item.children, (child: FactorTreeNode) => {
            this.TreeNodeStyle(child, level + 1)
          })
        }.alignItems(HorizontalAlign.Start)
      }
    }.alignItems(HorizontalAlign.Start)
  }

  private findSelected(factorId: string): WeightedFactor | undefined { return this.selectedFactorsForCreation.find(item => item.factor.id === factorId); }
  @Builder FactorSearchView() { Column({ space: 15 }) { Row({ space: 10 }) { TextInput({ placeholder: '搜索因子名称或描述...', text: this.searchKey }).onChange(val => this.searchKey = val).layoutWeight(1).height(45).backgroundColor('#FFFFFF').borderRadius(25).padding({ left: 15, right: 15 }); Button('搜索').width(80).height(45).onClick(() => this.executeSearch()) }; List({ space: 10 }) { ForEach(this.searchResults, (factor: BaseFactor) => { ListItem() { Column() { Row() { Text(factor.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1) }.width('100%'); Text(factor.description).fontSize(14).fontColor('#666666').width('100%').textAlign(TextAlign.Start).margin({ top: 4 }) }.padding(15).backgroundColor('#FFFFFF').borderRadius(8).alignItems(HorizontalAlign.Start) } }) }.layoutWeight(1) }.width('90%').height('100%').padding({ top: 15, bottom: 20 }) }
  executeSearch() { const key = this.searchKey.trim().toLowerCase(); if (!key) { this.searchResults = [...this.allBaseFactors]; return; } const results = this.allBaseFactors.filter(f => f.name.toLowerCase().includes(key) || f.description.toLowerCase().includes(key)); this.searchResults = results; if (results.length === 0) { prompt.showToast({ message: '未找到匹配的因子' }); } }
  @Builder CreateFactorView() { Column({ space: 15 }) { TextInput({ placeholder: '输入新因子的名称', text: this.newFactorName }).onChange(val => this.newFactorName = val); TextInput({ placeholder: '输入新因子的详细描述', text: this.newFactorDescription }).onChange(val => this.newFactorDescription = val); Button('创建因子').width('100%').height(45).onClick(() => this.createDerivativeFactor()); Text('选择基础因子并分配权重 (可选)').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start).margin({ top: 10 }); List({ space: 10 }) { ForEach(this.allBaseFactors, (factor: BaseFactor) => { ListItem() { this.FactorSelectionItem(factor) } }) }.layoutWeight(1) }.width('90%').height('100%').padding({ top: 15, bottom: 20 }) }
  @Builder FactorSelectionItem(factor: BaseFactor) { Column({ space: 8 }) { Row() { Toggle({ type: ToggleType.Checkbox, isOn: !!this.findSelected(factor.id) }).onChange(isOn => { const currentIndex = this.selectedFactorsForCreation.findIndex(item => item.factor.id === factor.id); if (isOn && currentIndex === -1) { this.selectedFactorsForCreation.push({ factor: factor, weight: 50 }); } else if (!isOn && currentIndex > -1) { this.selectedFactorsForCreation.splice(currentIndex, 1); } this.selectedFactorsForCreation = [...this.selectedFactorsForCreation]; }); Text(factor.name).fontSize(16).layoutWeight(1); Text(factor.description).fontSize(12).fontColor('#666666') }; if (this.findSelected(factor.id)) { Row({ space: 10 }) { Text(`权重: ${this.findSelected(factor.id)?.weight}%`); Slider({ value: this.findSelected(factor.id)?.weight, min: 0, max: 100, step: 1, style: SliderStyle.OutSet }).layoutWeight(1).onChange(val => { const index = this.selectedFactorsForCreation.findIndex(item => item.factor.id === factor.id); if (index > -1) { this.selectedFactorsForCreation[index].weight = Math.round(val); this.selectedFactorsForCreation = [...this.selectedFactorsForCreation]; } }) }.padding({ left: 30 }) } }.padding(10).backgroundColor('#FFFFFF').borderRadius(8) }
  createDerivativeFactor() { const name = this.newFactorName.trim(); const description = this.newFactorDescription.trim(); if (!name) { prompt.showToast({ message: '请输入因子名称' }); return; } if (!description) { prompt.showToast({ message: '请输入因子描述' }); return; } if (this.allBaseFactors.some(factor => factor.name === name)) { prompt.showToast({ message: `因子 "${name}" 已存在，请使用其他名称` }); return; } const newFactor: BaseFactor = { id: 'd_' + Date.now(), name: name, description: description }; this.allBaseFactors.push(newFactor); this.allBaseFactors = [...this.allBaseFactors]; prompt.showToast({ message: `因子 "${name}" 创建成功!` }); this.newFactorName = ''; this.newFactorDescription = ''; this.selectedFactorsForCreation = []; }
}