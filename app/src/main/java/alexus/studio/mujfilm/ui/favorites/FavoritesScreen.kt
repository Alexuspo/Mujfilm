package alexus.studio.mujfilm.ui.favorites

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import alexus.studio.mujfilm.ui.movies.MoviesGrid
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: MovieViewModel) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val selectedMovies by viewModel.selectedMovies.collectAsStateWithLifecycle()
    var isSelectionMode by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSelectionMode) "Vybráno: ${selectedMovies.size}" else "Oblíbené filmy") },
                actions = {
                    if (favoriteMovies.isNotEmpty()) {
                        if (isSelectionMode) {
                            IconButton(onClick = { viewModel.selectAllMovies(favoriteMovies) }) {
                                Icon(Icons.Default.Done, "Vybrat vše")
                            }
                            IconButton(onClick = {
                                viewModel.deleteSelectedMovies()
                                isSelectionMode = false
                            }) {
                                Icon(Icons.Default.Delete, "Smazat vybrané")
                            }
                            IconButton(onClick = {
                                viewModel.clearSelection()
                                isSelectionMode = false
                            }) {
                                Icon(Icons.Default.Close, "Zrušit výběr")
                            }
                        } else {
                            IconButton(onClick = { isSelectionMode = true }) {
                                Icon(Icons.Default.Edit, "Upravit")
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding -> 
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            if (favoriteMovies.isEmpty()) {
                Text(
                    text = "Zatím nemáte žádné oblíbené filmy",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            } else {
                MoviesGrid(
                    movies = favoriteMovies,
                    onMovieClick = { movie ->
                        if (isSelectionMode) {
                            viewModel.toggleMovieSelection(movie)
                        } else {
                            viewModel.toggleFavorite(movie)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    favoriteMovies = favoriteMovies,
                    selectedMovies = if (isSelectionMode) selectedMovies else emptySet(),
                    isSelectable = isSelectionMode
                )
            }
        }
    }
}
