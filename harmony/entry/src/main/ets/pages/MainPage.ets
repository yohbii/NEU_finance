// MainPage.ets

import { FundResearchPage } from "./FundResearchPage";
import { FactorPage } from "./FactorPage";
import { PortfolioManagementPage } from "./PortfolioManagementPage";
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
      TabContent() {
        FundResearchPage()
      }
      .tabBar(this.myBuilder(0, '基金研究', $r('app.media.home_black'), $r('app.media.home_red')))

      TabContent() {
        FactorPage()
      }
      .tabBar(this.myBuilder(1, '因子管理', $r('app.media.data00'), $r("app.media.data11")))

      TabContent() {
        PortfolioManagementPage()
      }
      .tabBar(this.myBuilder(2, '组合管理', $r('app.media.jin00'), $r('app.media.jin11')))

      TabContent() {
        TransactionManagementPage()
      }
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