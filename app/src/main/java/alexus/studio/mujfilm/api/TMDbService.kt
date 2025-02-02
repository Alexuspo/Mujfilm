package alexus.studio.mujfilm.api

import alexus.studio.mujfilm.data.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TMDbService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0ZGU1ZGUyMjZhYTJjNTQwMjc5OGUzZDlmMzY5MDE2YiIsIm5iZiI6MTczODQwNjAxOC45MTgwMDAyLCJzdWIiOiI2NzlkZjg4MmFjNWE3OTUxYjljYjNlY2YiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.GYRspo4XbUCiuFjBCa2AQfEVUhT2XfO2P4EP5BFU5pY"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") auth: String = AUTH_TOKEN,
        @Query("language") language: String = "cs-CZ",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Header("Authorization") auth: String = AUTH_TOKEN,
        @Query("query") query: String,
        @Query("language") language: String = "cs-CZ"
    ): MovieResponse
}

data class MovieResponse(
    val page: Int,
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val vote_average: Double
)
