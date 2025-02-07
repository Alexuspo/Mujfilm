package alexus.studio.mujfilm

import android.app.Application
import alexus.studio.mujfilm.data.MovieRepository

class MujFilmApplication : Application() {
    lateinit var repository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = MovieRepository(this)
    }
}
