//
//  StoryModel.swift
//  ios
//
//  Observes shared StoryViewModel via SKIE StateFlow → AsyncSequence.
//

import Foundation
import SwiftUI
import App

@MainActor
final class StoryModel: ObservableObject {
  @Published private(set) var state: StoryState

  private let viewModel: StoryViewModel

  init(routerContext: RouterContext, route: StoryRoute) {
    IosApp.shared.bootstrap()
    viewModel = IosApp.shared.createStoryViewModel(
      context: routerContext,
      section: IosApp.shared.topStorySection(name: route.section),
      uri: IosApp.shared.articleUri(value: route.uri),
      title: route.title
    )
    state = viewModel.states.value
  }

  deinit {
    viewModel.close()
  }

  func start() async {
    for await next in viewModel.states {
      state = next
    }
  }

  func refresh() {
    viewModel.onRefresh()
  }

  func save() {
    viewModel.onSave()
  }

  var isSaved: Bool {
    (state.isSaved as? NSNumber)?.boolValue == true
  }

  var isLoading: Bool {
    state.article == nil
  }
}
