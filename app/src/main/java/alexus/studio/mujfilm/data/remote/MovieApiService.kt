package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.data.model.MovieDto
import alexus.studio.mujfilm.data.model.TmdbResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): Response<TmdbResponse<MovieDto>>

    @GET("movie/top_rated")
    suspend fun getRecommendations(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): Response<TmdbResponse<MovieDto>>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): Response<TmdbResponse<MovieDto>>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = ApiConfig.API_KEY,
        @Query("language") language: String = "cs-CZ"
    ): Response<TmdbResponse<MovieDto>>
}
