package alexus.studio.mujfilm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

import alexus.studio.mujfilm.ui.theme.MujFilmTheme
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.viewmodel.MovieViewModelFactory
import alexus.studio.mujfilm.ui.components.ErrorScreen
import alexus.studio.mujfilm.ui.movies.MoviesGrid
import alexus.studio.mujfilm.ui.state.MoviesUiState
import alexus.studio.mujfilm.ui.recommendations.RecommendationsScreen
import alexus.studio.mujfilm.ui.recommendations.RecommendationsSection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as MujFilmApplication).repository
        val viewModel = ViewModelProvider(
            this,
            MovieViewModelFactory(repository)
        )[MovieViewModel::class.java]
        
        setContent {
            MujFilmTheme {
                App(viewModel)
            }
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
                    icon = { Icon(Icons.Filled.Star, contentDescription = stringResource(R.string.recommendations)) },
                    label = { Text(stringResource(R.string.recommendations)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Hledat") },
                    label = { Text("Hledat") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
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
                1 -> RecommendationsScreen(viewModel)
                2 -> SearchScreen(viewModel)
                3 -> FavoritesScreen(viewModel)
                else -> HomeScreen(viewModel) // Default case
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MovieViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    val addedToFavoritesMessage = stringResource(R.string.added_to_favorites)
    val removedFromFavoritesMessage = stringResource(R.string.removed_from_favorites)
    val recommendedForYouTitle = stringResource(R.string.recommended_for_you)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val wasAdded = remember(data.visuals.message) {
                    data.visuals.message.contains("přidán")
                }
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateContentSize(),
                    containerColor = if (wasAdded) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.errorContainer,
                    contentColor = if (wasAdded)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text(
                                text = if (wasAdded) "Zobrazit" else "Zpět",
                                color = if (wasAdded)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            // Sekce doporučených filmů
            RecommendationsSection(
                title = recommendedForYouTitle,
                movies = recommendations,
                onMovieClick = { movie -> 
                    val wasAdded = !favoriteMovies.any { it.id == movie.id }
                    viewModel.toggleFavorite(movie)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (wasAdded) {
                                String.format(addedToFavoritesMessage, movie.title)
                            } else {
                                String.format(removedFromFavoritesMessage, movie.title)
                            }
                        )
                    }
                }
            )

            // Populární filmy
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
                        onMovieClick = { movie -> 
                            val wasAdded = !viewModel.favoriteMovies.value.any { it.id == movie.id }
                            viewModel.toggleFavorite(movie)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (wasAdded) {
                                        "✨ ${movie.title} přidán do oblíbených"
                                    } else {
                                        "💔 ${movie.title} odebrán z oblíbených"
                                    },
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
                                )
                            }
                        },
                        favoriteMovies = viewModel.favoriteMovies.collectAsStateWithLifecycle().value
                    )
                }
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
                is MoviesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                is MoviesUiState.Empty -> {
                    Text(
                        text = "Nebyly nalezeny žádné filmy",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                is MoviesUiState.Error -> {
                    val errorMessage = state.message  // Lokální proměnná pro smart cast
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { viewModel.searchMovies(searchQuery) }
                    )
                }
                is MoviesUiState.Success -> {
                    val movies = state.movies  // Lokální proměnná pro smart cast
                    MoviesGrid(
                        movies = movies,
                        onMovieClick = { viewModel.toggleFavorite(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: MovieViewModel) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val selectedMovies by viewModel.selectedMovies.collectAsStateWithLifecycle()
    
    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedMovies.isNotEmpty()) {
            TopAppBar(
                title = { Text("Vybráno: ${selectedMovies.size}") },
                actions = {
                    IconButton(onClick = { viewModel.deleteSelectedMovies() }) {
                        Icon(Icons.Default.Delete, "Smazat vybrané")
                    }
                }
            )
        }
        
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
                onMovieClick = { viewModel.toggleMovieSelection(it) },
                modifier = Modifier.fillMaxSize(),
                favoriteMovies = favoriteMovies
            )
        }
    }
}