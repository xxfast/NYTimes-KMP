package io.github.xxfast.nytimes.data

import io.github.xxfast.nytimes.app.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val HttpClient = HttpClient {
  install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }

  install(Logging) { logger = Logger.SIMPLE }

  defaultRequest {
    url {
      host = "api.nytimes.com"
      protocol = URLProtocol.HTTPS
      parameters.append("api-key", BuildKonfig.API_KEY)
    }
  }
}
