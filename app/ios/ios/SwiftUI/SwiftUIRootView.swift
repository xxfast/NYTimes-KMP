//
//  SwiftUIRootView.swift
//  ios
//
//  SwiftUI host: NavigationStack over top stories + story routes.
//

import SwiftUI
import App

struct SwiftUIRootView: View {
  let routerContext: RouterContext
  let onSwitchToCompose: () -> Void

  @StateObject private var topStories: TopStoriesModel
  @State private var path = NavigationPath()

  init(routerContext: RouterContext, onSwitchToCompose: @escaping () -> Void) {
    self.routerContext = routerContext
    self.onSwitchToCompose = onSwitchToCompose
    _topStories = StateObject(wrappedValue: TopStoriesModel(routerContext: routerContext))
  }

  var body: some View {
    NavigationStack(path: $path) {
      TopStoriesView(
        model: topStories,
        onSwitchToCompose: onSwitchToCompose,
        onSelectArticle: { route in
          path.append(route)
        }
      )
      .navigationDestination(for: StoryRoute.self) { route in
        StoryStubDetailView(route: route)
      }
    }
    .task {
      await topStories.start()
    }
  }
}
