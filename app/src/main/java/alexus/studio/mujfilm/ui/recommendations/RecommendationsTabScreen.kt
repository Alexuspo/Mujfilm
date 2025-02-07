package alexus.studio.mujfilm.ui.recommendations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.ui.movies.MoviesGrid
import alexus.studio.mujfilm.data.model.Movie  // Přidán import pro Movie
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)  // Přidána anotace
@Composable
fun RecommendationsTabScreen(viewModel: MovieViewModel) {
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        AnimatedContent(
            targetState = Triple(recommendations.isEmpty(), favoriteMovies.isEmpty(), recommendations),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with
                fadeOut(animationSpec = tween(300))
            },
            label = "recommendations_animation",  // Přidán label
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { (noRecommendations, noFavorites, currentRecommendations) ->
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "✨ Filmy pro vás",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                when {
                    noFavorites -> EmptyFavoritesMessage()
                    noRecommendations -> LoadingIndicator()
                    else -> RecommendationsList(
                        recommendations = currentRecommendations,
                        favoriteMovies = favoriteMovies,
                        onMovieClick = { movie ->
                            viewModel.toggleFavorite(movie)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (movie in favoriteMovies) {
                                        "💔 ${movie.title} odebrán z oblíbených"
                                    } else {
                                        "✨ ${movie.title} přidán do oblíbených"
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Přidejte si nějaké filmy do oblíbených\npro získání personalizovaných doporučení",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RecommendationsList(
    recommendations: List<Movie>,
    favoriteMovies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    MoviesGrid(
        movies = recommendations,
        onMovieClick = onMovieClick,
        favoriteMovies = favoriteMovies,
        modifier = Modifier.fillMaxSize()
    )
}
