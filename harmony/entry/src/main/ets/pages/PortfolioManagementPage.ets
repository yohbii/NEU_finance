// 文件名：PortfolioManagementPage.ets

import { prompt } from '@kit.ArkUI';

// --- 数据模型定义 ---
interface BaseFund { id: string; name: string; code: string; }
interface SelectOption { value: string; }
type RiskLevelType = '低风险' | '中低风险' | '中风险' | '中高风险' | '高风险';
interface Portfolio { id: string; name: string; strategy: string; description: string; riskLevel: RiskLevelType; status: '待审核' | '已上架' | '已下架'; constituentFunds: BaseFund[]; cumulativeReturn?: number; }

// --- 模拟数据 ---
const ALL_AVAILABLE_FUNDS: BaseFund[] = [
  { id: 'fund_001', name: '易方达蓝筹精选混合', code: '005827' },
  { id: 'fund_002', name: '景顺长城新兴成长混合', code: '260108' },
  { id: 'fund_003', name: '中欧医疗健康混合A', code: '003095' },
  { id: 'fund_004', name: '招商中证白酒指数', code: '161725' },
  { id: 'fund_005', name: '国泰纳斯达克100(QDII)', code: '160213' },
  { id: 'fund_006', name: '华夏国证半导体芯片ETF', code: '159995' },
  { id: 'fund_007', name: '天弘中证消费ETF', code: '159928' },
  { id: 'fund_008', name: '博时信用债纯债债券', code: '050027' },
];
const INITIAL_PORTFOLIOS: Portfolio[] = [
  { id: 'p_001', name: '稳健增长FOF组合', strategy: '固收+策略', description: '本组合旨在通过精选固收类资产打底，搭配少量权益资产增强收益，追求在控制回撤的前提下实现长期稳健的资本增值。', riskLevel: '中低风险', status: '已上架', constituentFunds: [ALL_AVAILABLE_FUNDS[0], ALL_AVAILABLE_FUNDS[7]], cumulativeReturn: 15.7 },
  { id: 'p_002', name: 'A股核心动力组合', strategy: '行业轮动', description: '本组合聚焦A股市场中具有核心竞争力和长期成长潜力的龙头企业，通过行业轮动策略，力求捕捉市场结构性机会。', riskLevel: '中高风险', status: '待审核', constituentFunds: [ALL_AVAILABLE_FUNDS[1], ALL_AVAILABLE_FUNDS[2]], },
  { id: 'p_003', name: '全球科技先锋', strategy: '全球科技', description: '精选全球范围内领先的科技公司，重点布局半导体、人工智能和云计算赛道，旨在分享全球科技创新红利。', riskLevel: '高风险', status: '已上架', constituentFunds: [ALL_AVAILABLE_FUNDS[4], ALL_AVAILABLE_FUNDS[5]], cumulativeReturn: -5.2 },
  { id: 'p_004', name: '大消费升级组合', strategy: '大消费', description: '投资于受益于中国消费结构升级的优质企业，覆盖食品饮料、家电、美妆等多个领域。', riskLevel: '中风险', status: '已下架', constituentFunds: [ALL_AVAILABLE_FUNDS[3], ALL_AVAILABLE_FUNDS[6]], cumulativeReturn: 8.9 }
];
const RISK_LEVEL_OPTIONS: RiskLevelType[] = ['低风险', '中低风险', '中风险', '中高风险', '高风险'];
const RISK_LEVEL_SELECT_OPTIONS: SelectOption[] = RISK_LEVEL_OPTIONS.map((i): SelectOption => ({ value: i }));

@CustomDialog
struct PortfolioDetailsDialog {
  controller?: CustomDialogController;
  portfolio?: Portfolio;
  build() {
    Column({ space: 15 }) {
      Text(this.portfolio?.name).fontSize(20).fontWeight(FontWeight.Bold)
      Row({ space: 15 }) {
        Text(`风险: ${this.portfolio?.riskLevel}`).fontSize(14).fontColor('#666')
        Text(`策略: ${this.portfolio?.strategy}`).fontSize(14).fontColor('#666')
      }.width('100%').justifyContent(FlexAlign.Start)
      Column({ space: 5 }) {
        Text('组合描述').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start)
        Text(this.portfolio?.description).fontSize(14).fontColor(Color.Gray).width('100%').textAlign(TextAlign.Start)
      }.alignItems(HorizontalAlign.Start)
      Column({ space: 5 }) {
        Text('成份基金').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start)
        ForEach(this.portfolio?.constituentFunds, (fund: BaseFund) => {
          Row() {
            Text(fund.name).fontSize(14).layoutWeight(1)
            Text(fund.code).fontSize(14).fontColor(Color.Gray)
          }.width('100%')
        })
      }.alignItems(HorizontalAlign.Start)
      Button('关闭').width('100%').height(40).margin({ top: 20 }).onClick(() => { this.controller?.close(); })
    }.padding(20).width('90%')
  }
}

@Component
export struct PortfolioManagementPage {
  @State currentTab: number = 0;
  @State portfolios: Portfolio[] = INITIAL_PORTFOLIOS;
  @State newPortfolioName: string = '';
  @State newPortfolioDescription: string = '';
  @State newPortfolioStrategy: string = '';
  @State newPortfolioRiskIndex: number = 0;
  @State selectedFundsForCreation: BaseFund[] = [];
  @State portfolioToShowDetails: Portfolio | undefined = undefined;

  private dialogController: CustomDialogController = new CustomDialogController({
    builder: PortfolioDetailsDialog({ portfolio: this.portfolioToShowDetails }),
    autoCancel: true,
    alignment: DialogAlignment.Center
  });

  createPortfolio() {
    if (!this.newPortfolioName.trim()) { prompt.showToast({ message: '请输入组合名称' }); return; }
    if (!this.newPortfolioStrategy.trim()) { prompt.showToast({ message: '请输入策略名称' }); return; }
    if (!this.newPortfolioDescription.trim()) { prompt.showToast({ message: '请输入组合描述' }); return; }
    if (this.selectedFundsForCreation.length < 2) { prompt.showToast({ message: '请至少选择两只基金' }); return; }
    const newPortfolio: Portfolio = { id: 'p_' + Date.now(), name: this.newPortfolioName.trim(), strategy: this.newPortfolioStrategy.trim(), description: this.newPortfolioDescription.trim(), riskLevel: RISK_LEVEL_OPTIONS[this.newPortfolioRiskIndex] as RiskLevelType, status: '待审核', constituentFunds: [...this.selectedFundsForCreation] };
    this.portfolios.unshift(newPortfolio);
    this.portfolios = [...this.portfolios];
    prompt.showToast({ message: `组合 "${newPortfolio.name}" 创建成功，等待审核` });
    this.newPortfolioName = ''; this.newPortfolioDescription = ''; this.newPortfolioStrategy = ''; this.newPortfolioRiskIndex = 0; this.selectedFundsForCreation = [];
    this.currentTab = 0;
  }

  build() {
    Column() {
      Text('组合产品管理').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
      Tabs({ index: this.currentTab }) {
        TabContent() { this.PortfolioListView() }.tabBar('组合列表')
        TabContent() { this.CreatePortfolioView() }.tabBar('创建组合')
      }
      .onChange(index => this.currentTab = index)
      .barMode(BarMode.Fixed).barWidth('100%').layoutWeight(1)
    }.width('100%').height('100%').backgroundColor('#f0f2f5')
  }

  @Builder PortfolioListView() { List({ space: 12 }) { ForEach(this.portfolios, (portfolio: Portfolio) => { ListItem() { this.PortfolioListItem(portfolio) } }) }.padding({ top: 15, left: 15, right: 15 }).layoutWeight(1) }

  @Builder PortfolioListItem(portfolio: Portfolio) {
    Column({ space: 10 }) {
      // 第一行: 名称和状态
      Row() {
        Text(portfolio.name).fontSize(18).fontWeight(FontWeight.Bold).layoutWeight(1)
        Text(portfolio.status).padding({ left: 8, right: 8, top: 4, bottom: 4 }).fontSize(12).fontColor(Color.White)
          .backgroundColor(portfolio.status === '已上架' ? '#52c41a' : (portfolio.status === '待审核' ? '#faad14' : '#bfbfbf'))
          .borderRadius(4)
      }

      // 第二行: 风险和策略
      Row({ space: 15 }) {
        Text(`风险: ${portfolio.riskLevel}`).fontSize(14).fontColor('#666')
        Text(`策略: ${portfolio.strategy}`).fontSize(14).fontColor('#666')
      }.width('100%').justifyContent(FlexAlign.Start)

      // 【最终正确布局】
      // 第三行（条件显示）：累计收益
      if (portfolio.status === '已上架') {
        Flex({ justifyContent: FlexAlign.Start, alignItems: ItemAlign.Baseline }) {
          Text('累计收益: ').fontSize(14).fontColor(Color.Gray).margin({ right: 4 })
          Text(`${portfolio.cumulativeReturn}%`)
            .fontSize(16).fontWeight(FontWeight.Bold)
            .fontColor(portfolio.cumulativeReturn! >= 0 ? Color.Red : Color.Green)
        }
        .width('100%')
      }

      // 第四行（永远显示）：查看详情按钮，靠右对齐
      Row() {
        Blank() // 这个 Blank 会把按钮推到最右边
        Button('查看详情').height(35).type(ButtonType.Capsule).backgroundColor('#e6f7ff').fontColor('#1890ff')
          .onClick(() => {
            this.portfolioToShowDetails = portfolio;
            this.dialogController.open();
          })
      }
      .width('100%')

    }.padding(15).backgroundColor(Color.White).borderRadius(8)
  }

  @Builder CreatePortfolioView() {
    Scroll() {
      Column({ space: 15 }) {
        Text('组合基础信息').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start)
        TextInput({ placeholder: '组合名称', text: this.newPortfolioName }).onChange(val => this.newPortfolioName = val)
        TextInput({ placeholder: '策略名称', text: this.newPortfolioStrategy }).onChange(val => this.newPortfolioStrategy = val)
        TextInput({ placeholder: '组合描述', text: this.newPortfolioDescription }).onChange(val => this.newPortfolioDescription = val)
        Select(RISK_LEVEL_SELECT_OPTIONS).selected(this.newPortfolioRiskIndex).value(RISK_LEVEL_OPTIONS[this.newPortfolioRiskIndex]).onSelect(index => this.newPortfolioRiskIndex = index)
        Text('选择基金标的').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start).margin({ top: 10 })
        List({ space: 10 }) {
          ForEach(ALL_AVAILABLE_FUNDS, (fund: BaseFund) => {
            ListItem() {
              Row() {
                Toggle({ type: ToggleType.Checkbox, isOn: this.selectedFundsForCreation.some(f => f.id === fund.id) })
                  .onChange(isOn => {
                    if (isOn) { this.selectedFundsForCreation.push(fund); }
                    else { const idx = this.selectedFundsForCreation.findIndex(f => f.id === fund.id); if (idx > -1) this.selectedFundsForCreation.splice(idx, 1); }
                  })
                Text(fund.name).fontSize(16).layoutWeight(1)
                Text(fund.code).fontSize(14).fontColor(Color.Gray)
              }.padding(15).backgroundColor(Color.White).borderRadius(8)
            }
          })
        }.clip(true)
        Button('创建组合产品').width('90%').height(45).margin({ top: 20, bottom: 20 }).onClick(() => this.createPortfolio())
      }.width('90%').padding({ top: 15, bottom: 20 })
    }
  }
}
// // 文件名：PortfolioManagementPage.ets
// import { prompt } from '@kit.ArkUI';
//
// // --- 数据模型定义 ---
//
// interface BaseFund {
//   id: string;
//   name: string;
//   code: string;
// }
//
// interface SelectOption {
//   value: string;
// }
//
// type RiskLevelType = '低风险' | '中低风险' | '中风险' | '中高风险' | '高风险';
//
// interface Portfolio {
//   id: string;
//   name: string;
//   riskLevel: RiskLevelType;
//   manager: string;
//   strategy: string;
//   status: '待审核' | '已上架' | '已下架';
//   constituentFunds: BaseFund[];
//   cumulativeReturn?: number;
// }
//
// // --- 模拟数据 ---
// const ALL_AVAILABLE_FUNDS: BaseFund[] = [
//   { id: 'fund_001', name: '易方达蓝筹精选混合', code: '005827' },
//   { id: 'fund_002', name: '景顺长城新兴成长混合', code: '260108' },
//   { id: 'fund_003', name: '中欧医疗健康混合A', code: '003095' },
//   { id: 'fund_004', name: '招商中证白酒指数', code: '161725' },
//   { id: 'fund_005', name: '国泰纳斯达克100(QDII)', code: '160213' },
// ];
//
// const INITIAL_PORTFOLIOS: Portfolio[] = [
//   {
//     id: 'p_001', name: '稳健增长FOF组合', riskLevel: '中低风险', manager: '王经理',
//     strategy: '固收+策略', status: '已上架', constituentFunds: [ALL_AVAILABLE_FUNDS[0], ALL_AVAILABLE_FUNDS[3]],
//     cumulativeReturn: 15.7
//   },
//   {
//     id: 'p_002', name: 'A股核心动力组合', riskLevel: '中高风险', manager: '李投顾',
//     strategy: '行业轮动', status: '待审核', constituentFunds: [ALL_AVAILABLE_FUNDS[1], ALL_AVAILABLE_FUNDS[2]],
//   }
// ];
//
// const RISK_LEVEL_OPTIONS: RiskLevelType[] = ['低风险', '中低风险', '中风险', '中高风险', '高风险'];
//
// // 【核心修正点】为 .map() 的箭头函数添加显式的返回类型声明
// const RISK_LEVEL_SELECT_OPTIONS: SelectOption[] = RISK_LEVEL_OPTIONS.map((i): SelectOption => ({ value: i }));
//
//
// @Component
// export struct PortfolioManagementPage {
//   @State currentTab: number = 0;
//   @State portfolios: Portfolio[] = INITIAL_PORTFOLIOS;
//
//   @State newPortfolioName: string = '';
//   @State newPortfolioManager: string = '';
//   @State newPortfolioStrategy: string = '';
//   @State newPortfolioRiskIndex: number = 0;
//   @State selectedFundsForCreation: BaseFund[] = [];
//
//   approvePortfolio(portfolioId: string) {
//     const index = this.portfolios.findIndex(p => p.id === portfolioId);
//     if (index > -1) {
//       this.portfolios[index].status = '已上架';
//       this.portfolios[index].cumulativeReturn = parseFloat((Math.random() * 20).toFixed(2));
//       this.portfolios = [...this.portfolios];
//       prompt.showToast({ message: `组合 "${this.portfolios[index].name}" 已审核上架` });
//     }
//   }
//
//   createPortfolio() {
//     if (!this.newPortfolioName.trim()) { prompt.showToast({ message: '请输入组合名称' }); return; }
//     if (!this.newPortfolioManager.trim()) { prompt.showToast({ message: '请输入时任经理/投顾' }); return; }
//     if (!this.newPortfolioStrategy.trim()) { prompt.showToast({ message: '请输入策略名称' }); return; }
//     if (this.selectedFundsForCreation.length < 2) { prompt.showToast({ message: '请至少选择两只基金' }); return; }
//
//     const newPortfolio: Portfolio = {
//       id: 'p_' + Date.now(),
//       name: this.newPortfolioName.trim(),
//       manager: this.newPortfolioManager.trim(),
//       strategy: this.newPortfolioStrategy.trim(),
//       riskLevel: RISK_LEVEL_OPTIONS[this.newPortfolioRiskIndex] as RiskLevelType,
//       status: '待审核',
//       constituentFunds: [...this.selectedFundsForCreation]
//     };
//
//     this.portfolios.unshift(newPortfolio);
//     this.portfolios = [...this.portfolios];
//     prompt.showToast({ message: `组合 "${newPortfolio.name}" 创建成功，等待审核` });
//
//     this.newPortfolioName = '';
//     this.newPortfolioManager = '';
//     this.newPortfolioStrategy = '';
//     this.newPortfolioRiskIndex = 0;
//     this.selectedFundsForCreation = [];
//     this.currentTab = 0;
//   }
//
//   build() {
//     Column() {
//       Text('组合产品管理').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
//       Tabs({ index: this.currentTab }) {
//         TabContent() { this.PortfolioListView() }.tabBar('组合列表')
//         TabContent() { this.CreatePortfolioView() }.tabBar('创建组合')
//       }
//       .onChange(index => this.currentTab = index)
//       .barMode(BarMode.Fixed).barWidth('100%').layoutWeight(1)
//     }.width('100%').height('100%').backgroundColor('#f0f2f5')
//   }
//
//   @Builder PortfolioListView() { List({ space: 12 }) { ForEach(this.portfolios, (portfolio: Portfolio) => { ListItem() { this.PortfolioListItem(portfolio) } }) }.padding({ top: 15, left: 15, right: 15 }).layoutWeight(1) }
//
//   @Builder PortfolioListItem(portfolio: Portfolio) {
//     Column({ space: 10 }) {
//       Row() {
//         Text(portfolio.name).fontSize(18).fontWeight(FontWeight.Bold).layoutWeight(1)
//         Text(portfolio.status).padding({ left: 8, right: 8, top: 4, bottom: 4 }).fontSize(12).fontColor(Color.White).backgroundColor(portfolio.status === '已上架' ? '#52c41a' : (portfolio.status === '待审核' ? '#faad14' : '#bfbfbf')).borderRadius(4)
//       }
//       Row({ space: 15 }) {
//         Text(`风险: ${portfolio.riskLevel}`).fontSize(14).fontColor('#666')
//         Text(`策略: ${portfolio.strategy}`).fontSize(14).fontColor('#666')
//         Text(`时任: ${portfolio.manager}`).fontSize(14).fontColor('#666')
//       }.width('100%').justifyContent(FlexAlign.Start)
//       Row() {
//         if (portfolio.status === '已上架') {
//           Text(`累计收益: `).fontSize(14)
//           Text(`${portfolio.cumulativeReturn}%`).fontSize(16).fontWeight(FontWeight.Bold).fontColor(portfolio.cumulativeReturn! >= 0 ? Color.Red : Color.Green)
//         }
//         Blank()
//         if (portfolio.status === '待审核') {
//           Button('审核上架').height(35).type(ButtonType.Capsule).onClick(() => this.approvePortfolio(portfolio.id))
//         } else {
//           Button('查看详情').height(35).type(ButtonType.Capsule).backgroundColor('#e6f7ff').fontColor('#1890ff').onClick(() => prompt.showToast({ message: `正在查看 "${portfolio.name}"` }))
//         }
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8)
//   }
//
//   @Builder CreatePortfolioView() {
//     Scroll() {
//       Column({ space: 15 }) {
//         Text('组合基础信息').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start)
//         TextInput({ placeholder: '组合名称', text: this.newPortfolioName }).onChange(val => this.newPortfolioName = val)
//         TextInput({ placeholder: '策略名称', text: this.newPortfolioStrategy }).onChange(val => this.newPortfolioStrategy = val)
//         TextInput({ placeholder: '时任经理/投顾', text: this.newPortfolioManager }).onChange(val => this.newPortfolioManager = val)
//         Select(RISK_LEVEL_SELECT_OPTIONS)
//           .selected(this.newPortfolioRiskIndex)
//           .value(RISK_LEVEL_OPTIONS[this.newPortfolioRiskIndex])
//           .onSelect(index => this.newPortfolioRiskIndex = index)
//         Text('选择基金标的').fontSize(16).fontWeight(FontWeight.Medium).width('100%').textAlign(TextAlign.Start).margin({ top: 10 })
//         List({ space: 10 }) {
//           ForEach(ALL_AVAILABLE_FUNDS, (fund: BaseFund) => {
//             ListItem() {
//               Row() {
//                 Toggle({ type: ToggleType.Checkbox, isOn: this.selectedFundsForCreation.some(f => f.id === fund.id) })
//                   .onChange(isOn => {
//                     if (isOn) { this.selectedFundsForCreation.push(fund); }
//                     else { const idx = this.selectedFundsForCreation.findIndex(f => f.id === fund.id); if (idx > -1) this.selectedFundsForCreation.splice(idx, 1); }
//                   })
//                 Text(fund.name).fontSize(16).layoutWeight(1)
//                 Text(fund.code).fontSize(14).fontColor(Color.Gray)
//               }.padding(15).backgroundColor(Color.White).borderRadius(8)
//             }
//           })
//         }.clip(true)
//         Button('创建组合产品').width('90%').height(45).margin({ top: 20, bottom: 20 }).onClick(() => this.createPortfolio())
//       }.width('90%').padding({ top: 15, bottom: 20 })
//     }
//   }
// }