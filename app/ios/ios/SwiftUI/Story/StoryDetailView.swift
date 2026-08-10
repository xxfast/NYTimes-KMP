//
//  StoryDetailView.swift
//  ios
//

import SwiftUI
import App

struct StoryDetailView: View {
  let route: StoryRoute
  let routerContext: RouterContext
  let onSelectRelated: (StoryRoute) -> Void

  @StateObject private var model: StoryModel
  @Environment(\.openURL) private var openURL

  init(
    route: StoryRoute,
    routerContext: RouterContext,
    onSelectRelated: @escaping (StoryRoute) -> Void
  ) {
    self.route = route
    self.routerContext = routerContext
    self.onSelectRelated = onSelectRelated
    _model = StateObject(
      wrappedValue: StoryModel(routerContext: routerContext, route: route)
    )
  }

  var body: some View {
    Group {
      if model.isLoading {
        VStack(spacing: 12) {
          ProgressView()
          Text("Loading")
            .font(.subheadline)
            .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
      } else if let article = model.state.article {
        articleContent(article)
      } else {
        VStack(spacing: 12) {
          Image(systemName: "newspaper")
            .font(.largeTitle)
            .foregroundStyle(.secondary)
          Text("Story unavailable")
            .font(.headline)
          Text("Pull to refresh or go back.")
            .font(.subheadline)
            .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
      }
    }
    .navigationTitle(model.state.title)
    .navigationBarTitleDisplayMode(.large)
    .toolbar {
      ToolbarItemGroup(placement: .topBarTrailing) {
        Button {
          model.save()
        } label: {
          Image(systemName: model.isSaved ? "heart.fill" : "heart")
            .foregroundStyle(model.isSaved ? Color.red : Color.primary)
        }
        .disabled(model.state.article == nil)
        .accessibilityLabel(model.isSaved ? "Unsave" : "Save")

        Button {
          model.refresh()
        } label: {
          Image(systemName: "arrow.clockwise")
        }
        .accessibilityLabel("Refresh")
      }
    }
    .refreshable {
      model.refresh()
      try? await Task.sleep(nanoseconds: 600_000_000)
    }
    .task {
      await model.start()
    }
  }

  @ViewBuilder
  private func articleContent(_ article: Article) -> some View {
    ScrollView {
      VStack(alignment: .leading, spacing: 16) {
        heroImage(article.multimedia)

        VStack(alignment: .leading, spacing: 12) {
          Text(article.title)
            .font(.title2.weight(.bold))

          HStack(spacing: 8) {
            Text(IosApp.shared.sectionName(section: article.section) ?? route.section)
              .font(.caption.weight(.medium))
              .padding(.horizontal, 8)
              .padding(.vertical, 4)
              .background(Capsule().fill(Color(.tertiarySystemBackground)))

            Text(article.byline)
              .font(.subheadline)
              .foregroundStyle(.secondary)
              .lineLimit(2)
          }

          Text(article.description_)
            .font(.body)

          if !article.subsection.isEmpty {
            Text(article.subsection)
              .font(.caption)
              .foregroundStyle(.secondary)
          }

          Button {
            if let url = URL(string: article.url) {
              openURL(url)
            }
          } label: {
            Label {
              Text(article.url)
                .font(.footnote)
                .lineLimit(2)
                .multilineTextAlignment(.leading)
            } icon: {
              Image(systemName: "arrow.up.right.square")
            }
          }
          .padding(.top, 4)
        }
        .padding(.horizontal, 16)

        if let related = model.state.related, !related.isEmpty {
          relatedSection(related)
        }
      }
      .padding(.bottom, 32)
    }
  }

  /// First multimedia only; full image (no crop).
  @ViewBuilder
  private func heroImage(_ multimedia: [Multimedia]?) -> some View {
    if let urlString = multimedia?.first?.url, let url = URL(string: urlString) {
      AsyncImage(url: url) { phase in
        switch phase {
        case .empty:
          ZStack {
            Color(.tertiarySystemBackground)
            ProgressView()
          }
          .frame(maxWidth: .infinity)
          .frame(height: 200)
        case .success(let image):
          image
            .resizable()
            .scaledToFit()
            .frame(maxWidth: .infinity)
        case .failure:
          EmptyView()
        @unknown default:
          EmptyView()
        }
      }
      .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
      .padding(.horizontal, 16)
    }
  }

  private func relatedSection(_ related: [SummaryState]) -> some View {
    VStack(alignment: .leading, spacing: 12) {
      Text("Related")
        .font(.headline)
        .padding(.horizontal, 16)

      ScrollView(.horizontal, showsIndicators: false) {
        HStack(alignment: .top, spacing: 12) {
          ForEach(Array(related.enumerated()), id: \.offset) { _, summary in
            StorySummaryCard(summary: summary) {
              onSelectRelated(StoryRoute(summary: summary))
            }
            .frame(width: 260)
          }
        }
        .padding(.horizontal, 16)
      }
    }
    .padding(.top, 8)
  }
}
