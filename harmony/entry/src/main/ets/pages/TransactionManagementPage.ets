// 文件名：TransactionManagementPage.ets
import { prompt } from '@kit.ArkUI';
// --- 数据模型定义 ---
type TradeOrderType = '调仓交易' | '每月定投' | '单笔交易';
type TradeOrderStatus = '待处理' | '已下单' | '已回撤';
interface TradeOrder {
  id: string;
  name: string;
  portfolioName: string;
  type: TradeOrderType;
  status: TradeOrderStatus;
  amount: number;
  date: string;
}
type SettlementTradeType = '买入' | '卖出';
type SettlementStatus = '已成交' | '部分成交' | '失败';
interface SettlementSlip {
  id: string;
  account: string;
  fundName: string;
  fundCode: string;
  tradeType: SettlementTradeType;
  settledAmount: number;
  settledPrice: number;
  date: string;
  status: SettlementStatus;
}

// 【核心修正点】扩充所有类型的模拟数据，每种至少5条
const MOCK_TRADE_ORDERS: TradeOrder[] = [
// 调仓交易 (5+)
  { id: 'to_001', name: '2023年Q4组合目标调仓', portfolioName: '稳健增长FOF组合', type: '调仓交易', status: '待处理', amount: 50000, date: '2023-10-08' },
  { id: 'to_004', name: '市场波动应对调仓', portfolioName: '稳健增长FOF组合', type: '调仓交易', status: '已回撤', amount: 25000, date: '2023-09-28' },
  { id: 'to_006', name: '美联储加息应对调仓', portfolioName: '全球科技先锋', type: '调仓交易', status: '待处理', amount: 88000, date: '2023-10-11' },
  { id: 'to_007', name: '消费板块季度再平衡', portfolioName: '大消费升级组合', type: '调仓交易', status: '已下单', amount: 32000, date: '2023-10-02' },
  { id: 'to_008', name: '防御性资产配置调整', portfolioName: '稳健增长FOF组合', type: '调仓交易', status: '已下单', amount: 150000, date: '2023-09-15' },
  { id: 'to_009', name: '半导体行业周期调仓', portfolioName: '全球科技先锋', type: '调仓交易', status: '已回撤', amount: 60000, date: '2023-09-10' },

  // 每月定投 (5+)
  { id: 'to_003', name: '全球科技先锋10月定投', portfolioName: '全球科技先锋', type: '每月定投', status: '已下单', amount: 5000, date: '2023-10-01' },
  { id: 'to_005', name: '新增资金定投计划', portfolioName: 'A股核心动力组合', type: '每月定投', status: '待处理', amount: 10000, date: '2023-10-10' },
  { id: 'to_010', name: '稳健增长FOF组合10月定投', portfolioName: '稳健增长FOF组合', type: '每月定投', status: '已下单', amount: 8000, date: '2023-10-01' },
  { id: 'to_011', name: '大消费升级组合10月定投', portfolioName: '大消费升级组合', type: '每月定投', status: '待处理', amount: 3000, date: '2023-10-15' },
  { id: 'to_012', name: 'A股核心动力组合9月定投', portfolioName: 'A股核心动力组合', type: '每月定投', status: '已下单', amount: 10000, date: '2023-09-10' },

  // 单笔交易 (5+)
  { id: 'to_002', name: 'A股核心动力组合建仓', portfolioName: 'A股核心动力组合', type: '单笔交易', status: '已下单', amount: 100000, date: '2023-10-05' },
  { id: 'to_013', name: '国庆节后市场追加投资', portfolioName: 'A股核心动力组合', type: '单笔交易', status: '待处理', amount: 50000, date: '2023-10-09' },
  { id: 'to_014', name: '新客户专享建仓', portfolioName: '稳健增长FOF组合', type: '单笔交易', status: '已下单', amount: 200000, date: '2023-09-30' },
  { id: 'to_015', name: '科技股低位补仓', portfolioName: '全球科技先锋', type: '单笔交易', status: '已下单', amount: 30000, date: '2023-09-25' },
  { id: 'to_016', name: '债券资产买入', portfolioName: '稳健增长FOF组合', type: '单笔交易', status: '已回撤', amount: 80000, date: '2023-09-22' },
];
const MOCK_SETTLEMENT_SLIPS: SettlementSlip[] = [
// 交割单 (5+)
  { id: 'ss_001', account: '财富账号A', fundName: '易方达蓝筹精选混合', fundCode: '005827', tradeType: '买入', settledAmount: 10000, settledPrice: 1.85, date: '2023-10-06', status: '已成交' },
  { id: 'ss_002', account: '财富账号B', fundName: '招商中证白酒指数', fundCode: '161725', tradeType: '卖出', settledAmount: 5000, settledPrice: 0.88, date: '2023-10-06', status: '已成交' },
  { id: 'ss_003', account: '财富账号A', fundName: '中欧医疗健康混合A', fundCode: '003095', tradeType: '买入', settledAmount: 8000, settledPrice: 2.15, date: '2023-10-05', status: '失败' },
  { id: 'ss_004', account: '财富账号C', fundName: '国泰纳斯达克100(QDII)', fundCode: '160213', tradeType: '买入', settledAmount: 20000, settledPrice: 1.55, date: '2023-10-02', status: '已成交' },
  { id: 'ss_005', account: '财富账号B', fundName: '博时信用债纯债债券', fundCode: '050027', tradeType: '买入', settledAmount: 50000, settledPrice: 1.02, date: '2023-10-09', status: '已成交' },
  { id: 'ss_006', account: '财富账号C', fundName: '华夏国证半导体芯片ETF', fundCode: '159995', tradeType: '买入', settledAmount: 15000, settledPrice: 0.95, date: '2023-09-29', status: '部分成交' },
  { id: 'ss_007', account: '财富账号A', fundName: '天弘中证消费ETF', fundCode: '159928', tradeType: '卖出', settledAmount: 22000, settledPrice: 1.12, date: '2023-09-28', status: '已成交' },
  { id: 'ss_008', account: '财富账号D', fundName: '易方达蓝筹精选混合', fundCode: '005827', tradeType: '买入', settledAmount: 5000, settledPrice: 1.86, date: '2023-09-27', status: '已成交' },
  { id: 'ss_009', account: '财富账号B', fundName: '景顺长城新兴成长混合', fundCode: '260108', tradeType: '卖出', settledAmount: 30000, settledPrice: 3.45, date: '2023-09-26', status: '失败' },
];
@Component
export struct TransactionManagementPage {
  @State mainTabIndex: number = 0;
  @State tradeOrderTabIndex: number = 0;
  @State tradeOrders: TradeOrder[] = MOCK_TRADE_ORDERS;
  @State settlementSlips: SettlementSlip[] = MOCK_SETTLEMENT_SLIPS;
  @State searchQuery: string = '';

  filteredSettlementSlips(): SettlementSlip[] {
    if (!this.searchQuery.trim()) {
      return this.settlementSlips;
    }
    const query = this.searchQuery.toLowerCase();
    return this.settlementSlips.filter(slip =>
    slip.fundName.includes(query) ||
    slip.fundCode.includes(query) ||
    slip.account.toLowerCase().includes(query)
    );
  }

  updateOrderStatus(orderId: string, newStatus: TradeOrderStatus) {
    const index = this.tradeOrders.findIndex(o => o.id === orderId);
    if (index !== -1) {
      this.tradeOrders[index].status = newStatus;
      this.tradeOrders = [...this.tradeOrders];
      prompt.showToast({ message: `订单 ${this.tradeOrders[index].name} 状态已更新为 "${newStatus}"` });
    }
  }

  build() {
    Column() {
      Text('交易管理').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
      Tabs({ index: this.mainTabIndex, barPosition: BarPosition.Start }) {
        TabContent() { this.TradeOrderView() }.tabBar('交易单管理')
        TabContent() { this.SettlementSlipView() }.tabBar('交割单管理')
      }
      .onChange(index => this.mainTabIndex = index)
      .barMode(BarMode.Scrollable)
      .barWidth('100%')
      .layoutWeight(1)
    }.width('100%').height('100%').backgroundColor('#f0f2f5')
  }

  @Builder TradeOrderView() {
    Column() {
      Tabs({ index: this.tradeOrderTabIndex, barPosition: BarPosition.Start }) {
        TabContent() { this.TradeOrderList(['调仓交易']) }.tabBar('调仓交易单')
        TabContent() { this.TradeOrderList(['每月定投']) }.tabBar('每月定投单')
        TabContent() { this.TradeOrderList(['单笔交易']) }.tabBar('单笔交易单')
      }
      .barMode(BarMode.Fixed).barWidth('100%').onChange(index => this.tradeOrderTabIndex = index)
    }.width('100%').height('100%')
  }

  @Builder TradeOrderList(types: TradeOrderType[]) {
    List({ space: 12 }) {
      ForEach(this.tradeOrders.filter(o => types.includes(o.type)), (order: TradeOrder) => {
        ListItem() {
          this.TradeOrderListItem(order)
        }
      })
    }.padding({ top: 15, left: 15, right: 15 }).layoutWeight(1)
  }

  @Builder TradeOrderListItem(order: TradeOrder) {
    Column({ space: 12 }) {
      Row() {
        Text(order.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
        Text(order.status)
          .padding({ left: 8, right: 8, top: 4, bottom: 4 }).fontSize(12)
          .fontColor(order.status === '待处理' ? '#d46b08' : (order.status === '已下单' ? '#389e0d' : '#595959'))
          .backgroundColor(order.status === '待处理' ? '#fffbe6' : (order.status === '已下单' ? '#f6ffed' : '#fafafa'))
          .border({
            width: 1,
            color: order.status === '待处理' ? '#ffe58f' : (order.status === '已下单' ? '#b7eb8f' : '#d9d9d9')
          })
          .borderRadius(4)
      }
      Row() {
        Text(`组合: ${order.portfolioName}`).fontSize(14).fontColor('#666')
        Blank()
        Text(`金额: ¥${order.amount.toFixed(2)}`).fontSize(14).fontColor('#333')
      }
      if (order.status === '待处理') {
        Row({ space: 15 }) {
          Blank()
          Button('一键回撤').type(ButtonType.Normal).backgroundColor('#fff1f0').fontColor('#cf1322')
            .onClick(() => this.updateOrderStatus(order.id, '已回撤'))
          Button('一键下单').type(ButtonType.Capsule).backgroundColor('#1890ff')
            .onClick(() => this.updateOrderStatus(order.id, '已下单'))
        }.width('100%').margin({ top: 8 })
      }
    }.padding(15).backgroundColor(Color.White).borderRadius(8)
  }

  @Builder SettlementSlipView() {
    Column({ space: 12 }) {
      TextInput({ placeholder: '搜索基金名称、代码或财富账号' })
        .margin({ top: 15, left: 15, right: 15 })
        .onChange(val => this.searchQuery = val)
      List({ space: 12 }) {
        ForEach(this.filteredSettlementSlips(), (slip: SettlementSlip) => {
          ListItem() {
            this.SettlementSlipListItem(slip)
          }
        })
      }.padding({ left: 15, right: 15, bottom: 15 }).layoutWeight(1)
    }.width('100%').height('100%')
  }

  @Builder SettlementSlipListItem(slip: SettlementSlip) {
    Column({ space: 10 }) {
      Row() {
        Text(`${slip.fundName} (${slip.fundCode})`).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
        Text(slip.status).fontSize(12).fontColor(slip.status === '已成交' ? '#52c41a' : '#f5222d')
      }
      Row() {
        Text(`账号: ${slip.account}`).fontSize(14).fontColor('#666')
        Blank()
        Text(slip.tradeType)
          .fontSize(14).fontWeight(FontWeight.Bold)
          .fontColor(slip.tradeType === '买入' ? '#f5222d' : '#52c41a')
      }
      Row() {
        Text(`成交价格: ${slip.settledPrice.toFixed(3)}`).fontSize(14).fontColor('#666')
        Blank()
        Text(`成交金额: ¥${slip.settledAmount.toFixed(2)}`).fontSize(14).fontColor('#333')
      }
    }.padding(15).backgroundColor(Color.White).borderRadius(8)
  }
}

//
// // 文件名：TransactionManagementPage.ets
//
// import { prompt } from '@kit.ArkUI';
//
// // --- 数据模型定义 ---
// type TradeOrderType = '调仓交易' | '每月定投' | '单笔交易';
// type TradeOrderStatus = '待处理' | '已下单' | '已回撤';
// interface TradeOrder {
//   id: string;
//   name: string;
//   portfolioName: string;
//   type: TradeOrderType;
//   status: TradeOrderStatus;
//   amount: number;
//   date: string;
// }
//
// type SettlementTradeType = '买入' | '卖出';
// type SettlementStatus = '已成交' | '部分成交' | '失败';
// interface SettlementSlip {
//   id: string;
//   account: string;
//   fundName: string;
//   fundCode: string;
//   tradeType: SettlementTradeType;
//   settledAmount: number;
//   settledPrice: number;
//   date: string;
//   status: SettlementStatus;
// }
//
// // --- 模拟数据 ---
// const MOCK_TRADE_ORDERS: TradeOrder[] = [
//   { id: 'to_001', name: '2023年Q4组合目标调仓', portfolioName: '稳健增长FOF组合', type: '调仓交易', status: '待处理', amount: 50000, date: '2023-10-08' },
//   { id: 'to_002', name: 'A股核心动力组合建仓', portfolioName: 'A股核心动力组合', type: '单笔交易', status: '已下单', amount: 100000, date: '2023-10-05' },
//   { id: 'to_003', name: '全球科技先锋10月定投', portfolioName: '全球科技先锋', type: '每月定投', status: '已下单', amount: 5000, date: '2023-10-01' },
//   { id: 'to_004', name: '市场波动应对调仓', portfolioName: '稳健增长FOF组合', type: '调仓交易', status: '已回撤', amount: 25000, date: '2023-09-28' },
//   { id: 'to_005', name: '新增资金定投计划', portfolioName: 'A股核心动力组合', type: '每月定投', status: '待处理', amount: 10000, date: '2023-10-10' },
// ];
//
// const MOCK_SETTLEMENT_SLIPS: SettlementSlip[] = [
//   { id: 'ss_001', account: '财富账号A', fundName: '易方达蓝筹精选混合', fundCode: '005827', tradeType: '买入', settledAmount: 10000, settledPrice: 1.85, date: '2023-10-06', status: '已成交' },
//   { id: 'ss_002', account: '财富账号B', fundName: '招商中证白酒指数', fundCode: '161725', tradeType: '卖出', settledAmount: 5000, settledPrice: 0.88, date: '2023-10-06', status: '已成交' },
//   { id: 'ss_003', account: '财富账号A', fundName: '中欧医疗健康混合A', fundCode: '003095', tradeType: '买入', settledAmount: 8000, settledPrice: 2.15, date: '2023-10-05', status: '失败' },
//   { id: 'ss_004', account: '财富账号C', fundName: '国泰纳斯达克100(QDII)', fundCode: '160213', tradeType: '买入', settledAmount: 20000, settledPrice: 1.55, date: '2023-10-02', status: '已成交' },
// ];
//
// @Component
// export struct TransactionManagementPage {
//   @State mainTabIndex: number = 0;
//   @State tradeOrderTabIndex: number = 0;
//   @State tradeOrders: TradeOrder[] = MOCK_TRADE_ORDERS;
//   @State settlementSlips: SettlementSlip[] = MOCK_SETTLEMENT_SLIPS;
//   @State searchQuery: string = '';
//
//   // 【核心修正点 1】移除 @Computed 装饰器，并将 getter 改为普通方法
//   filteredSettlementSlips(): SettlementSlip[] {
//     if (!this.searchQuery.trim()) {
//       return this.settlementSlips;
//     }
//     const query = this.searchQuery.toLowerCase();
//     return this.settlementSlips.filter(slip =>
//     slip.fundName.includes(query) ||
//     slip.fundCode.includes(query) ||
//     slip.account.toLowerCase().includes(query)
//     );
//   }
//
//   // 更新交易单状态的模拟方法
//   updateOrderStatus(orderId: string, newStatus: TradeOrderStatus) {
//     const index = this.tradeOrders.findIndex(o => o.id === orderId);
//     if (index !== -1) {
//       this.tradeOrders[index].status = newStatus;
//       // 强制刷新状态数组
//       this.tradeOrders = [...this.tradeOrders];
//       prompt.showToast({ message: `订单 ${this.tradeOrders[index].name} 状态已更新为 "${newStatus}"` });
//     }
//   }
//
//   build() {
//     Column() {
//       Text('交易管理').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
//       Tabs({ index: this.mainTabIndex, barPosition: BarPosition.Start }) {
//         TabContent() { this.TradeOrderView() }.tabBar('交易单管理')
//         TabContent() { this.SettlementSlipView() }.tabBar('交割单管理')
//       }
//       .onChange(index => this.mainTabIndex = index)
//       .barMode(BarMode.Scrollable)
//       .barWidth('100%')
//       .layoutWeight(1)
//     }.width('100%').height('100%').backgroundColor('#f0f2f5')
//   }
//
//   // --- 交易单管理视图 ---
//   @Builder TradeOrderView() {
//     Column() {
//       Tabs({ index: this.tradeOrderTabIndex, barPosition: BarPosition.Start }) {
//         TabContent() { this.TradeOrderList(['调仓交易']) }.tabBar('调仓交易单')
//         TabContent() { this.TradeOrderList(['每月定投']) }.tabBar('每月定投单')
//         TabContent() { this.TradeOrderList(['单笔交易']) }.tabBar('单笔交易单')
//       }
//       .barMode(BarMode.Fixed).barWidth('100%').onChange(index => this.tradeOrderTabIndex = index)
//     }.width('100%').height('100%')
//   }
//
//   @Builder TradeOrderList(types: TradeOrderType[]) {
//     List({ space: 12 }) {
//       ForEach(this.tradeOrders.filter(o => types.includes(o.type)), (order: TradeOrder) => {
//         ListItem() {
//           this.TradeOrderListItem(order)
//         }
//       })
//     }.padding({ top: 15, left: 15, right: 15 }).layoutWeight(1)
//   }
//
//   @Builder TradeOrderListItem(order: TradeOrder) {
//     Column({ space: 12 }) {
//       Row() {
//         Text(order.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
//         Text(order.status)
//           .padding({ left: 8, right: 8, top: 4, bottom: 4 }).fontSize(12)
//           .fontColor(order.status === '待处理' ? '#d46b08' : (order.status === '已下单' ? '#389e0d' : '#595959'))
//           .backgroundColor(order.status === '待处理' ? '#fff7e6' : (order.status === '已下单' ? '#f6ffed' : '#f5f5f5'))
//           .border({
//             width: 1,
//             color: order.status === '待处理' ? '#ffd591' : (order.status === '已下单' ? '#b7eb8f' : '#d9d9d9')
//           })
//           .borderRadius(4)
//       }
//       Row() {
//         Text(`组合: ${order.portfolioName}`).fontSize(14).fontColor('#666')
//         Blank()
//         Text(`金额: ¥${order.amount.toFixed(2)}`).fontSize(14).fontColor('#333')
//       }
//       if (order.status === '待处理') {
//         Row({ space: 15 }) {
//           Blank()
//           Button('一键回撤').type(ButtonType.Normal).backgroundColor('#fff1f0').fontColor('#cf1322')
//             .onClick(() => this.updateOrderStatus(order.id, '已回撤'))
//           Button('一键下单').type(ButtonType.Capsule).backgroundColor('#1890ff')
//             .onClick(() => this.updateOrderStatus(order.id, '已下单'))
//         }.width('100%').margin({ top: 8 })
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8)
//   }
//
//   // --- 交割单管理视图 ---
//   @Builder SettlementSlipView() {
//     Column({ space: 12 }) {
//       TextInput({ placeholder: '搜索基金名称、代码或财富账号' })
//         .margin({ top: 15, left: 15, right: 15 })
//         .onChange(val => this.searchQuery = val)
//       List({ space: 12 }) {
//         // 【核心修正点 2】在 ForEach 中显式调用该方法
//         ForEach(this.filteredSettlementSlips(), (slip: SettlementSlip) => {
//           ListItem() {
//             this.SettlementSlipListItem(slip)
//           }
//         })
//       }.padding({ left: 15, right: 15, bottom: 15 }).layoutWeight(1)
//     }.width('100%').height('100%')
//   }
//
//   @Builder SettlementSlipListItem(slip: SettlementSlip) {
//     Column({ space: 10 }) {
//       Row() {
//         Text(`${slip.fundName} (${slip.fundCode})`).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
//         Text(slip.status).fontSize(12).fontColor(slip.status === '已成交' ? Color.Green : Color.Red)
//       }
//       Row() {
//         Text(`账号: ${slip.account}`).fontSize(14).fontColor('#666')
//         Blank()
//         Text(slip.tradeType)
//           .fontSize(14).fontWeight(FontWeight.Bold)
//           .fontColor(slip.tradeType === '买入' ? Color.Red : Color.Green)
//       }
//       Row() {
//         Text(`成交价格: ${slip.settledPrice.toFixed(3)}`).fontSize(14).fontColor('#666')
//         Blank()
//         Text(`成交金额: ¥${slip.settledAmount.toFixed(2)}`).fontSize(14).fontColor('#333')
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8)
//   }
// }