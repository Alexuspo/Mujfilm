package alexus.studio.mujfilm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import alexus.studio.mujfilm.data.MovieRepository
import alexus.studio.mujfilm.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)
    
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = _favoriteMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getPopularMovies().fold(
                    onSuccess = { movies ->
                        _uiState.value = if (movies.isEmpty()) {
                            MoviesUiState.Empty
                        } else {
                            MoviesUiState.Success(movies)
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = MoviesUiState.Error(error.message ?: "Neznámá chyba")
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = _favoriteMovies.value.toMutableList()
        if (currentFavorites.any { it.id == movie.id }) {
            currentFavorites.removeAll { it.id == movie.id }
        } else {
            currentFavorites.add(movie)
        }
        _favoriteMovies.value = currentFavorites
    }
}

sealed class MoviesUiState {
    data object Loading : MoviesUiState()
    data object Empty : MoviesUiState()
    data class Success(val movies: List<Movie>) : MoviesUiState()
    data class Error(val message: String) : MoviesUiState()
}