package au.com.mantelgroup.tvmazebrowser.data.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val score: Double?= null,
    val show: TvShow?= null,
)