package au.com.mantelgroup.tvmazebrowser.data

import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.SearchResult
import au.com.mantelgroup.tvmazebrowser.data.source.remote.model.TvShow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TvShowRepository {
    suspend fun getTvShows(): List<TvShow>
    suspend fun searchTvShows(query: String): List<SearchResult>
}

class DefaultTvShowRepository(
    private val ioDispatcher: CoroutineDispatcher=Dispatchers.IO,
    private val apiService: ApiService
) : TvShowRepository {

    override suspend fun getTvShows(): List<TvShow> {
        return  withContext(ioDispatcher){
            apiService.getTvShows()
        }
    }

    override suspend fun searchTvShows(query: String): List<SearchResult> {
        return  withContext(ioDispatcher) {
             apiService.searchTvShows(query)
        }
    }
}
