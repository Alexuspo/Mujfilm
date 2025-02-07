package alexus.studio.mujfilm.di

import alexus.studio.mujfilm.data.remote.ApiConfig
import alexus.studio.mujfilm.data.remote.MovieApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    fun provideMovieApiService(): MovieApiService {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}
