// entry/src/main/ets/pages/GuidePage.ets
import router from '@ohos.router';

@Entry
@Component
struct GuidePage {
  @State currentIndex: number = 0;

  build() {
    Swiper() {
      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.welcome'))
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)
      }
      .width('100%')
      .height('100%')


      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.guide3'))
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)

        Button('进入')
          .width(200)
          .height(50)
          .fontSize(20)
          .fontColor(Color.White)
          .backgroundColor(Color.Orange)
          .margin({ top: 50 })
          .position({ x: '25%', y: '80%' })
          .offset({ x: 0, y: 0 })
          .onClick(() => {
            router.replaceUrl({ url: 'pages/LoginPage' });
          });
      }
      .width('100%')
      .height('100%')
    }
    .index(this.currentIndex)
    .autoPlay(false)
    .indicator(true)
    .loop(false)
    .onChange((index: number) => {
      this.currentIndex = index;
    })
  }
}