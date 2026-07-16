//
//  UiMode.swift
//  ios
//
//  Persisted UI framework mode for the dual Compose / SwiftUI host.
//

import Foundation

enum UiMode: String {
  case compose
  case swiftUI

  private static let defaultsKey = "ios.uiMode"

  static var current: UiMode {
    get {
      guard let raw = UserDefaults.standard.string(forKey: defaultsKey),
            let mode = UiMode(rawValue: raw) else {
        return .compose
      }
      return mode
    }
    set {
      UserDefaults.standard.set(newValue.rawValue, forKey: defaultsKey)
    }
  }

  mutating func toggle() {
    self = self == .compose ? .swiftUI : .compose
    UiMode.current = self
  }
}
