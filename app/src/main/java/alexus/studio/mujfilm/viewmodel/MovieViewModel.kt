package alexus.studio.mujfilm.viewmodel

import alexus.studio.mujfilm.data.MovieRepository
import alexus.studio.mujfilm.data.model.Movie
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import alexus.studio.mujfilm.ui.state.MoviesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import alexus.studio.mujfilm.data.remote.ApiConfig

class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val apiKey = ApiConfig.API_KEY
    
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies.asStateFlow()

    private val _selectedMovies = MutableStateFlow<Set<Movie>>(emptySet())
    val selectedMovies: StateFlow<Set<Movie>> = _selectedMovies.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Movie>>(emptyList())
    val recommendations: StateFlow<List<Movie>> = _recommendations.asStateFlow()

    init {
        loadMovies()
        loadRecommendations()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            repository.getPopularMovies()
                .fold(
                    onSuccess = { movies ->
                        _uiState.value = when {
                            movies.isEmpty() -> MoviesUiState.Empty
                            else -> MoviesUiState.Success(movies)
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = MoviesUiState.Error(error.message ?: "Unknown error")
                    }
                )
        }
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            try {
                println("Loading recommendations for ${_favoriteMovies.value.size} favorite movies")
                repository.getRecommendations(_favoriteMovies.value)
                    .fold(
                        onSuccess = { movies -> 
                            println("Got ${movies.size} recommendations")
                            _recommendations.value = movies 
                        },
                        onFailure = { error -> 
                            println("Failed to get recommendations: ${error.message}")
                        }
                    )
            } catch (e: Exception) {
                println("Error in loadRecommendations: ${e.message}")
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchMovies(query)
                    .fold(
                        onSuccess = { movies ->
                            _uiState.value = when {
                                movies.isEmpty() -> MoviesUiState.Empty
                                else -> MoviesUiState.Success(movies)
                            }
                        },
                        onFailure = { error ->
                            _uiState.value = MoviesUiState.Error(error.message ?: "Unknown error")
                        }
                    )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = _favoriteMovies.value.toMutableList()
        val wasAdded = !currentFavorites.any { it.id == movie.id }
        
        if (wasAdded) {
            currentFavorites.add(movie)
        } else {
            currentFavorites.removeAll { it.id == movie.id }
        }
        
        _favoriteMovies.value = currentFavorites
        println("Favorites updated, now has ${currentFavorites.size} movies")
        loadRecommendations()
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
        // Aktualizace doporučení po smazání
        loadRecommendations()
    }

    // Přidáme novou metodu pro hromadné mazání
    fun deleteFavoriteMovies(moviesToDelete: List<Movie>) {
        val currentFavorites = _favoriteMovies.value.toMutableList()
        currentFavorites.removeAll(moviesToDelete.toSet())
        _favoriteMovies.value = currentFavorites
        // Aktualizace doporučení po smazání
        loadRecommendations()
    }

    fun selectAllMovies(movies: List<Movie>) {
        _selectedMovies.value = movies.toSet()
    }

    fun clearSelection() {
        _selectedMovies.value = emptySet()
    }
}