package au.com.mantelgroup.tvmazebrowser.data

import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.SearchResult
import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.TvShow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("shows")
    suspend fun getTvShows(): List<TvShow>

    @GET("search/shows")
    suspend fun searchTvShows(@Query("q") query: String): List<SearchResult>
}