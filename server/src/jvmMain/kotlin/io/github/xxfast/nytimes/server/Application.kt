package io.github.xxfast.nytimes.server

import io.github.xxfast.nytimes.server.api.HttpClient
import io.github.xxfast.nytimes.server.api.NyTimesWebService
import io.github.xxfast.nytimes.server.services.topStories.TopStoriesService
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc

val RequestTracePlugin = createRouteScopedPlugin("RequestTracePlugin", { }) {
  onCall { call ->
    println("Processing call: ${call.request.uri}")
  }
}

fun main() {
  embeddedServer(Netty, 8080) { nyTimes() }
    .start(wait = true)
}

private fun Application.nyTimes() {
  install(RPC)
  install(RequestTracePlugin)

  routing {
    route("hello", HttpMethod.Get) {
      handle {
        call.respondText("Hello, world!")
      }
    }

    rpc("/topStories") {
      rpcConfig {
        waitForServices = false

        serialization {
          json()
        }
      }

      registerService { coroutineContext ->
        TopStoriesService(
          coroutineContext = coroutineContext,
          webService = NyTimesWebService(client = HttpClient)
        )
      }
    }
  }
}
