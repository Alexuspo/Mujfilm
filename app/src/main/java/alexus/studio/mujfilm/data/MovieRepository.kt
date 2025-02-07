package alexus.studio.mujfilm.data

import android.content.Context
import alexus.studio.mujfilm.data.model.Movie
import alexus.studio.mujfilm.data.model.MovieDto
import alexus.studio.mujfilm.data.model.TmdbResponse
import alexus.studio.mujfilm.data.remote.TmdbApiService
import alexus.studio.mujfilm.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

sealed class MovieError : Exception() {
    object NoInternet : MovieError()
    object ApiNotReachable : MovieError()
    object Timeout : MovieError()
    class ApiError(val code: Int) : MovieError()
    class UnknownError(val originalError: Throwable) : MovieError()
}

class MovieRepository(private val context: Context) {
    private val api: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original.url.newBuilder()
                        .addQueryParameter("api_key", ApiConfig.API_KEY)
                        .build()
                    chain.proceed(original.newBuilder().url(url).build())
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }

    private fun mapDtoToMovie(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            posterPath = dto.poster_path?.let { "${TmdbApiService.IMAGE_BASE_URL}$it" },
            releaseDate = dto.release_date ?: "",
            overview = dto.overview,
            voteAverage = dto.vote_average,
            genreIds = dto.genre_ids ?: emptyList()
        )
    }

    suspend fun getPopularMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMovies()
            if (response.isSuccessful && response.body() != null) {
                val movies = response.body()?.results?.map(::mapDtoToMovie) ?: emptyList()
                Result.success(movies)
            } else {
                Result.failure(MovieError.ApiError(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(MovieError.UnknownError(e))
        }
    }

    suspend fun searchMovies(query: String): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMovies(query)
            if (response.isSuccessful && response.body() != null) {
                val movies = response.body()?.results?.map(::mapDtoToMovie) ?: emptyList()
                Result.success(movies)
            } else {
                Result.failure(MovieError.ApiError(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(MovieError.UnknownError(e))
        }
    }

    suspend fun getRecommendations(favoriteMovies: List<Movie>): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            if (favoriteMovies.isEmpty()) {
                return@withContext getPopularMovies()
            }

            val recommendations = mutableSetOf<Movie>()
            
            // Získáme doporučení pro každý oblíbený film
            favoriteMovies.forEach { favorite ->
                try {
                    val response = api.getMovieRecommendations(favorite.id)
                    if (response.isSuccessful && response.body() != null) {
                        val movies = response.body()?.results?.map(::mapDtoToMovie) ?: emptyList()
                        // Přidáme pouze filmy, které ještě nejsou v oblíbených
                        recommendations.addAll(
                            movies.filterNot { movie -> 
                                favoriteMovies.any { it.id == movie.id }
                            }
                        )
                    }
                } catch (e: Exception) {
                    println("Error getting recommendations for movie ${favorite.id}: ${e.message}")
                }
            }

            // Pokud nemáme žádná doporučení, vrátíme populární filmy
            if (recommendations.isEmpty()) {
                return@withContext getPopularMovies()
            }

            Result.success(recommendations.toList())
        } catch (e: Exception) {
            println("Error in getRecommendations: ${e.message}")
            Result.failure(MovieError.UnknownError(e))
        }
    }
}

private data class MoviesResponse(val movies: List<Movie>)
