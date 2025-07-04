import { promptAction } from '@kit.ArkUI'

class Toast {
  show(s: string) {
    promptAction.showToast({
      message: s,
      duration: 1000,
      bottom: '360vp'
    })
  }
}

let toast = new Toast()

export default toast as Toast