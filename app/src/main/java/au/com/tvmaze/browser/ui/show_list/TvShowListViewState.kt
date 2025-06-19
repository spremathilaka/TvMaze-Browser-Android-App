package au.com.tvmaze.browser.ui.show_list

import au.com.tvmaze.browser.ui.model.TvShowUiModel


sealed class TvShowListViewState {
    data object Loading : TvShowListViewState()
    data class Ready(val shows: List<TvShowUiModel>) : TvShowListViewState()
    data class Failure(val throwable: Throwable) : TvShowListViewState()
}