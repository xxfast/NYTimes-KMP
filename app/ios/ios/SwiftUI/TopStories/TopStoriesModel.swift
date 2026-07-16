//
//  TopStoriesModel.swift
//  ios
//
//  Observes shared TopStoriesViewModel via SKIE StateFlow → AsyncSequence.
//

import Foundation
import SwiftUI
import App

@MainActor
final class TopStoriesModel: ObservableObject {
  @Published private(set) var state: TopStoriesState
  /// Display names from `TopStorySection.name` (via `IosApp.sectionNames()`).
  @Published private(set) var sectionNames: [String] = []

  private let viewModel: TopStoriesViewModel

  init(routerContext: RouterContext) {
    IosApp.shared.bootstrap()
    viewModel = IosApp.shared.createTopStoriesViewModel(context: routerContext)
    state = viewModel.states.value
    sectionNames = IosApp.shared.sectionNames()
  }

  func start() async {
    for await next in viewModel.states {
      state = next
    }
  }

  func refresh() {
    viewModel.onRefresh()
  }

  func selectSection(name: String) {
    viewModel.onSelectSection(section: IosApp.shared.topStorySection(name: name))
  }
}
