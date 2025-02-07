package alexus.studio.mujfilm.ui.state

import alexus.studio.mujfilm.data.model.Movie

sealed interface MoviesUiState {
    object Loading : MoviesUiState
    object Empty : MoviesUiState
    data class Success(val movies: List<Movie>) : MoviesUiState
    data class Error(val message: String) : MoviesUiState
}