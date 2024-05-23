package io.github.xxfast.nytimes.server

import io.github.xxfast.nytimes.server.api.HttpClient
import io.github.xxfast.nytimes.server.api.NyTimesWebService
import io.github.xxfast.nytimes.server.services.topStories.TopStoriesService
import io.ktor.http.HttpMethod
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc

fun main() {
  embeddedServer(Netty, 8080) {
    install(RPC)
    routing {
      route("hello", HttpMethod.Get) {
        handle {
          call.respondText("Hello, world!")
        }
      }

      rpc("/topStories") {
        rpcConfig {
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
  }.start(wait = true)
}