package io.github.xxfast.nytimes

import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.destroy
import io.github.xxfast.decompose.router.resume
import io.github.xxfast.decompose.router.stop
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

@OptIn(BetaInteropApi::class)
class AppDelegate @OverrideInit constructor() : UIResponder(), UIApplicationDelegateProtocol {
  companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

  private val routerContext: RouterContext = defaultRouterContext()

  private var _window: UIWindow? = null
  override fun window() = _window
  override fun setWindow(window: UIWindow?) {
    _window = window
  }

  @OptIn(ExperimentalForeignApi::class)
  override fun application(
    application: UIApplication,
    didFinishLaunchingWithOptions: Map<Any?, *>?
  ): Boolean {
    window = UIWindow(frame = UIScreen.mainScreen.bounds)
    window!!.rootViewController = HomeUIViewController(routerContext)
    window!!.makeKeyAndVisible()
    return true
  }

  override fun applicationDidBecomeActive(application: UIApplication) {
    routerContext.resume()
  }

  override fun applicationWillResignActive(application: UIApplication) {
    routerContext.stop()
  }

  override fun applicationWillTerminate(application: UIApplication) {
    routerContext.destroy()
  }
}
