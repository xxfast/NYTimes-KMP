package io.github.xxfast.nytimes.screens.story

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.kstore.KStore
import io.github.xxfast.nytimes.fixtures.FakeWebService
import io.github.xxfast.nytimes.fixtures.FakeWebService.Reply
import io.github.xxfast.nytimes.fixtures.article
import io.github.xxfast.nytimes.fixtures.articles
import io.github.xxfast.nytimes.fixtures.inMemoryStore
import io.github.xxfast.nytimes.models.FailureKind
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoryDomainTest {
  private val section = TopStorySection("home")
  private val story = article(2, section)
  private val webService = FakeWebService()
  private val events = MutableSharedFlow<StoryEvent>(extraBufferCapacity = 5)

  private fun TestScope.states(
    store: KStore<SavedArticles> = inMemoryStore(),
  ): StateFlow<StoryState> {
    val initialState = StoryState(story.title, Loading)
    return moleculeFlow(Immediate) {
      StoryDomain(section, story.uri, story.title, initialState, events, webService.service, store)
    }.stateIn(backgroundScope, SharingStarted.Eagerly, initialState)
  }

  @Test
  fun loadsTheStoryAndUpToThreeRelatedStoriesFromTheSameSection() = runTest {
    webService.respondWith = { Reply.Stories(articles(6, section)) }

    val loaded = states().first { it.article != Loading }

    assertEquals(story, loaded.article)
    assertEquals(3, loaded.related?.size)
    assertFalse(loaded.related.orEmpty().any { it.uri == story.uri }, "the story is not related to itself")
    assertNull(loaded.failure)
  }

  @Test
  fun aStoryMissingFromTheSectionIsANotFoundFailure() = runTest {
    webService.respondWith = { Reply.Stories(articles(1, section)) }

    val failed = states().first { it.failure != null }

    assertEquals(FailureKind.NotFound, failed.failure?.kind)
    assertNull(failed.article)
  }

  @Test
  fun aSavedStoryLoadsFromTheStoreEvenWhenTheSectionNoLongerHasIt() = runTest {
    webService.respondWith = { Reply.Stories(articles(1, section)) }

    val loaded = states(store = inMemoryStore(setOf(story))).first { it.article != Loading }

    assertEquals(story, loaded.article)
    assertEquals(true, loaded.isSaved)
    assertNull(loaded.failure)
  }

  @Test
  fun aServerErrorBecomesAnHttpFailureAndRefreshRetries() = runTest {
    webService.respondWith = { Reply.Status(HttpStatusCode.InternalServerError) }
    val states = states()

    val failed = states.first { it.failure != null }
    assertEquals(FailureKind.Http, failed.failure?.kind)
    assertEquals(500, failed.failure?.statusCode)

    webService.respondWith = { Reply.Stories(articles(4, section)) }
    events.emit(Refresh)

    val recovered = states.first { it.article != Loading }
    assertNull(recovered.failure)
    assertEquals(story, recovered.article)
  }

  @Test
  fun saveTogglesTheStoryInTheStore() = runTest {
    webService.respondWith = { Reply.Stories(articles(4, section)) }
    val store = inMemoryStore()
    val states = states(store)
    val loaded = states.first { it.article != Loading }
    assertEquals(false, loaded.isSaved, "an empty store means not saved, not unknown")

    events.emit(Save)
    val saved = states.first { it.isSaved == true }
    assertTrue(store.get().orEmpty().contains(story))
    assertEquals(true, saved.isSaved)

    events.emit(Save)
    states.first { it.isSaved == false }
    assertFalse(store.get().orEmpty().contains(story))
  }
}
