package au.com.mantelgroup.tvmazebrowser

import au.com.mantelgroup.tvmazebrowser.data.ApiService
import au.com.mantelgroup.tvmazebrowser.data.DefaultTvShowRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AppContainer {
    companion object {
        const val BASE_URL = "https://api.tvmaze.com"
        const val CONTENT_TYPE = "application/json"
    }

    val contentType = CONTENT_TYPE.toMediaType()
    val json = Json { ignoreUnknownKeys = true }

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
        .create(ApiService::class.java)

    val tvShowRepository = DefaultTvShowRepository(apiService= apiService)
}