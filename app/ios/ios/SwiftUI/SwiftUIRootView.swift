//
//  SwiftUIRootView.swift
//  ios
//
//  Milestone 1 smoke shell: bootstrap + SKIE StateFlow collect + mode switch.
//  Full TopStories UI lands in the next milestone.
//

import SwiftUI
import App

struct SwiftUIRootView: View {
  let routerContext: RouterContext
  let onSwitchToCompose: () -> Void

  @StateObject private var smoke = SmokeViewModel()

  var body: some View {
    NavigationStack {
      VStack(spacing: 16) {
        Text("SwiftUI mode")
          .font(.largeTitle.bold())

        Text("SKIE smoke")
          .font(.headline)
          .foregroundStyle(.secondary)

        if let section = smoke.sectionName {
          Text("Section: \(section)")
            .font(.body.monospaced())
        } else {
          ProgressView("Loading shared VM…")
        }

        if let count = smoke.articleCount {
          Text("Articles: \(count)")
            .font(.body.monospaced())
        }

        Button("Refresh") {
          smoke.refresh()
        }
        .buttonStyle(.bordered)

        Button("Switch to Compose") {
          onSwitchToCompose()
        }
        .buttonStyle(.borderedProminent)
      }
      .padding()
      .navigationTitle("NYTimes")
      .toolbar {
        ToolbarItem(placement: .topBarTrailing) {
          Button {
            onSwitchToCompose()
          } label: {
            Label("Compose", systemImage: "rectangle.split.2x1")
          }
        }
      }
    }
    .task {
      await smoke.start(routerContext: routerContext)
    }
  }
}

/// Collects `TopStoriesViewModel.states` via SKIE (`StateFlow` → `AsyncSequence`).
@MainActor
final class SmokeViewModel: ObservableObject {
  @Published private(set) var sectionName: String?
  @Published private(set) var articleCount: Int?

  private var viewModel: TopStoriesViewModel?

  func start(routerContext: RouterContext) async {
    IosApp.shared.bootstrap()
    let vm = IosApp.shared.createTopStoriesViewModel(context: routerContext)
    viewModel = vm

    // SKIE maps StateFlow to an AsyncSequence-preserving generic type.
    // TopStorySection is a Kotlin value class; nullable uses export as `Any` (underlying String).
    for await state in vm.states {
      sectionName = Self.sectionName(from: state.section)
      if let articles = state.articles {
        articleCount = articles.count
      } else {
        articleCount = nil
      }
    }
  }

  func refresh() {
    viewModel?.onRefresh()
  }

  private static func sectionName(from section: Any?) -> String? {
    switch section {
    case let name as String:
      return name
    case let name as NSString:
      return name as String
    case .some(let value):
      return String(describing: value)
    case .none:
      return nil
    }
  }
}
