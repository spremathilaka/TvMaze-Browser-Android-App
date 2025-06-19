package au.com.tvmaze.browser.ui.model

data class TvShowUiModel(
    val id: Int,
    val title: String,
    val image: String,
    val scheduleDescription: String,
    val premieredYear: String,
    val genres: String,
    val summary: String,
    val rating: String,
)