package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.data.model.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }

    @GET("movie/popular")
    suspend fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): Response<MovieListResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): Response<MovieListResponse>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "cs-CZ",
        @Query("sort_by") sortBy: String = "vote_average.desc",
        @Query("vote_count.gte") minVotes: Int = 100,
        @Query("with_genres") genres: String? = null,
        @Query("without_genres") excludedGenres: String? = null,
        @Query("vote_average.gte") minRating: Double? = null,
        @Query("with_original_language") language_filter: String = "en"
    ): Response<MovieListResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "cs-CZ"
    ): Response<MovieListResponse>
}
