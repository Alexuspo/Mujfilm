package alexus.studio.mujfilm.ui.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment  // Added missing import
import androidx.lifecycle.viewmodel.compose.viewModel
import alexus.studio.mujfilm.ui.components.ErrorScreen
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.ui.state.MoviesUiState
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesScreen(
    viewModel: MovieViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    
    when (val state = uiState) {
        is MoviesUiState.Loading -> {
            LoadingIndicator()
        }
        is MoviesUiState.Empty -> {
            EmptyState()
        }
        is MoviesUiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = { viewModel.loadMovies() }
            )
        }
        is MoviesUiState.Success -> {
            MoviesGrid(
                movies = state.movies,
                onMovieClick = { viewModel.toggleFavorite(it) },
                modifier = modifier
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
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Nebyly nalezeny žádné filmy",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}