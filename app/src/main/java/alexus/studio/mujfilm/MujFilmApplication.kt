package alexus.studio.mujfilm

import android.app.Application
import alexus.studio.mujfilm.data.MovieRepository
import alexus.studio.mujfilm.db.MovieDatabase

class MujFilmApplication : Application() {
    val database: MovieDatabase by lazy { MovieDatabase.getDatabase(this) }
    lateinit var movieRepository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        movieRepository = MovieRepository(this)
    }
}
