package alexus.studio.mujfilm.ui.movies

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import alexus.studio.mujfilm.data.MovieRepository
import alexus.studio.mujfilm.data.MovieError
import alexus.studio.mujfilm.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MoviesUiState.Loading
            
            repository.getPopularMovies().fold(
                onSuccess = { movies ->
                    _uiState.value = if (movies.isEmpty()) {
                        MoviesUiState.Empty
                    } else {
                        MoviesUiState.Success(movies)
                    }
                },
                onFailure = { error ->
                    val message = when (error) {
                        is MovieError.NoInternet -> 
                            "Není k dispozici připojení k internetu. Zkuste to prosím znovu."
                        is MovieError.ApiNotReachable -> 
                            "Nepodařilo se připojit k serveru. Zkuste to prosím později."
                        is MovieError.Timeout -> 
                            "Vypršel časový limit připojení. Zkuste to prosím znovu."
                        is MovieError.ApiError -> 
                            "Došlo k chybě při načítání dat (${error.code}). Zkuste to prosím znovu."
                        else -> "Došlo k neočekávané chybě. Zkuste to prosím znovu."
                    }
                    Log.e("MoviesViewModel", "Error loading movies", error)
                    _uiState.value = MoviesUiState.Error(message)
                }
            )
        }
    }
}

sealed class MoviesUiState {
    data object Loading : MoviesUiState()
    data object Empty : MoviesUiState()
    data class Success(val movies: List<Movie>) : MoviesUiState()
    data class Error(val message: String) : MoviesUiState()
}
