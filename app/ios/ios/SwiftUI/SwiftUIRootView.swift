//
//  SwiftUIRootView.swift
//  ios
//
//  Compact: NavigationStack. Regular: NavigationSplitView (list | detail).
//

import SwiftUI
import App

struct SwiftUIRootView: View {
  let routerContext: RouterContext
  let onSwitchToCompose: () -> Void

  @Environment(\.horizontalSizeClass) private var sizeClass
  @StateObject private var topStories: TopStoriesModel
  @State private var path = NavigationPath()
  @State private var selectedRoute: StoryRoute?
  @State private var detailPath = NavigationPath()

  init(routerContext: RouterContext, onSwitchToCompose: @escaping () -> Void) {
    self.routerContext = routerContext
    self.onSwitchToCompose = onSwitchToCompose
    _topStories = StateObject(wrappedValue: TopStoriesModel(routerContext: routerContext))
  }

  private var isCompact: Bool {
    sizeClass == .compact
  }

  var body: some View {
    Group {
      if isCompact {
        compactRoot
      } else {
        splitRoot
      }
    }
    .task {
      await topStories.start()
    }
  }

  private var compactRoot: some View {
    NavigationStack(path: $path) {
      TopStoriesView(
        model: topStories,
        selectedUri: nil,
        onSwitchToCompose: onSwitchToCompose,
        onSelectArticle: { path.append($0) }
      )
      .navigationDestination(for: StoryRoute.self) { route in
        StoryDetailView(
          route: route,
          routerContext: routerContext,
          onSelectRelated: { path.append($0) }
        )
      }
    }
  }

  private var splitRoot: some View {
    NavigationSplitView {
      TopStoriesView(
        model: topStories,
        selectedUri: selectedRoute?.uri,
        onSwitchToCompose: onSwitchToCompose,
        onSelectArticle: { route in
          selectedRoute = route
          detailPath = NavigationPath()
        }
      )
      .navigationSplitViewColumnWidth(min: 360, ideal: 420, max: 520)
    } detail: {
      NavigationStack(path: $detailPath) {
        Group {
          if let selectedRoute {
            // `.id` forces a new StoryModel when list selection changes;
            // @StateObject alone keeps the first article's VM.
            StoryDetailView(
              route: selectedRoute,
              routerContext: routerContext,
              onSelectRelated: { detailPath.append($0) }
            )
            .id(selectedRoute.uri)
          } else {
            VStack(spacing: 12) {
              Image(systemName: "newspaper")
                .font(.largeTitle)
                .foregroundStyle(.secondary)
              Text("Select a story")
                .font(.headline)
              Text("Choose an article from the list.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
          }
        }
        .navigationDestination(for: StoryRoute.self) { route in
          StoryDetailView(
            route: route,
            routerContext: routerContext,
            onSelectRelated: { detailPath.append($0) }
          )
          .id(route.uri)
        }
      }
    }
    .navigationSplitViewStyle(.balanced)
  }
}
