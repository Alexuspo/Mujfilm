package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): List<Movie>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): List<Movie>
}
