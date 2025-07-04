// 导入页面路由模块
import router from '@ohos.router';
// 导入弹窗
import promptAction from '@ohos.promptAction';
// import { preferences } from '@kit.ArkData'; // <- 移除
// import PreferenceUtil from '../util/PreferencesUtil'; // <- 移除
// import { Path_Regist } from './RegistPage'; // <- 移除
import { Path_Main } from './MainPage';

export const Path_Login = 'pages/LoginPage'

@Entry
@Component
struct Login {
  @State user: string = ''; // 用户名 (初始值设为空，让用户输入)
  @State password: string = ''; // 密码 (初始值设为空)

  // aboutToAppear() { // <- 移除
  //   PreferenceUtil.init(getContext(this));
  // }

  build() {
    Row() {
      Column() {
        // logo
        Image($r("app.media.icon"))
          .width(80)
          .borderRadius(10)
          .margin({ bottom: '20vp' })
        // 欢迎文字
        Text('账号登录')
          .fontSize('30fp')
          .fontWeight(FontWeight.Bold)
        // 用户名和密码输入框
        TextInput({ placeholder: '请输入账号' }) // 提示文字可以更通用
          .inputStyle()
          .maxLength(11)
          .onChange(data => {
            this.user = data;
          })
          .margin({ left: 20, right: 20, top: 20 })
        TextInput({ placeholder: '请输入密码' })
          .type(InputType.Password)
          .inputStyle()
          .onChange(data => {
            this.password = data;
          }).margin({ left: 20, right: 20, top: 10 })
        // 文字提示
        Row() {
          Text('忘记密码')
            .fontSize('16fp')
            .fontColor(Color.Blue)
            .margin({ right: 30, top: 10 })
        }
        .width('100%')
        .justifyContent(FlexAlign.End)
        // 登录按钮
        Button('登录', { type: ButtonType.Capsule })
          .width('60%')
          .margin({ top: 20 })
          .backgroundColor($r('app.color.red'))
          .onClick(() => {
            this.loginBtnClick()
          })

        // Row() { // <- 移除注册按钮
        //   Text('注册')
        //     .fontSize('16fp')
        //     .fontColor(Color.Blue)
        // }
        // .width('100%')
        // .justifyContent(FlexAlign.Center)
        // .margin({ top: 15 })
        // .onClick(() => {
        //   this.registBtnClick()
        // })

        // 其他
        Row() {
          Blank().height(0.5).layoutWeight(1)
            .backgroundColor($r('app.color.tint_gray'))
            .margin({ left: 10, right: 10 })
          Text('其他登录方式')
            .fontSize('16fp')
            .fontColor(Color.Gray)
          Blank().height(0.5).layoutWeight(1)
            .backgroundColor($r('app.color.tint_gray'))
            .margin({ left: 10, right: 10 })
        }
        .width('100%')
        .justifyContent(FlexAlign.Center)
        .margin({ top: 40 })
        // 三个按钮
        Row() {
          Row() {
            Image($r('app.media.huawei')).width(40).height(40)
            Text('华为账号')
          }.layoutWeight(1).alignItems(VerticalAlign.Center).justifyContent(FlexAlign.Center)
          Row() {
            Image($r('app.media.weixin')).width(40).height(40)
            Text('微信')
          }.layoutWeight(1).alignItems(VerticalAlign.Center).justifyContent(FlexAlign.Center)
        }
        .width('100%')
        .justifyContent(FlexAlign.SpaceAround)
        .margin('20vp')
      }
      .width('100%')
    }
    .height('100%')
  }

  // 【核心修改】loginBtnClick 函数
  loginBtnClick() {


      promptAction.showToast({
        message: '登录成功',
        duration: 1000,
        bottom: '360vp'
      })
      // 路由跳转
      router.replaceUrl({
        url: Path_Main
      }).catch((err: Error) => {
        console.error(`Router Error: ${JSON.stringify(err)}`);
      })
    return;


    // 1. 判断用户名和密码非空
    if (this.user.trim() === '' || this.password.trim() === '') {
      promptAction.showToast({
        message: '账号或密码不能为空',
        duration: 1000,
        bottom: '360vp'
      })
      return
    }

    // 2. 直接比较写死的账号和密码
    if (this.user === 'admin' && this.password === 'admin') {
      // 登录成功
      promptAction.showToast({
        message: '登录成功',
        duration: 1000,
        bottom: '360vp'
      })
      // 路由跳转
      router.replaceUrl({
        url: Path_Main
      }).catch((err: Error) => {
        console.error(`Router Error: ${JSON.stringify(err)}`);
      })
    } else {
      // 登录失败
      promptAction.showToast({
        message: '账号或密码不正确',
        duration: 1000,
        bottom: '360vp'
      })
    }
  }

}

@Extend(TextInput)
function inputStyle() {
  .placeholderColor(0x999999)
  .maxLength(20) // 密码和账号长度可能超过6，适当放宽
  .padding('12vp')
  .margin('10vp')
}

@Extend(Button)
function circleBtnStyle() {
  .width('60vp')
  .height('60vp')
}