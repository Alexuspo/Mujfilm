package alexus.studio.mujfilm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import alexus.studio.mujfilm.data.remote.MovieApiService
import alexus.studio.mujfilm.model.Movie
import alexus.studio.mujfilm.ui.state.MoviesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val apiService: MovieApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    private val _selectedMovies = MutableStateFlow<Set<Movie>>(emptySet())
    val selectedMovies: StateFlow<Set<Movie>> = _selectedMovies

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            try {
                val movies = apiService.getPopularMoviess()
                _uiState.value = if (movies.isEmpty()) {
                    MoviesUiState.Empty
                } else {
                    MoviesUiState.Success(movies)
                }
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val movies = apiService.searchMovies(query)
                _uiState.value = if (movies.isEmpty()) {
                    MoviesUiState.Empty
                } else {
                    MoviesUiState.Success(movies)
                }
            } catch (e: Exception) {
                _uiState.value = MoviesUiState.Error(e.message ?: "Unknown error")
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

    fun toggleMovieSelection(movie: Movie) {
        val currentSelection = _selectedMovies.value.toMutableSet()
        if (currentSelection.contains(movie)) {
            currentSelection.remove(movie)
        } else {
            currentSelection.add(movie)
        }
        _selectedMovies.value = currentSelection
    }

    fun deleteSelectedMovies() {
        val currentFavorites = _favoriteMovies.value.toMutableList()
        currentFavorites.removeAll(_selectedMovies.value)
        _favoriteMovies.value = currentFavorites
        _selectedMovies.value = emptySet()
    }
}