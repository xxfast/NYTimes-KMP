package io.github.xxfast.nytimes.fixtures

import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStoryResponse
import io.github.xxfast.nytimes.models.TopStorySection
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.time.Instant

fun article(
  index: Int,
  section: TopStorySection = TopStorySection("home"),
): Article = Article(
  uri = ArticleUri("nyt://article/$index"),
  section = section,
  subsection = "Subsection $index",
  title = "Story $index",
  description = "Abstract $index",
  url = "https://www.nytimes.com/story-$index",
  byline = "By Reporter $index",
  publishedDate = Instant.fromEpochSeconds(1_700_000_000L + index),
  multimedia = emptyList(),
)

fun articles(count: Int, section: TopStorySection = TopStorySection("home")): List<Article> =
  List(count) { index -> article(index, section) }

/**
 * Web service backed by a Ktor mock engine. Swap [respondWith] between requests to script
 * failure-then-success sequences; every request goes through the real content negotiation
 * and `expectSuccess` path used in production.
 */
class FakeWebService {
  var respondWith: () -> Reply = { Reply.Stories(emptyList()) }
  var requests: Int = 0
    private set

  private val json = Json { ignoreUnknownKeys = true }

  private val client = HttpClient(MockEngine { _ ->
    requests++
    when (val reply = respondWith()) {
      is Reply.Stories -> respond(
        content = json.encodeToString(TopStoryResponse.serializer(), TopStoryResponse(reply.articles)),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
      )

      is Reply.Status -> respond(content = "", status = reply.status)
      Reply.Offline -> throw IOException("Unable to resolve host api.nytimes.com")
    }
  }) {
    install(ContentNegotiation) { json(json) }
    expectSuccess = true
  }

  val service: NyTimesWebService = NyTimesWebService(client)

  sealed interface Reply {
    data class Stories(val articles: List<Article>) : Reply
    data class Status(val status: HttpStatusCode) : Reply
    data object Offline : Reply
  }
}

/** In-memory [KStore] with the same empty-set default the platform stores use. */
fun inMemoryStore(initial: SavedArticles? = null): KStore<SavedArticles> {
  val codec = object : Codec<SavedArticles> {
    private var value: SavedArticles? = initial
    override suspend fun encode(value: SavedArticles?) { this.value = value }
    override suspend fun decode(): SavedArticles? = value
  }
  return storeOf(codec, default = emptySet())
}
