package alexus.studio.mujfilm.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        const val API_KEY = "YOUR_API_KEY_HERE" // Nahraďte svým API klíčem z TMDB
    }

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "cs-CZ",
        @Query("region") region: String = "CZ",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "cs-CZ",
        @Query("query") query: String,
        @Query("region") region: String = "CZ"
    ): Response<MovieResponse>
}

data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int,
    val total_results: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String?,
    val overview: String?,
    val vote_average: Double,
    val original_title: String?
)
