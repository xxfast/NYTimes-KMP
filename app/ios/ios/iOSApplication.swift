//
//  iOSApplication.swift
//  ios
//
//  Created by Rajapaksage Isuru Rajapakse on 15/3/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import NyTimes

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  var window: UIWindow?
  
  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    window = UIWindow(frame: UIScreen.main.bounds)
    let mainViewController = ApplicationKt.Main()
    window?.rootViewController = mainViewController
    window?.makeKeyAndVisible()
    return true
  }
}
