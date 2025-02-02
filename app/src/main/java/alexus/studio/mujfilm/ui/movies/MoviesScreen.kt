
package alexus.studio.mujfilm.ui.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import alexus.studio.mujfilm.ui.components.ErrorScreen

@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MoviesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is MoviesUiState.Loading -> LoadingIndicator()
        is MoviesUiState.Empty -> EmptyState()
        is MoviesUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = { viewModel.loadMovies() }
        )
        is MoviesUiState.Success -> MoviesGrid(movies = state.movies)
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