//
//  AppDelegate.swift
//  ios
//
//  Created by Rajapaksage Isuru Rajapakse on 2/11/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import UIKit
import Foundation
import App

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  var window: UIWindow?

  var rootRouterContext = DefaultRouterContextKt.defaultRouterContext()

  func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    IosApp.shared.bootstrap()

    window = UIWindow(frame: UIScreen.main.bounds)
    window?.rootViewController = RootViewController(
      routerContext: rootRouterContext,
      mode: UiMode.current
    )
    window?.makeKeyAndVisible()
    return true
  }

  func applicationDidBecomeActive(_ application: UIApplication) {
    rootRouterContext.resume()
  }

  func applicationWillResignActive(_ application: UIApplication) {
    rootRouterContext.stop()
  }

  func applicationWillTerminate(_ application: UIApplication) {
    rootRouterContext.destroy()
  }
}
