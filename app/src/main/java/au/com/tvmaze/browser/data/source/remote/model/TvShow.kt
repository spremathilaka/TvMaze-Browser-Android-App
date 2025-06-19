package au.com.mantelgroup.tvmazebrowser.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvShow(
    val id: Int = 0,
    @SerialName("name") val title: String? = null,
    @SerialName("premiered") val premiered: String? = null,
    val schedule: Schedule? = null,
    val image: Image? = null,
    val genres: List<String>? = null,
    val summary: String? = null,
    val rating: Rating? = null
) {

    val premieredYear: String? get() = premiered?.split("-")?.getOrNull(0)

    @Serializable
    data class Schedule(
        val time: String? = null,
        val days: List<String>? = null
    )

    @Serializable
    data class Image(
        val original: String? = null
    )
    @Serializable
    data class Rating(
        val average: Double? = null
    )
}
