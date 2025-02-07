package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.data.model.MovieDto
import alexus.studio.mujfilm.data.model.TmdbResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface TmdbApiService {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }

    @GET("movie/popular")
    suspend fun getMovies(): Response<TmdbResponse<MovieDto>>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): Response<TmdbResponse<MovieDto>>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") genreIds: String? = null,
        @Query("page") page: Int = 1,
        @Query("vote_count.gte") minVoteCount: Int = 100
    ): Response<TmdbResponse<MovieDto>>

    @GET("genre/movie/list")
    suspend fun getGenres(): Response<TmdbResponse<MovieDto>>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int
    ): Response<TmdbResponse<MovieDto>>
}
