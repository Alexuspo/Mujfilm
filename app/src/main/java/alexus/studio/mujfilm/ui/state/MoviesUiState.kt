package alexus.studio.mujfilm.ui.state

import alexus.studio.mujfilm.data.model.Movie

sealed class MoviesUiState {
    data object Loading : MoviesUiState()
    data object Empty : MoviesUiState()
    data class Error(val message: String) : MoviesUiState()
    data class Success(val movies: List<Movie>) : MoviesUiState()
}