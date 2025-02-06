package alexus.studio.mujfilm.api

import alexus.studio.mujfilm.data.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CsfdService {
    companion object {
        const val BASE_URL = "https://api.csfd.cz/"
    }

    @GET("film/search")
    suspend fun searchMovies(
        @Query("query") query: String
    ): MovieResponse

    @GET("film/{id}")
    suspend fun getMovieDetails(
        @Query("id") id: Int
    ): MovieResponse

    @GET("film/popular")
    suspend fun getPopularMovies(): MovieResponse
} 