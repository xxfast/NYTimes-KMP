package io.github.xxfast.nytimes.screens.topStories

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.nytimes.fixtures.FakeWebService
import io.github.xxfast.nytimes.fixtures.FakeWebService.Reply
import io.github.xxfast.nytimes.fixtures.article
import io.github.xxfast.nytimes.fixtures.articles
import io.github.xxfast.nytimes.fixtures.inMemoryStore
import io.github.xxfast.nytimes.models.FailureKind
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.screens.summary.SummaryState
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.Refresh
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.SelectSection
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TopStoriesDomainTest {
  private val webService = FakeWebService()
  private val events = MutableSharedFlow<TopStoriesEvent>(extraBufferCapacity = 5)

  private fun TestScope.states(
    initialState: TopStoriesState = TopStoriesState(),
    saved: SavedArticles? = null,
  ): StateFlow<TopStoriesState> = moleculeFlow(Immediate) {
    TopStoriesDomain(initialState, events, webService.service, inMemoryStore(saved))
  }.stateIn(backgroundScope, SharingStarted.Eagerly, initialState)

  @Test
  fun loadsTheHomeSectionOnStart() = runTest {
    webService.respondWith = { Reply.Stories(articles(3)) }

    val loaded = states().first { it.articles != Loading }

    assertEquals(TopStorySections.home, loaded.section)
    assertEquals(listOf("Story 0", "Story 1", "Story 2"), loaded.articles?.map { it.title })
    assertNull(loaded.failure)
  }

  @Test
  fun anEmptySectionIsAnEmptyListNotLoading() = runTest {
    webService.respondWith = { Reply.Stories(emptyList()) }

    val loaded = states().first { it.articles != Loading }

    assertEquals(emptyList(), loaded.articles)
    assertNull(loaded.failure)
  }

  @Test
  fun aServerErrorBecomesAnHttpFailureAndRefreshRetries() = runTest {
    webService.respondWith = { Reply.Status(HttpStatusCode.ServiceUnavailable) }
    val states = states()

    val failed = states.first { it.failure != null }
    assertEquals(FailureKind.Http, failed.failure?.kind)
    assertEquals(503, failed.failure?.statusCode)
    assertNull(failed.articles, "articles stay null so hosts do not show a stale list")

    webService.respondWith = { Reply.Stories(articles(1)) }
    events.emit(Refresh)

    val recovered = states.first { it.articles != Loading }
    assertNull(recovered.failure, "refresh clears the previous failure")
    assertEquals(1, recovered.articles?.size)
  }

  @Test
  fun aConnectionErrorBecomesAnOfflineFailure() = runTest {
    webService.respondWith = { Reply.Offline }

    val failed = states().first { it.failure != null }

    assertEquals(FailureKind.Offline, failed.failure?.kind)
    assertNull(failed.failure?.statusCode)
  }

  @Test
  fun selectingASectionReloadsIt() = runTest {
    val sports = TopStorySection("sports")
    webService.respondWith = { Reply.Stories(articles(2)) }
    val states = states()
    states.first { it.articles != Loading }

    webService.respondWith = { Reply.Stories(articles(4, sports)) }
    events.emit(SelectSection(sports))

    val reloaded = states.first { it.section == sports && it.articles != Loading }
    assertEquals(4, reloaded.articles?.size)
    assertTrue(reloaded.articles.orEmpty().all { it.section == sports })
  }

  @Test
  fun selectingTheCurrentSectionAgainReturnsHome() = runTest {
    val sports = TopStorySection("sports")
    webService.respondWith = { Reply.Stories(articles(1)) }
    val states = states()
    states.first { it.articles != Loading }

    events.emit(SelectSection(sports))
    states.first { it.section == sports && it.articles != Loading }

    events.emit(SelectSection(sports))
    val home = states.first { it.section == TopStorySections.home && it.articles != Loading }

    assertNotNull(home.articles)
  }

  @Test
  fun favouritesComeFromTheStoreWithoutANetworkCall() = runTest {
    val saved = setOf(article(7), article(8))
    val states = states(saved = saved)
    webService.respondWith = { Reply.Stories(articles(1)) }
    states.first { it.articles != Loading }
    val requestsBefore = webService.requests

    events.emit(SelectSection(TopStorySections.favourites))

    val favourites = states.first { it.section == TopStorySections.favourites && it.articles != Loading }
    assertEquals(setOf("Story 7", "Story 8"), favourites.articles?.map { it.title }?.toSet())
    assertEquals(2, favourites.numberOfFavourites)
    assertEquals(requestsBefore, webService.requests, "favourites never hit the web service")
  }

  @Test
  fun aRestoredStateWithArticlesDoesNotReloadOnStart() = runTest {
    val restored = TopStoriesState(articles = articles(2).map(::SummaryState))
    webService.respondWith = { Reply.Stories(articles(9)) }

    val states = states(initialState = restored)
    val current = states.first { it.articles != Loading }

    assertEquals(2, current.articles?.size)
    assertEquals(0, webService.requests)
  }
}
