// entry/src/main/ets/pages/GuidePage.ets
import router from '@ohos.router';

@Entry
@Component
struct GuidePage {
  @State currentIndex: number = 0;

  build() {
    Swiper() {
      // 第一页
      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.welcome')) // <-- 这是第一张图片的引用
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)
      }
      .width('100%')
      .height('100%')

      // 【这里是修改点】中间的第二页已经被删除

      // 最后一张（原第三页）
      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.guide3')) // <-- 这是最后一张图片的引用
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)

        // “进入”按钮只在最后一页显示
        Button('进入')
          .width(200)
          .height(50)
          .fontSize(20)
          .fontColor(Color.White)
          .backgroundColor(Color.Orange)
          .margin({ top: 50 })
          .position({ x: '25%', y: '80%' }) // 建议微调位置以适应不同屏幕
          .offset({ x: 0, y: 0 })
          .onClick(() => {
            // 跳转到登录页
            router.replaceUrl({ url: 'pages/LoginPage' });
          });
      }
      .width('100%')
      .height('100%')
    }
    .index(this.currentIndex)
    .autoPlay(false)
    .indicator(true) // 显示底部的指示器圆点
    .loop(false)     // 禁止循环滑动
    .onChange((index: number) => {
      this.currentIndex = index;
    })
  }
}
/*
import router from '@ohos.router';


@Entry
@Component
struct GuidePage {
  @State currentIndex: number = 0;

  build() {
    Swiper() {
      // 第一页
      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.guide1'))
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)
      }
      .width('100%')
      .height('100%')

      // 第二页
      Stack({ alignContent: Alignment.Center }) {
        Image($r('app.media.guide2'))
          .width('100%')
          .height('100%')
          .objectFit(ImageFit.Cover)
      }
      .width('100%')
      .height('100%')

      // 第三页
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
            router.replace({ url: 'pages/LoginPage' });
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
*/