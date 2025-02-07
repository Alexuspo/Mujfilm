package alexus.studio.mujfilm.ui.recommendations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp  // Add missing dp import
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.ui.movies.MoviesGrid
import alexus.studio.mujfilm.R

@Composable
fun RecommendationsScreen(viewModel: MovieViewModel) {
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.recommended_for_you),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        MoviesGrid(
            movies = recommendations,
            onMovieClick = { viewModel.toggleFavorite(it) },
            favoriteMovies = favoriteMovies,
            modifier = Modifier.fillMaxSize()
        )
    }
}
