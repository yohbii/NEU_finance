
import router from '@ohos.router';
import { Path_Login } from './LoginPage';

export const Path_Splash = 'pages/SplashPage'

@Entry
@Component
struct SplashPage {
  //倒计时
  @State countDownSeconds: number = 100;
  private timeId: number = 0;
  private TAG: string = 'AdvertisingPage';

  onPageShow() {
    this.timeId = setInterval(() => {
      if (this.countDownSeconds === 0) {
        this.jumpToAppHomePage();
      } else {
        this.countDownSeconds--;
      }
    }, 1000);
  }

  onPageHide() {
    router.clear();
    clearInterval(this.timeId);
  }

  /**
   * 跳转到主页.
   */
  jumpToAppHomePage() {
    router.replaceUrl({
      url: Path_Login
    }).catch((error: Error) => {
    });
  }

  build() {
    Stack({ alignContent: Alignment.Top }) {
      Image($r('app.media.splash'))
        .width('100%')
        .height('100%')
      Text(`跳过 ${this.countDownSeconds}`)
        .fontColor(Color.White)
        .fontSize('12fp')
        .letterSpacing(0.05)
        .backgroundColor('#33000000')
        .border({
          radius: '18vp',
          width: '1',
          color: Color.White
        })
        .margin({
          top: '50vp',
          left: '260vp'
        })
        .padding({
          left: '12vp',
          top: '8vp',
          right: '12vp',
          bottom: '8vp'
        })
        .onClick(() => {
          this.jumpToAppHomePage();
        })
    }
    .width('100%')
    .height('100%')
  }
}