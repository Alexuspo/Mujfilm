package alexus.studio.mujfilm.data.remote

import alexus.studio.mujfilm.data.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers

interface CsfdApi {
    companion object {
        const val BASE_URL = "https://www.csfd.cz/api/"
    }

    @Headers("Accept: application/json")
    @GET("films/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @Headers("Accept: application/json")
    @GET("search/suggest")
    suspend fun searchMovies(
        @Query("q") query: String
    ): Response<MoviesResponse>
} 