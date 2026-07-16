//
//  TopStoriesView.swift
//  ios
//

import SwiftUI
import App

struct TopStoriesView: View {
  @ObservedObject var model: TopStoriesModel
  let onSwitchToCompose: () -> Void
  let onSelectArticle: (StoryRoute) -> Void

  private let columns = [GridItem(.adaptive(minimum: 248), spacing: 16)]

  var body: some View {
    ScrollView {
      VStack(alignment: .leading, spacing: 16) {
        SectionChipsView(
          sectionNames: model.sectionNames,
          selectedName: IosApp.shared.sectionName(section: model.state.section),
          favouritesCount: model.state.numberOfFavourites.map { Int(truncating: $0) },
          onSelect: model.selectSection
        )

        if model.state.articles == nil {
          VStack(spacing: 12) {
            ProgressView()
            Text("Loading")
              .font(.subheadline)
              .foregroundStyle(.secondary)
          }
          .frame(maxWidth: .infinity)
          .padding(.top, 48)
        } else if let articles = model.state.articles {
          LazyVGrid(columns: columns, spacing: 16) {
            ForEach(Array(articles.enumerated()), id: \.offset) { _, summary in
              StorySummaryCard(summary: summary) {
                onSelectArticle(StoryRoute(summary: summary))
              }
            }
          }
          .padding(.horizontal, 16)

          attribution
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
        }
      }
      .padding(.bottom, 24)
    }
    .navigationTitle("My Times News")
    .navigationBarTitleDisplayMode(.large)
    .toolbar {
      ToolbarItem(placement: .topBarTrailing) {
        HStack(spacing: 12) {
          Button {
            model.refresh()
          } label: {
            Image(systemName: "arrow.clockwise")
          }
          .accessibilityLabel("Refresh")

          Button {
            onSwitchToCompose()
          } label: {
            Label("Compose", systemImage: "rectangle.split.2x1")
          }
          .accessibilityLabel("Switch to Compose")
        }
      }
    }
    .refreshable {
      model.refresh()
      // Give the molecule domain a beat to emit loading + results.
      try? await Task.sleep(nanoseconds: 600_000_000)
    }
  }

  private var attribution: some View {
    Link(destination: URL(string: "https://developer.nytimes.com")!) {
      Text("Data provided by The New York Times")
        .font(.caption)
        .foregroundStyle(.secondary)
    }
  }
}
