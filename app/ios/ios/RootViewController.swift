//
//  RootViewController.swift
//  ios
//
//  Stable root that swaps Compose vs SwiftUI children without replacing the window.
//

import UIKit
import SwiftUI
import App

final class RootViewController: UIViewController {
  private let routerContext: RouterContext
  private var mode: UiMode
  private weak var activeChild: UIViewController?
  /// Temporary until the Compose top bar hosts the framework switch.
  private var composeSwitchButton: UIButton?

  init(routerContext: RouterContext, mode: UiMode = .current) {
    self.routerContext = routerContext
    self.mode = mode
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = .systemBackground
    show(mode: mode)
  }

  func switchToCompose() {
    guard mode != .compose else { return }
    mode = .compose
    UiMode.current = .compose
    show(mode: .compose)
  }

  func switchToSwiftUI() {
    guard mode != .swiftUI else { return }
    mode = .swiftUI
    UiMode.current = .swiftUI
    show(mode: .swiftUI)
  }

  private func show(mode: UiMode) {
    composeSwitchButton?.removeFromSuperview()
    composeSwitchButton = nil

    let next: UIViewController
    switch mode {
    case .compose:
      next = ApplicationKt.HomeUIViewController(
        routerContext: routerContext,
        onSwitchToSwiftUI: { [weak self] in
          DispatchQueue.main.async {
            self?.switchToSwiftUI()
          }
        }
      )
    case .swiftUI:
      let rootView = SwiftUIRootView(
        routerContext: routerContext,
        onSwitchToCompose: { [weak self] in
          self?.switchToCompose()
        }
      )
      next = UIHostingController(rootView: rootView)
    }

    if let activeChild {
      activeChild.willMove(toParent: nil)
      activeChild.view.removeFromSuperview()
      activeChild.removeFromParent()
    }

    addChild(next)
    next.view.frame = view.bounds
    next.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    view.addSubview(next.view)
    next.didMove(toParent: self)
    activeChild = next

    if mode == .compose {
      installTemporaryComposeSwitchButton()
    }
  }

  private func installTemporaryComposeSwitchButton() {
    let button = UIButton(type: .system)
    button.setTitle("SwiftUI", for: .normal)
    button.titleLabel?.font = .systemFont(ofSize: 14, weight: .semibold)
    button.backgroundColor = UIColor.systemBackground.withAlphaComponent(0.92)
    button.layer.cornerRadius = 16
    button.layer.borderWidth = 1
    button.layer.borderColor = UIColor.separator.cgColor
    button.contentEdgeInsets = UIEdgeInsets(top: 8, left: 12, bottom: 8, right: 12)
    button.translatesAutoresizingMaskIntoConstraints = false
    button.addTarget(self, action: #selector(temporarySwitchTapped), for: .touchUpInside)
    view.addSubview(button)
    NSLayoutConstraint.activate([
      button.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 8),
      button.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -12),
    ])
    composeSwitchButton = button
  }

  @objc private func temporarySwitchTapped() {
    switchToSwiftUI()
  }
}
