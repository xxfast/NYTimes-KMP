//
//  StorySummaryCard.swift
//  ios
//

import SwiftUI
import App

struct StorySummaryCard: View {
  let summary: SummaryState
  var isSelected: Bool = false
  let onSelect: () -> Void

  private enum Metrics {
    static let imageHeight: CGFloat = 160
    static let titleHeight: CGFloat = 52
    static let descriptionHeight: CGFloat = 40
    static let metaHeight: CGFloat = 28
  }

  var body: some View {
    Button(action: onSelect) {
      VStack(alignment: .leading, spacing: 8) {
        imageSlot

        Text(summary.title)
          .font(.title3.weight(.semibold))
          .foregroundStyle(.primary)
          .lineLimit(2)
          .multilineTextAlignment(.leading)
          .frame(maxWidth: .infinity, minHeight: Metrics.titleHeight, maxHeight: Metrics.titleHeight, alignment: .topLeading)

        Text(summary.description_)
          .font(.subheadline)
          .foregroundStyle(.secondary)
          .lineLimit(2)
          .multilineTextAlignment(.leading)
          .frame(maxWidth: .infinity, minHeight: Metrics.descriptionHeight, maxHeight: Metrics.descriptionHeight, alignment: .topLeading)

        HStack(spacing: 8) {
          Text(IosApp.shared.sectionName(section: summary.section) ?? "")
            .font(.caption.weight(.medium))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(Capsule().fill(Color(.tertiarySystemBackground)))

          Text(summary.byline)
            .font(.caption)
            .foregroundStyle(.secondary)
            .lineLimit(1)
        }
        .frame(maxWidth: .infinity, minHeight: Metrics.metaHeight, maxHeight: Metrics.metaHeight, alignment: .leading)
      }
      .padding(12)
      .frame(maxWidth: .infinity, alignment: .topLeading)
      .background(
        RoundedRectangle(cornerRadius: 20, style: .continuous)
          .fill(Color(.secondarySystemBackground))
      )
      .overlay(
        RoundedRectangle(cornerRadius: 20, style: .continuous)
          .strokeBorder(isSelected ? Color.accentColor : Color.clear, lineWidth: 2)
      )
    }
    .buttonStyle(.plain)
  }

  @ViewBuilder
  private var imageSlot: some View {
    Group {
      if let urlString = summary.imageUrl, let url = URL(string: urlString) {
        AsyncImage(url: url) { phase in
          switch phase {
          case .empty:
            ZStack {
              Color(.tertiarySystemBackground)
              ProgressView()
            }
          case .success(let image):
            image
              .resizable()
              .scaledToFill()
          case .failure:
            imagePlaceholder
          @unknown default:
            imagePlaceholder
          }
        }
      } else {
        imagePlaceholder
      }
    }
    .frame(maxWidth: .infinity)
    .frame(height: Metrics.imageHeight)
    .clipped()
    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
  }

  private var imagePlaceholder: some View {
    Color(.tertiarySystemBackground)
      .overlay {
        Image(systemName: "photo")
          .foregroundStyle(.secondary)
      }
  }
}
