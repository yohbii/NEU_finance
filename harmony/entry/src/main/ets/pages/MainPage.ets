// 文件名：MainPage.ets

import { FundResearchPage } from "./FundResearchPage";
import { FactorPage } from "./FactorPage";
import { PortfolioManagementPage } from "./PortfolioManagementPage";
// 【核心修改点 1】导入新创建的交易管理页面
import { TransactionManagementPage } from "./TransactionManagementPage";

export const Path_Main = 'pages/MainPage';

@Entry
@Component
struct MainPage {
  @State selectedIndex: number = 0;

  @Builder
  myBuilder(itemIndex: number, title: string, icon1: ResourceStr, icon2: ResourceStr) {
    Column() {
      Image(itemIndex === this.selectedIndex ? icon2 : icon1)
        .width(25)
        .height(25)
      Text(title)
        .fontColor(itemIndex === this.selectedIndex ? '#1890ff' : Color.Black)
        .margin({ top: 2 })
        .fontSize(12)
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .onClick(() => {
      this.selectedIndex = itemIndex;
    })
  }

  build() {
    Tabs({ index: this.selectedIndex }) {
      // 第一个 Tab 保持不变
      TabContent() {
        FundResearchPage()
      }
      .tabBar(this.myBuilder(0, '基金研究', $r('app.media.home_black'), $r('app.media.home_red')))

      // 第二个 Tab 保持不变
      TabContent() {
        FactorPage()
      }
      .tabBar(this.myBuilder(1, '因子管理', $r('app.media.data00'), $r("app.media.data11")))

      // 第三个 Tab 保持不变
      TabContent() {
        PortfolioManagementPage()
      }
      .tabBar(this.myBuilder(2, '组合管理', $r('app.media.jin00'), $r('app.media.jin11')))

      // 【核心修改点 2】第四个 Tab 的内容替换为新页面
      TabContent() {
        TransactionManagementPage()
      }
      // 【核心修改点 3】更新 Tab 的标题和图标
      .tabBar(this.myBuilder(3, '交易管理', $r('app.media.chen00'), $r('app.media.chen11')))

    }
    .barPosition(BarPosition.End)
    .barWidth('100%')
    .barHeight(60)
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}
// // 文件名：MainPage.ets
// import { FundResearchPage } from "./FundResearchPage";
// import { FactorPage } from "./FactorPage";
// // 【核心修改点 1】导入新创建的页面
// import { PortfolioManagementPage } from "./PortfolioManagementPage";
//
// export const Path_Main = 'pages/MainPage';
//
// @Entry
// @Component
// struct MainPage {
//   @State selectedIndex: number = 0;
//
//   @Builder
//   myBuilder(itemIndex: number, title: string, icon1: ResourceStr, icon2: ResourceStr) {
//     Column() {
//       Image(itemIndex === this.selectedIndex ? icon2 : icon1)
//         .width(25)
//         .height(25)
//       Text(title)
//         .fontColor(itemIndex === this.selectedIndex ? '#1890ff' : Color.Black)
//         .margin({ top: 2 })
//         .fontSize(12)
//     }
//     .width('100%')
//     .height('100%')
//     .justifyContent(FlexAlign.Center)
//     .onClick(() => {
//       this.selectedIndex = itemIndex;
//     })
//   }
//
//   build() {
//     Tabs({ index: this.selectedIndex }) {
//       // 第一个 Tab 保持不变
//       TabContent() {
//         FundResearchPage()
//       }
//       .tabBar(this.myBuilder(0, '基金研究', $r('app.media.home_black'), $r('app.media.home_red')))
//
//       // 第二个 Tab 保持不变
//       TabContent() {
//         FactorPage()
//       }
//       .tabBar(this.myBuilder(1, '因子管理', $r('app.media.data00'), $r("app.media.data11")))
//
//       // 【核心修改点 2】第三个 Tab 的内容替换为新页面
//       TabContent() {
//         PortfolioManagementPage()
//       }
//       // 【核心修改点 3】更新 Tab 的标题
//       .tabBar(this.myBuilder(2, '组合管理', $r('app.media.jin00'), $r('app.media.jin11')))
//
//       // 第四个 Tab 保持不变
//       TabContent() {
//         FundResearchPage()
//       }
//       .tabBar(this.myBuilder(3, '多维特征', $r('app.media.chen00'), $r('app.media.chen11')))
//     }
//     .barPosition(BarPosition.End)
//     .barWidth('100%')
//     .barHeight(60)
//     .width('100%')
//     .height('100%')
//     .backgroundColor(Color.White)
//   }
// }