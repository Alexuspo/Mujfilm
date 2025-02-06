package alexus.studio.mujfilm.data.api

import alexus.studio.mujfilm.data.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("movies/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @GET("movies/search")
    suspend fun searchMovies(
        @Query("query") query: String
    ): Response<MoviesResponse>
} 