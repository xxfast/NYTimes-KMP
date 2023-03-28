package io.github.xxfast.nytimes.server

import io.github.xxfast.nytimes.server.plugins.configureMonitoring
import io.github.xxfast.nytimes.server.plugins.configureRouting
import io.github.xxfast.nytimes.server.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
  embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  configureMonitoring()
  configureSerialization()
  configureRouting()
}
