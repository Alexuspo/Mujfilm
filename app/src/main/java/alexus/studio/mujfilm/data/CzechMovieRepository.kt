package alexus.studio.mujfilm.data

import android.content.Context
import android.util.Log
import alexus.studio.mujfilm.data.model.CzechMovie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CzechMovieRepository(private val context: Context) {
    private val gson = Gson()

    fun getMovies(): Flow<List<CzechMovie>> = flow {
        try {
            val jsonString = context.assets.open("czech_movies.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<MoviesResponse>() {}.type
            val response: MoviesResponse = gson.fromJson(jsonString, type)
            emit(response.movies)
        } catch (e: Exception) {
            Log.e("CzechMovieRepository", "Error loading movies", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun searchMovies(query: String): Flow<List<CzechMovie>> = flow {
        try {
            val allMovies = getMoviesFromJson()
            val filteredMovies = allMovies.filter { movie ->
                movie.title.contains(query, ignoreCase = true) ||
                movie.director.contains(query, ignoreCase = true)
            }
            emit(filteredMovies)
        } catch (e: Exception) {
            Log.e("CzechMovieRepository", "Error searching movies", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    private fun getMoviesFromJson(): List<CzechMovie> {
        val jsonString = context.assets.open("czech_movies.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<MoviesResponse>() {}.type
        return gson.fromJson<MoviesResponse>(jsonString, type).movies
    }

    private data class MoviesResponse(val movies: List<CzechMovie>)
}
