package au.com.tvmaze.browser.ui.show_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.com.mantelgroup.tvmazebrowser.data.TvShowRepository
import au.com.tvmaze.browser.ui.model.TvShowUiModel
import au.com.tvmaze.browser.ui.model.TvShowUiModelMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TvShowListViewModel(
    private val tvShowRepository: TvShowRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState: MutableStateFlow<TvShowListViewState> = MutableStateFlow(TvShowListViewState.Loading)
    val viewState: StateFlow<TvShowListViewState> = _viewState.asStateFlow()

    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(dispatcher) {
            _viewState.update { TvShowListViewState.Loading }
            val newState: TvShowListViewState = try {
                val tvShows = tvShowRepository.getTvShows().map { TvShowUiModelMapper.map(it) }
                TvShowListViewState.Ready(tvShows)
            } catch (e: Exception) {
                TvShowListViewState.Failure(e)
            }
            _viewState.update { newState }
        }
    }

    fun getShowById(showId: Int):TvShowUiModel {
        return viewState.value.let { state ->
            when (state) {
                is TvShowListViewState.Ready -> state.shows.firstOrNull { it.id == showId }
                else -> null
            }
        } ?: throw IllegalArgumentException("Show with ID $showId not found")
    }


    fun searchNow(query: String) {
        viewModelScope.launch(dispatcher) {
            _viewState.value = TvShowListViewState.Loading
            try {
                val tvShowList = tvShowRepository.searchTvShows(query).mapNotNull { it.show?.let { show -> TvShowUiModelMapper.map(show) } }
                _viewState.value = TvShowListViewState.Ready(tvShowList)
            } catch (e: Exception) {
                _viewState.value = TvShowListViewState.Failure(e)
            }
        }
    }

    fun setSearchMode(enabled: Boolean) {
        _isSearchMode.value = enabled
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchAndShowDefault() {
        setSearchMode(false)
        setSearchQuery("")
        refresh()
    }

    class TvShowListViewModelFactory(
        private val repository: TvShowRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TvShowListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TvShowListViewModel(repository, dispatcher) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
