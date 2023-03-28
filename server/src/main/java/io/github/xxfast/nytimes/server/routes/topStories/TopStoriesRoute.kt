package io.github.xxfast.nytimes.server.routes.topStories

import io.github.xxfast.nytimes.core.models.Article
import io.github.xxfast.nytimes.core.models.ArticleUri
import io.github.xxfast.nytimes.core.models.TopStorySections.home
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.datetime.Clock.System.now

fun Routing.topStories() {
  get("/topStories") {
    call.respond(Article(ArticleUri(""), home, "", "", "", "", "", now()))
  }
}