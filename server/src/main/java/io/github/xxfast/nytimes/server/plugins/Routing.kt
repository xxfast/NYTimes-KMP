package io.github.xxfast.nytimes.server.plugins

import io.github.xxfast.nytimes.server.routes.topStories.topStories
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
  routing {
    topStories()
  }
}
