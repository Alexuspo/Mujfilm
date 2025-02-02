package alexus.studio.mujfilm.data

import android.content.Context
import android.util.Log
import alexus.studio.mujfilm.data.model.Movie
import alexus.studio.mujfilm.data.remote.TmdbApi
import alexus.studio.mujfilm.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

sealed class MovieError : Exception() {
    object NoInternet : MovieError()
    object ApiNotReachable : MovieError()
    object Timeout : MovieError()
    class ApiError(val code: Int) : MovieError()
    class UnknownError(val originalError: Throwable) : MovieError()
}

class MovieRepository(private val context: Context) {
    private val api: TmdbApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(TmdbApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }

    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            if (!NetworkUtil.isOnline(context)) {
                Log.e("MovieRepository", "No internet connection")
                return@withContext Result.failure(MovieError.NoInternet)
            }

            if (!NetworkUtil.canReachTmdbApi()) {
                Log.e("MovieRepository", "Cannot reach TMDB API")
                return@withContext Result.failure(MovieError.ApiNotReachable)
            }

            val response = api.getPopularMovies(page = page)
            Log.d("MovieRepository", "Successfully fetched ${response.results.size} movies")
            Result.success(response.results)
            
        } catch (e: Exception) {
            val error = when (e) {
                is SocketTimeoutException -> MovieError.Timeout
                else -> MovieError.UnknownError(e)
            }
            Log.e("MovieRepository", "Error fetching movies", e)
            Result.failure(error)
        }
    }
}
