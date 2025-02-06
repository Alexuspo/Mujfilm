package alexus.studio.mujfilm.ui.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import alexus.studio.mujfilm.ui.components.ErrorScreen
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.viewmodel.MoviesUiState
import alexus.studio.mujfilm.data.model.Movie
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesScreen(
    viewModel: MovieViewModel = viewModel(),
    onMovieClick: (Movie) -> Unit
) {
    val uiState by viewModel.homeState.collectAsStateWithLifecycle()
    
    when (val state = uiState) {
        MoviesUiState.Loading -> LoadingIndicator()
        MoviesUiState.Empty -> EmptyState()
        is MoviesUiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = { viewModel.loadPopularMovies() }
            )
        }
        is MoviesUiState.Success -> {
            MoviesGrid(
                movies = state.movies,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Nebyly nalezeny žádné filmy.")
    }
}