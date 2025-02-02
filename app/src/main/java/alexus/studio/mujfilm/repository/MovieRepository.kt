package alexus.studio.mujfilm.repository

import alexus.studio.mujfilm.api.TMDbService
import alexus.studio.mujfilm.data.Movie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

class MovieRepository {
    private val api = Retrofit.Builder()
        .baseUrl(TMDbService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TMDbService::class.java)

    suspend fun getPopularMovies(): List<Movie> {
        return try {
            api.getPopularMovies().results
        } catch (e: UnknownHostException) {
            throw Exception("Není k dispozici připojení k internetu")
        } catch (e: Exception) {
            throw Exception("Nepodařilo se načíst filmy: ${e.localizedMessage}")
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return try {
            api.searchMovies(query = query).results
        } catch (e: UnknownHostException) {
            throw Exception("Není k dispozici připojení k internetu")
        } catch (e: Exception) {
            throw Exception("Nepodařilo se najít filmy: ${e.localizedMessage}")
        }
    }
}
