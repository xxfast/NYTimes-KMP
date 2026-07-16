//
//  StoryStubDetailView.swift
//  ios
//
//  Placeholder detail until milestone 3 wires StoryViewModel.
//

import SwiftUI

struct StoryStubDetailView: View {
  let route: StoryRoute

  var body: some View {
    ScrollView {
      VStack(alignment: .leading, spacing: 12) {
        Text(route.title)
          .font(.largeTitle.bold())

        Text(route.section)
          .font(.subheadline.weight(.semibold))
          .foregroundStyle(.secondary)

        Text(route.uri)
          .font(.caption.monospaced())
          .foregroundStyle(.tertiary)
          .textSelection(.enabled)

        Text("Full story detail (save, media, related) lands in the next milestone.")
          .font(.body)
          .foregroundStyle(.secondary)
          .padding(.top, 8)
      }
      .frame(maxWidth: .infinity, alignment: .leading)
      .padding()
    }
    .navigationTitle(route.section)
    .navigationBarTitleDisplayMode(.inline)
  }
}
