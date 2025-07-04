// 文件名：FundResearchPage.ets
import { prompt } from '@kit.ArkUI';
// ------------------- 1. 数据模型定义 (无变化) -------------------
interface Fund { id: number; code: string; name: string; manager: string; company: string; tags: string[]; }
interface FundCompany { id: number; name: string; tags: string[]; }
interface FundManager { id: number; name: string; company: string; tags: string[]; }
// ------------------- 2. 静态数据 (使用原始数据) -------------------
const ALL_TAGS: string[] = ['明星基金', '价值投资', '成长股', '量化策略', '固收+', '行业龙头', '大规模', '高夏普'];
const ALL_FUNDS: Fund[] = [
  { id: 1, code: '161725', name: '招商中证白酒指数', manager: '侯昊', company: '招商基金', tags: ['行业龙头', '大规模'] },
  { id: 2, code: '005827', name: '易方达蓝筹精选混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
  { id: 3, code: '110011', name: '易方达中小盘混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '成长股'] },
  { id: 4, code: '001938', name: '中欧价值发现混合A', manager: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
  { id: 5, code: '003095', name: '中庚价值领航混合', manager: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
  { id: 6, code: '519674', name: '银河创新成长混合A', manager: '郑巍山', company: '银河基金', tags: ['成长股', '行业龙头'] },
  { id: 7, code: '005228', name: '国泰聚信价值优势A', manager: '程洲', company: '国泰基金', tags: ['价值投资', '高夏普'] },
  { id: 8, code: '001550', name: '天弘沪深300ETF联接C', manager: '陈瑶', company: '天弘基金', tags: ['量化策略', '大规模'] },
];
const ALL_COMPANIES: FundCompany[] = [
  { id: 1, name: '易方达基金', tags: ['明星基金', '大规模', '价值投资'] },
  { id: 2, name: '招商基金', tags: ['行业龙头', '大规模'] },
  { id: 3, name: '中欧基金', tags: ['价值投资', '高夏普'] },
  { id: 4, name: '天弘基金', tags: ['量化策略', '固收+'] },
];
const ALL_MANAGERS: FundManager[] = [
  { id: 1, name: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
  { id: 2, name: '侯昊', company: '招商基金', tags: ['行业龙头'] },
  { id: 3, name: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
  { id: 4, name: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
];
@Entry
@Component
export struct FundResearchPage {
  @State currentIndex: number = 0;
  @State fundSearchKey: string = '';
  @State companySearchKey: string = '';
  @State managerSearchKey: string = '';
  @State activeTags: string[] = [];
  @State filteredFunds: Fund[] = [];
  @State filteredCompanies: FundCompany[] = [];
  @State filteredManagers: FundManager[] = [];

  aboutToAppear() { this.applyAllFilters(); }
  applyFundFilters = () => { const key = this.fundSearchKey.trim().toLowerCase(); let funds = ALL_FUNDS; if (key) { funds = funds.filter(f => f.name.toLowerCase().includes(key) || f.code.includes(key)); } if (this.activeTags.length > 0) { funds = funds.filter(f => this.activeTags.every(tag => f.tags.includes(tag))); } this.filteredFunds = funds; }
  applyCompanyFilters = () => { const key = this.companySearchKey.trim().toLowerCase(); let companies = ALL_COMPANIES; if (key) { companies = companies.filter(c => c.name.toLowerCase().includes(key)); } if (this.activeTags.length > 0) { companies = companies.filter(c => this.activeTags.every(tag => c.tags.includes(tag))); } this.filteredCompanies = companies; }
  applyManagerFilters = () => { const key = this.managerSearchKey.trim().toLowerCase(); let managers = ALL_MANAGERS; if (key) { managers = managers.filter(m => m.name.toLowerCase().includes(key)); } if (this.activeTags.length > 0) { managers = managers.filter(m => this.activeTags.every(tag => m.tags.includes(tag))); } this.filteredManagers = managers; }
  applyAllFilters = () => { this.applyFundFilters(); this.applyCompanyFilters(); this.applyManagerFilters(); }
  handleTagChange = () => { this.activeTags = [...this.activeTags]; if (this.currentIndex === 0) this.applyFundFilters(); else if (this.currentIndex === 1) this.applyCompanyFilters(); else this.applyManagerFilters(); }
  getCurrentSearchKey = (): string => { if (this.currentIndex === 0) return this.fundSearchKey; if (this.currentIndex === 1) return this.companySearchKey; return this.managerSearchKey; }

  build() {
    Column() {
      Text('基金研究').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
      Row({ space: 10 }) {
        TextInput({ placeholder: '输入关键词搜索...', text: this.getCurrentSearchKey() })
          .layoutWeight(1).height(45).fontSize(16).padding({ left: 15, right: 15 })
          .backgroundColor('#FFFFFF').borderRadius(25)
          .onChange(val => { if (this.currentIndex === 0) this.fundSearchKey = val; else if (this.currentIndex === 1) this.companySearchKey = val; else this.managerSearchKey = val; })
        Button('搜索').width(80).height(45)
          .onClick(() => { if (this.currentIndex === 0) this.applyFundFilters(); else if (this.currentIndex === 1) this.applyCompanyFilters(); else this.applyManagerFilters(); })
      }.width('95%')

      Text('标签筛选').fontSize(16).fontWeight(FontWeight.Medium).width('95%').textAlign(TextAlign.Start).margin({ top: 20, bottom: 10 })
      Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start, alignItems: ItemAlign.Start }) {
        ForEach(ALL_TAGS, (tag: string) => { this.TagButton(tag) })
      }.width('95%')

      Tabs({ index: this.currentIndex }) {
        // 【核心修正点】在每个 TabContent 中用 Column 包裹列表，以确保顶部对齐
        TabContent() {
          Column() {
            this.FundList()
          }.width('100%').height('100%')
        }.tabBar(this.TabBuilder('全部公募基金', 0))

        TabContent() {
          Column() {
            this.CompanyList()
          }.width('100%').height('100%')
        }.tabBar(this.TabBuilder('基金公司', 1))

        TabContent() {
          Column() {
            this.ManagerList()
          }.width('100%').height('100%')
        }.tabBar(this.TabBuilder('基金经理', 2))
      }
      .barMode(BarMode.Fixed).vertical(false).barWidth('100%')
      .onChange(index => {
        this.currentIndex = index;
        this.fundSearchKey = ''; this.companySearchKey = ''; this.managerSearchKey = '';
        this.activeTags = [];
        this.applyAllFilters();
      })
      .layoutWeight(1).margin({ top: 20 })
    }.width('100%').height('100%').backgroundColor('#f0f2f5');
  }

  @Builder
  TabBuilder(title: string, index: number) {
    Column() {
      Text(title).fontSize(16)
        .fontWeight(this.currentIndex === index ? FontWeight.Bold : FontWeight.Normal)
        .fontColor(this.currentIndex === index ? '#1890ff' : '#000000')
      Divider().strokeWidth(2).color('#1890ff').opacity(this.currentIndex === index ? 1 : 0)
    }.padding(10).width('100%')
  }

  @Builder
  TagButton(tag: string) {
    Button(tag, { type: ButtonType.Capsule })
      .height(32).fontSize(14).margin({ right: 8, bottom: 8 })
      .backgroundColor(this.activeTags.includes(tag) ? '#1890ff' : '#FFFFFF')
      .fontColor(this.activeTags.includes(tag) ? '#FFFFFF' : '#000000')
      .onClick(() => {
        const index = this.activeTags.indexOf(tag);
        if (index > -1) { this.activeTags.splice(index, 1); } else { this.activeTags.push(tag); }
        this.handleTagChange();
      })
  }

  // --- 列表 UI Builders (无变化) ---
  @Builder FundList() { List({ space: 10 }) { ForEach(this.filteredFunds, (item: Fund) => { ListItem() { this.FundListItem(item) } }) }.padding({ left: 10, right: 10 }) }
  @Builder CompanyList() { List({ space: 10 }) { ForEach(this.filteredCompanies, (item: FundCompany) => { ListItem() { this.CompanyListItem(item) } }) }.padding({ left: 10, right: 10 }) }
  @Builder ManagerList() { List({ space: 10 }) { ForEach(this.filteredManagers, (item: FundManager) => { ListItem() { this.ManagerListItem(item) } }) }.padding({ left: 10, right: 10 }) }
  @Builder FundListItem(item: Fund) { Column() { Row() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1); Text(item.code).fontSize(14).fontColor('#666666') }.width('100%'); Text(`公司: ${item.company} | 经理: ${item.manager}`).fontSize(14).fontColor('#666').width('100%').margin({ top: 4, bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor('#FFFFFF').borderRadius(8).alignItems(HorizontalAlign.Start) }
  @Builder CompanyListItem(item: FundCompany) { Column() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).width('100%').margin({ bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor('#FFFFFF').borderRadius(8).alignItems(HorizontalAlign.Start) }
  @Builder ManagerListItem(item: FundManager) { Column() { Row() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1); Text(item.company).fontSize(14).fontColor('#666666') }.width('100%').margin({ bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor('#FFFFFF').borderRadius(8).alignItems(HorizontalAlign.Start) }
}


// // 文件名：FundResearchPage.ets
// import { prompt } from '@kit.ArkUI';
//
// // ------------------- 1. 数据模型定义 (无变化) -------------------
// interface Fund { id: number; code: string; name: string; manager: string; company: string; tags: string[]; }
// interface FundCompany { id: number; name: string; tags: string[]; }
// interface FundManager { id: number; name: string; company: string; tags: string[]; }
//
// // ------------------- 2. 静态数据 (使用原始数据) -------------------
// const ALL_TAGS: string[] = ['明星基金', '价值投资', '成长股', '量化策略', '固收+', '行业龙头', '大规模', '高夏普'];
// const ALL_FUNDS: Fund[] = [
//   { id: 1, code: '161725', name: '招商中证白酒指数', manager: '侯昊', company: '招商基金', tags: ['行业龙头', '大规模'] },
//   { id: 2, code: '005827', name: '易方达蓝筹精选混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
//   { id: 3, code: '110011', name: '易方达中小盘混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '成长股'] },
//   { id: 4, code: '001938', name: '中欧价值发现混合A', manager: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 5, code: '003095', name: '中庚价值领航混合', manager: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
//   { id: 6, code: '519674', name: '银河创新成长混合A', manager: '郑巍山', company: '银河基金', tags: ['成长股', '行业龙头'] },
//   { id: 7, code: '005228', name: '国泰聚信价值优势A', manager: '程洲', company: '国泰基金', tags: ['价值投资', '高夏普'] },
//   { id: 8, code: '001550', name: '天弘沪深300ETF联接C', manager: '陈瑶', company: '天弘基金', tags: ['量化策略', '大规模'] },
// ];
// const ALL_COMPANIES: FundCompany[] = [
//   { id: 1, name: '易方达基金', tags: ['明星基金', '大规模', '价值投资'] },
//   { id: 2, name: '招商基金', tags: ['行业龙头', '大规模'] },
//   { id: 3, name: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 4, name: '天弘基金', tags: ['量化策略', '固收+'] },
// ];
// const ALL_MANAGERS: FundManager[] = [
//   { id: 1, name: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
//   { id: 2, name: '侯昊', company: '招商基金', tags: ['行业龙头'] },
//   { id: 3, name: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 4, name: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
// ];
//
// @Entry
// @Component
// export struct FundResearchPage {
//   @State currentIndex: number = 0;
//
//   // 【改进】为每个模块创建独立的搜索关键字状态
//   @State fundSearchKey: string = '';
//   @State companySearchKey: string = '';
//   @State managerSearchKey: string = '';
//
//   // 保持单一的标签数组，但在切换时清空
//   @State activeTags: string[] = [];
//
//   // 筛选结果的状态保持不变
//   @State filteredFunds: Fund[] = [];
//   @State filteredCompanies: FundCompany[] = [];
//   @State filteredManagers: FundManager[] = [];
//
//   aboutToAppear() {
//     this.applyAllFilters();
//   }
//
//   // 【改进】拆分为独立的筛选函数
//   applyFundFilters = () => {
//     const key = this.fundSearchKey.trim().toLowerCase();
//     let funds = ALL_FUNDS;
//     if (key) { funds = funds.filter(f => f.name.toLowerCase().includes(key) || f.code.includes(key)); }
//     if (this.activeTags.length > 0) { funds = funds.filter(f => this.activeTags.every(tag => f.tags.includes(tag))); }
//     this.filteredFunds = funds;
//   }
//
//   applyCompanyFilters = () => {
//     const key = this.companySearchKey.trim().toLowerCase();
//     let companies = ALL_COMPANIES;
//     if (key) { companies = companies.filter(c => c.name.toLowerCase().includes(key)); }
//     if (this.activeTags.length > 0) { companies = companies.filter(c => this.activeTags.every(tag => c.tags.includes(tag))); }
//     this.filteredCompanies = companies;
//   }
//
//   applyManagerFilters = () => {
//     const key = this.managerSearchKey.trim().toLowerCase();
//     let managers = ALL_MANAGERS;
//     if (key) { managers = managers.filter(m => m.name.toLowerCase().includes(key)); }
//     if (this.activeTags.length > 0) { managers = managers.filter(m => this.activeTags.every(tag => m.tags.includes(tag))); }
//     this.filteredManagers = managers;
//   }
//
//   // 一个辅助函数，用于应用所有筛选，常在状态重置后使用
//   applyAllFilters = () => {
//     this.applyFundFilters();
//     this.applyCompanyFilters();
//     this.applyManagerFilters();
//   }
//
//   // 处理标签点击事件，并强制UI更新
//   handleTagChange = () => {
//     // 通过创建新数组副本的方式，强制UI更新
//     this.activeTags = [...this.activeTags];
//     // 根据当前所在的Tab，只应用对应的筛选
//     if (this.currentIndex === 0) this.applyFundFilters();
//     else if (this.currentIndex === 1) this.applyCompanyFilters();
//     else this.applyManagerFilters();
//   }
//
//   // 获取当前Tab应该使用的搜索关键字
//   getCurrentSearchKey(): string {
//     if (this.currentIndex === 0) return this.fundSearchKey;
//     if (this.currentIndex === 1) return this.companySearchKey;
//     return this.managerSearchKey;
//   }
//
//   build() {
//     Column() {
//       Text('基金研究系统').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
//
//       // 【改进】搜索区域现在包含输入框和按钮
//       Row({ space: 10 }) {
//         TextInput({
//           placeholder: '输入关键词搜索...',
//           text: this.getCurrentSearchKey() // 输入框内容与当前Tab状态绑定
//         })
//           .layoutWeight(1).height(45).fontSize(16).padding({ left: 15, right: 15 })
//           .backgroundColor(Color.White).borderRadius(25)
//           .onChange(val => {
//             // 根据当前Tab，更新对应的 searchKey
//             if (this.currentIndex === 0) this.fundSearchKey = val;
//             else if (this.currentIndex === 1) this.companySearchKey = val;
//             else this.managerSearchKey = val;
//           })
//
//         Button('搜索').width(80).height(45)
//           .onClick(() => {
//             // 根据当前Tab，执行对应的筛选
//             if (this.currentIndex === 0) this.applyFundFilters();
//             else if (this.currentIndex === 1) this.applyCompanyFilters();
//             else this.applyManagerFilters();
//           })
//       }.width('95%')
//
//       Text('标签筛选').fontSize(16).fontWeight(FontWeight.Medium).width('95%').textAlign(TextAlign.Start).margin({ top: 20, bottom: 10 })
//       Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start, alignItems: ItemAlign.Start }) {
//         ForEach(ALL_TAGS, (tag: string) => { this.TagButton(tag) })
//       }.width('95%')
//
//       Tabs({ index: this.currentIndex }) {
//         TabContent() { this.FundList() }.tabBar(this.TabBuilder('全部公募基金', 0))
//         TabContent() { this.CompanyList() }.tabBar(this.TabBuilder('基金公司', 1))
//         TabContent() { this.ManagerList() }.tabBar(this.TabBuilder('基金经理', 2))
//       }
//       .barMode(BarMode.Fixed).vertical(false).barWidth('100%')
//       .onChange(index => {
//         // 【改进】切换Tab时，清空所有模块的搜索框和已选标签，并重新筛选
//         this.currentIndex = index;
//         this.fundSearchKey = '';
//         this.companySearchKey = '';
//         this.managerSearchKey = '';
//         this.activeTags = []; // 清空标签
//         this.applyAllFilters(); // 刷新所有列表
//       })
//       .layoutWeight(1).margin({ top: 20 })
//     }.width('100%').height('100%').backgroundColor('#f0f2f5');
//   }
//
//   @Builder
//   TabBuilder(title: string, index: number) {
//     Column() {
//       Text(title).fontSize(16)
//         .fontWeight(this.currentIndex === index ? FontWeight.Bold : FontWeight.Normal)
//         .fontColor(this.currentIndex === index ? '#1890ff' : Color.Black)
//       Divider().strokeWidth(2).color('#1890ff').opacity(this.currentIndex === index ? 1 : 0)
//     }.padding(10).width('100%')
//   }
//
//   @Builder
//   TagButton(tag: string) {
//     Button(tag, { type: ButtonType.Capsule })
//       .height(32).fontSize(14).margin({ right: 8, bottom: 8 })
//       .backgroundColor(this.activeTags.includes(tag) ? '#1890ff' : Color.White)
//       .fontColor(this.activeTags.includes(tag) ? Color.White : Color.Black)
//       .onClick(() => {
//         const index = this.activeTags.indexOf(tag);
//         if (index > -1) { this.activeTags.splice(index, 1); } else { this.activeTags.push(tag); }
//         // 调用统一的处理器
//         this.handleTagChange();
//       })
//   }
//
//   // --- 列表 UI Builders (无变化) ---
//   @Builder FundList() { List({ space: 10 }) { ForEach(this.filteredFunds, (item: Fund) => { ListItem() { this.FundListItem(item) } }) }.padding({ left: 10, right: 10 }) }
//   @Builder CompanyList() { List({ space: 10 }) { ForEach(this.filteredCompanies, (item: FundCompany) => { ListItem() { this.CompanyListItem(item) } }) }.padding({ left: 10, right: 10 }) }
//   @Builder ManagerList() { List({ space: 10 }) { ForEach(this.filteredManagers, (item: FundManager) => { ListItem() { this.ManagerListItem(item) } }) }.padding({ left: 10, right: 10 }) }
//   @Builder FundListItem(item: Fund) { Column() { Row() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1); Text(item.code).fontSize(14).fontColor(Color.Gray) }.width('100%'); Text(`公司: ${item.company} | 经理: ${item.manager}`).fontSize(14).fontColor('#666').width('100%').margin({ top: 4, bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start) }
//   @Builder CompanyListItem(item: FundCompany) { Column() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).width('100%').margin({ bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start) }
//   @Builder ManagerListItem(item: FundManager) { Column() { Row() { Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1); Text(item.company).fontSize(14).fontColor(Color.Gray) }.width('100%').margin({ bottom: 8 }); Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) { ForEach(item.tags, (tag: string) => { Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 }) }) } }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start) }
// }






// interface Fund {
//   id: number;
//   code: string;
//   name: string;
//   manager: string;
//   company: string;
//   tags: string[];
// }
//
// interface FundCompany {
//   id: number;
//   name: string;
//   tags: string[];
// }
//
// interface FundManager {
//   id: number;
//   name: string;
//   company: string;
//   tags: string[];
// }
//
//
// const ALL_TAGS: string[] = ['明星基金', '价值投资', '成长股', '量化策略', '固收+', '行业龙头', '大规模', '高夏普'];
// const ALL_FUNDS: Fund[] = [
//   { id: 1, code: '161725', name: '招商中证白酒指数', manager: '侯昊', company: '招商基金', tags: ['行业龙头', '大规模'] },
//   { id: 2, code: '005827', name: '易方达蓝筹精选混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
//   { id: 3, code: '110011', name: '易方达中小盘混合', manager: '张坤', company: '易方达基金', tags: ['明星基金', '成长股'] },
//   { id: 4, code: '001938', name: '中欧价值发现混合A', manager: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 5, code: '003095', name: '中庚价值领航混合', manager: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
//   { id: 6, code: '519674', name: '银河创新成长混合A', manager: '郑巍山', company: '银河基金', tags: ['成长股', '行业龙头'] },
//   { id: 7, code: '005228', name: '国泰聚信价值优势A', manager: '程洲', company: '国泰基金', tags: ['价值投资', '高夏普'] },
//   { id: 8, code: '001550', name: '天弘沪深300ETF联接C', manager: '陈瑶', company: '天弘基金', tags: ['量化策略', '大规模'] },
// ];
// const ALL_COMPANIES: FundCompany[] = [
//   { id: 1, name: '易方达基金', tags: ['明星基金', '大规模', '价值投资'] },
//   { id: 2, name: '招商基金', tags: ['行业龙头', '大规模'] },
//   { id: 3, name: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 4, name: '天弘基金', tags: ['量化策略', '固收+'] },
// ];
// const ALL_MANAGERS: FundManager[] = [
//   { id: 1, name: '张坤', company: '易方达基金', tags: ['明星基金', '价值投资', '大规模'] },
//   { id: 2, name: '侯昊', company: '招商基金', tags: ['行业龙头'] },
//   { id: 3, name: '曹名长', company: '中欧基金', tags: ['价值投资', '高夏普'] },
//   { id: 4, name: '丘栋荣', company: '中庚基金', tags: ['价值投资'] },
// ];
//
// @Entry
// @Component
// export struct FundResearchPage {
//   @State currentIndex: number = 0;
//   @State searchKey: string = '';
//   @State activeTags: string[] = [];
//   @State filteredFunds: Fund[] = [];
//   @State filteredCompanies: FundCompany[] = [];
//   @State filteredManagers: FundManager[] = [];
//
//   aboutToAppear() {
//     this.applyFilters();
//   }
//
//   applyFilters() {
//     const key = this.searchKey.trim().toLowerCase();
//     let funds = ALL_FUNDS;
//     if (key) { funds = funds.filter(f => f.name.toLowerCase().includes(key) || f.code.includes(key)); }
//     if (this.activeTags.length > 0) { funds = funds.filter(f => this.activeTags.every(tag => f.tags.includes(tag))); }
//     this.filteredFunds = funds;
//     let companies = ALL_COMPANIES;
//     if (key) { companies = companies.filter(c => c.name.toLowerCase().includes(key)); }
//     if (this.activeTags.length > 0) { companies = companies.filter(c => this.activeTags.every(tag => c.tags.includes(tag))); }
//     this.filteredCompanies = companies;
//     let managers = ALL_MANAGERS;
//     if (key) { managers = managers.filter(m => m.name.toLowerCase().includes(key)); }
//     if (this.activeTags.length > 0) { managers = managers.filter(m => this.activeTags.every(tag => m.tags.includes(tag))); }
//     this.filteredManagers = managers;
//   }
//
//   build() {
//     Column() {
//       Text('基金研究系统').fontSize(24).fontWeight(FontWeight.Bold).margin({ top: 20, bottom: 15 })
//       TextInput({ placeholder: '输入代码、名称或关键词进行搜索' })
//         .width('95%').height(45).fontSize(16).padding({ left: 15, right: 15 })
//         .backgroundColor(Color.White).borderRadius(25)
//         .onChange(val => { this.searchKey = val; this.applyFilters(); })
//       Text('标签筛选').fontSize(16).fontWeight(FontWeight.Medium).width('95%').textAlign(TextAlign.Start).margin({ top: 20, bottom: 10 })
//       Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start, alignItems: ItemAlign.Start }) {
//         ForEach(ALL_TAGS, (tag: string) => { this.TagButton(tag) })
//       }.width('95%')
//
//       Tabs({ index: this.currentIndex }) {
//         TabContent() { this.FundList() }.tabBar(this.TabBuilder('全部公募基金', 0))
//         TabContent() { this.CompanyList() }.tabBar(this.TabBuilder('基金公司', 1))
//         TabContent() { this.ManagerList() }.tabBar(this.TabBuilder('基金经理', 2))
//       }
//       .barMode(BarMode.Fixed)
//       .vertical(false)
//       .barWidth('100%')
//       .onChange(index => {
//         this.currentIndex = index;
//         this.searchKey = '';
//         this.activeTags = [];
//         this.applyFilters();
//       })
//       .layoutWeight(1)
//       .margin({ top: 20 })
//
//     }.width('100%').height('100%').backgroundColor('#f0f2f5');
//   }
//
//   @Builder
//   TabBuilder(title: string, index: number) {
//     Column() {
//       Text(title)
//         .fontSize(16)
//         .fontWeight(this.currentIndex === index ? FontWeight.Bold : FontWeight.Normal)
//         .fontColor(this.currentIndex === index ? '#1890ff' : Color.Black)
//       Divider().strokeWidth(2).color('#1890ff').opacity(this.currentIndex === index ? 1 : 0)
//     }.padding(10).width('100%')
//   }
//
//   @Builder
//   TagButton(tag: string) {
//     Button(tag, { type: ButtonType.Capsule })
//       .height(32).fontSize(14).margin({ right: 8, bottom: 8 })
//       .backgroundColor(this.activeTags.includes(tag) ? '#1890ff' : Color.White)
//       .fontColor(this.activeTags.includes(tag) ? Color.White : Color.Black)
//       .onClick(() => {
//         const index = this.activeTags.indexOf(tag);
//         if (index > -1) { this.activeTags.splice(index, 1); } else { this.activeTags.push(tag); }
//         this.applyFilters();
//       })
//   }
//
//   @Builder
//   FundList() {
//     List({ space: 10 }) {
//       ForEach(this.filteredFunds, (item: Fund) => { ListItem() { this.FundListItem(item) } })
//     }.padding({ left: 10, right: 10 })
//   }
//
//   @Builder
//   CompanyList() {
//     List({ space: 10 }) {
//       ForEach(this.filteredCompanies, (item: FundCompany) => { ListItem() { this.CompanyListItem(item) } })
//     }.padding({ left: 10, right: 10 })
//   }
//
//   @Builder
//   ManagerList() {
//     List({ space: 10 }) {
//       ForEach(this.filteredManagers, (item: FundManager) => { ListItem() { this.ManagerListItem(item) } })
//     }.padding({ left: 10, right: 10 })
//   }
//
//   @Builder
//   FundListItem(item: Fund) {
//     Column() {
//       Row() {
//         Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
//         Text(item.code).fontSize(14).fontColor(Color.Gray)
//       }.width('100%')
//       Text(`公司: ${item.company} | 经理: ${item.manager}`).fontSize(14).fontColor('#666').width('100%').margin({ top: 4, bottom: 8 })
//       Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) {
//         ForEach(item.tags, (tag: string) => {
//           Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 })
//         })
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start)
//   }
//
//   @Builder
//   CompanyListItem(item: FundCompany) {
//     Column() {
//       Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).width('100%').margin({ bottom: 8 })
//       Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) {
//         ForEach(item.tags, (tag: string) => {
//           Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 })
//         })
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start)
//   }
//
//   @Builder
//   ManagerListItem(item: FundManager) {
//     Column() {
//       Row() {
//         Text(item.name).fontSize(16).fontWeight(FontWeight.Bold).layoutWeight(1)
//         Text(item.company).fontSize(14).fontColor(Color.Gray)
//       }.width('100%').margin({ bottom: 8 })
//       Flex({ wrap: FlexWrap.Wrap, justifyContent: FlexAlign.Start }) {
//         ForEach(item.tags, (tag: string) => {
//           Text(tag).fontSize(12).padding({ left: 6, right: 6, top: 3, bottom: 3 }).backgroundColor('#e6f7ff').fontColor('#1890ff').borderRadius(4).margin({ right: 6, bottom: 6 })
//         })
//       }
//     }.padding(15).backgroundColor(Color.White).borderRadius(8).alignItems(HorizontalAlign.Start)
//   }
// }