package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.data.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = "4de5de226aa2c5402798e3d9f369016b",
        @Query("language") language: String = "cs",
        @Query("page") page: Int = 1
    ): MovieResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    }
}

data class MovieResponse(
    val page: Int,
    val results: List<Movie>
)
