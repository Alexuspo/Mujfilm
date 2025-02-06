package alexus.studio.mujfilm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import alexus.studio.mujfilm.ui.theme.MujFilmTheme
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.ui.components.ErrorScreen
import alexus.studio.mujfilm.ui.movies.MoviesGrid
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import alexus.studio.mujfilm.viewmodel.MoviesUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MovieViewModel = viewModel()
            App(viewModel)
        }
    }
}

@Composable
fun App(viewModel: MovieViewModel) {
    MujFilmTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MovieViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Domů") },
                    label = { Text("Domů") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Hledat") },
                    label = { Text("Hledat") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Oblíbené") },
                    label = { Text("Oblíbené") }
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel)
                1 -> SearchScreen(viewModel)
                2 -> FavoritesScreen(viewModel)
                else -> HomeScreen(viewModel) // Default case
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MovieViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.popular_movies),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        when (val state = uiState) {
            MoviesUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            MoviesUiState.Empty -> {
                Text(
                    text = "Nebyly nalezeny žádné filmy",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
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
                    onMovieClick = { movie -> viewModel.toggleFavorite(movie) }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                if (it.length >= 2) {
                    viewModel.searchMovies(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, stringResource(R.string.search)) }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        } else {
            when (val state = uiState) {
                MoviesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                MoviesUiState.Empty -> {
                    Text(
                        text = "Nebyly nalezeny žádné filmy",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                is MoviesUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.searchMovies(searchQuery) }
                    )
                }
                is MoviesUiState.Success -> {
                    MoviesGrid(
                        movies = state.movies,
                        onMovieClick = { movie -> viewModel.toggleFavorite(movie) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text(
                        text = "Neznámý stav",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: MovieViewModel) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    } else if (favoriteMovies.isEmpty()) {
        Text(
            text = "Zatím nemáte žádné oblíbené filmy",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    } else {
        MoviesGrid(            movies = favoriteMovies,            onMovieClick = { movie -> viewModel.toggleFavorite(movie) },            modifier = Modifier.fillMaxSize()
        )
    }}