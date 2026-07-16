//
//  SectionChipsView.swift
//  ios
//

import SwiftUI

struct SectionChipsView: View {
  /// `TopStorySection.name` values.
  let sectionNames: [String]
  let selectedName: String?
  let favouritesCount: Int?
  let onSelect: (String) -> Void

  var body: some View {
    ScrollView(.horizontal, showsIndicators: false) {
      HStack(spacing: 8) {
        ForEach(sectionNames, id: \.self) { name in
          let selected = name == selectedName
          let label: String = {
            if name == "favourites", let favouritesCount, favouritesCount > 0 {
              return "\(name) (\(favouritesCount))"
            }
            return name
          }()

          Button {
            onSelect(name)
          } label: {
            HStack(spacing: 4) {
              if name == "favourites" {
                Image(systemName: "heart.fill")
                  .font(.caption)
              }
              Text(label)
                .font(.subheadline)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(
              Capsule()
                .fill(selected ? Color.accentColor.opacity(0.18) : Color(.secondarySystemBackground))
            )
            .overlay(
              Capsule()
                .strokeBorder(selected ? Color.accentColor.opacity(0.5) : Color.clear, lineWidth: 1)
            )
          }
          .buttonStyle(.plain)
        }
      }
      .padding(.horizontal, 16)
    }
  }
}
