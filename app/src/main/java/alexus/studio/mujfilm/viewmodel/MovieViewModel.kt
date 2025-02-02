package alexus.studio.mujfilm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import alexus.studio.mujfilm.data.Movie
import alexus.studio.mujfilm.db.MovieDatabase
import alexus.studio.mujfilm.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MovieDatabase.getDatabase(application)
    private val movieDao = database.movieDao()
    private val repository = MovieRepository()
    
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadPopularMovies()
            // Sledujeme oblíbené filmy
            try {
                movieDao.getFavoriteMovies().collect { favorites ->
                    _favoriteMovies.value = favorites
                    // Aktualizujeme stav oblíbených v hlavním seznamu
                    updateMainListFavorites(favorites)
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error collecting favorites", e)
            }
        }
    }

    private fun updateMainListFavorites(favorites: List<Movie>) {
        val favoriteIds = favorites.map { it.id }.toSet()
        val currentMovies = _movies.value.map { movie ->
            movie.copy(isFavorite = favoriteIds.contains(movie.id))
        }
        _movies.value = currentMovies
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movies.value = repository.getPopularMovies()
                updateMoviesList()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading popular movies", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movies.value = repository.searchMovies(query)
                updateMoviesList()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error searching movies", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
                if (updatedMovie.isFavorite) {
                    movieDao.insertMovie(updatedMovie)
                } else {
                    movieDao.deleteMovie(updatedMovie)
                }
                
                // Okamžitě aktualizujeme UI
                val currentMovies = _movies.value.toMutableList()
                val index = currentMovies.indexOfFirst { it.id == movie.id }
                if (index != -1) {
                    currentMovies[index] = updatedMovie
                    _movies.value = currentMovies
                }

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error toggling favorite", e)
            }
        }
    }

    private suspend fun updateMoviesList() {
        withContext(Dispatchers.IO) {
            try {
                val currentMovies = _movies.value
                val updatedMovies = currentMovies.map { movie ->
                    val isFavorite = movieDao.isMovieFavorite(movie.id)
                    movie.copy(isFavorite = isFavorite)
                }
                withContext(Dispatchers.Main) {
                    _movies.value = updatedMovies
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error updating movies list", e)
            }
        }
    }
}