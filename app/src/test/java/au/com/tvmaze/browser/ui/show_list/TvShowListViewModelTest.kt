package au.com.tvmaze.browser.ui.show_list

import au.com.mantelgroup.tvmazebrowser.data.TvShowRepository
import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.SearchResult
import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TvShowListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val sampleTvShow = TvShow(
        id = 2,
        title = "Sample Title",
        schedule = TvShow.Schedule(
            days = listOf("Monday"),
            time = "08:00"
        ),
        image = TvShow.Image("sample_image_url"),
        premiered = "2018",
        genres = emptyList(),
        summary = "<p><b>Under the Dome</b> is the story of a small town that is .",
        rating = TvShow.Rating(8.4),
    )

    @Test
    fun initialViewStateIsLoading() = runTest {
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> {
                return listOf(
                    sampleTvShow
                )
            }

            override suspend fun searchTvShows(query: String): List<SearchResult> {
                return listOf(
                    SearchResult(
                        show = sampleTvShow
                    )
                )
            }
        }
        val tvShowListViewModel = TvShowListViewModel(tvShowRepository, testDispatcher)

        Assert.assertEquals(
            TvShowListViewState.Loading,
            tvShowListViewModel.viewState.value
        )
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(tvShowListViewModel.viewState.value is TvShowListViewState.Ready)
    }

    @Test
    fun viewStateIsReadyOnSuccessfulLoad() = runTest {
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> {
                return listOf(
                    sampleTvShow
                )
            }

            override suspend fun searchTvShows(query: String): List<SearchResult> {
                return listOf(
                    SearchResult(
                        show = sampleTvShow
                    )
                )
            }
        }

        val tvShowListViewModel = TvShowListViewModel(tvShowRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(
            tvShowListViewModel.viewState.value is TvShowListViewState.Ready
        )
    }

    @Test
    fun viewStateIsFailureOnUnsuccessfulLoad() = runTest {
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> {
                throw IllegalStateException("Fake Exception")
            }

            override suspend fun searchTvShows(query: String): List<SearchResult> {
                throw IllegalStateException("Fake Exception")
            }
        }
        val tvShowListViewModel = TvShowListViewModel(tvShowRepository, testDispatcher)

        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(
            tvShowListViewModel.viewState.value is TvShowListViewState.Failure
        )
    }

    @Test
    fun searchNow_updatesViewStateWithSearchResults() = runTest {
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> = emptyList()
            override suspend fun searchTvShows(query: String): List<SearchResult> {
                return listOf(SearchResult(show = sampleTvShow))
            }
        }
        val viewModel = TvShowListViewModel(tvShowRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.searchNow("Sample")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(viewModel.viewState.value is TvShowListViewState.Ready)

        val readyState = viewModel.viewState.value as TvShowListViewState.Ready
        Assert.assertEquals(1, readyState.shows.size)
        Assert.assertEquals(sampleTvShow.id, readyState.shows[0].id)
    }

    @Test
    fun searchNow_setsFailureOnException() = runTest {
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> = emptyList()
            override suspend fun searchTvShows(query: String): List<SearchResult> {
                throw IllegalStateException("Search failed")
            }
        }
        val viewModel = TvShowListViewModel(tvShowRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.searchNow("fail")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(viewModel.viewState.value is TvShowListViewState.Failure)
    }

    @Test
    fun clearSearchAndShowDefault_resetsSearchAndShowsDefaultList() = runTest {
        var getTvShowsCalled = false
        val defaultShow = sampleTvShow.copy(id = 10, title = "Default Show")
        val tvShowRepository = object : TvShowRepository {
            override suspend fun getTvShows(): List<TvShow> {
                getTvShowsCalled = true
                return listOf(defaultShow)
            }

            override suspend fun searchTvShows(query: String): List<SearchResult> {
                return listOf(SearchResult(show = sampleTvShow))
            }
        }
        val viewModel = TvShowListViewModel(tvShowRepository, testDispatcher)

        viewModel.setSearchMode(true)
        viewModel.setSearchQuery("Sample")

        viewModel.clearSearchAndShowDefault()

        Assert.assertTrue(viewModel.viewState.value is TvShowListViewState.Loading)
        testDispatcher.scheduler.advanceUntilIdle()

        Assert.assertTrue(viewModel.viewState.value is TvShowListViewState.Ready)
        val readyState = viewModel.viewState.value as TvShowListViewState.Ready
        Assert.assertEquals(defaultShow.id, readyState.shows[0].id)
        Assert.assertEquals("", viewModel.searchQuery.value)
        Assert.assertEquals(false, viewModel.isSearchMode.value)
        Assert.assertTrue(getTvShowsCalled)
    }
}