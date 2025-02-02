package alexus.studio.mujfilm.repository

import alexus.studio.mujfilm.api.CsfdService
import alexus.studio.mujfilm.data.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieRepository {
    private val csfdApi: CsfdService = Retrofit.Builder()
        .baseUrl(CsfdService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CsfdService::class.java)

    suspend fun searchMovies(query: String): MovieResponse {
        return csfdApi.searchMovies(query)
    }

    suspend fun getMovieDetails(id: Int): MovieResponse {
        return csfdApi.getMovieDetails(id)
    }

    suspend fun getPopularMovies(): MovieResponse {
        return csfdApi.getPopularMovies()
    }
}
