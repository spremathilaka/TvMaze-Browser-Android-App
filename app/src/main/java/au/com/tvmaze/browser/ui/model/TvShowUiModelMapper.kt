package au.com.tvmaze.browser.ui.model

import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.TvShow
import au.com.tvmaze.browser.ui.model.ScheduleDescriptionFormatter

object TvShowUiModelMapper {
    fun map(show: TvShow): TvShowUiModel {
        return TvShowUiModel(
            id = show.id,
            title = show.title ?: "",
            image = show.image?.original ?: "",
            scheduleDescription = show.schedule?.let {
                ScheduleDescriptionFormatter(
                    days = it.days ?: emptyList(),
                    time = it.time ?: ""
                ).buildScheduleDescription()
            } ?: "",
            premieredYear = show.premieredYear ?: "",
            genres = show.genres?.joinToString(", ") ?: "",
            summary = show.summary ?: "",
            rating = show.rating?.average?.toString() ?: ""
        )
    }
}