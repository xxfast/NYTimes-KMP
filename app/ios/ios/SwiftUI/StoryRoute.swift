//
//  StoryRoute.swift
//  ios
//
//  SwiftUI-owned navigation destinations (not Decompose).
//

import Foundation
import App

struct StoryRoute: Hashable, Identifiable {
  let section: String
  let uri: String
  let title: String

  var id: String { uri }

  init(section: String, uri: String, title: String) {
    self.section = section
    self.uri = uri
    self.title = title
  }

  init(summary: SummaryState) {
    self.section = IosApp.shared.sectionName(section: summary.section) ?? ""
    self.uri = IosApp.shared.articleUriValue(uri: summary.uri) ?? ""
    self.title = summary.title
  }
}
