package alexus.studio.mujfilm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import alexus.studio.mujfilm.ui.theme.MujFilmTheme
import alexus.studio.mujfilm.viewmodel.MovieViewModel
import alexus.studio.mujfilm.ui.components.MovieCard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign  // Add this import
import alexus.studio.mujfilm.ui.components.ErrorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MujFilmTheme {
                val viewModel: MovieViewModel = viewModel()
                MainScreen(viewModel)
            }
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, "Domů") },
                    label = { Text("Domů") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.Search, "Hledat") },
                    label = { Text("Hledat") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Favorite, "Oblíbené") },
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
                2 -> FavoritesScreen(viewModel)  // Přidáno předání viewModel
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MovieViewModel) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.popular_movies),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            error != null -> {
                ErrorScreen(
                    message = error ?: stringResource(R.string.error_loading_movies),
                    onRetry = { viewModel.loadPopularMovies() }
                )
            }
            else -> {
                LazyColumn {
                    items(movies) { movie ->
                        MovieCard(
                            movie = movie,
                            onFavoriteClick = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MovieViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val movies by viewModel.movies.collectAsStateWithLifecycle()
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
            LazyColumn {
                items(movies) { movie ->
                    MovieCard(
                        movie = movie,
                        onFavoriteClick = { viewModel.toggleFavorite(it) }  // Přidáno předání callbacku
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

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (favoriteMovies.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Zatím nemáte žádné oblíbené filmy",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(
                    items = favoriteMovies,
                    key = { it.id }
                ) { movie ->
                    MovieCard(
                        movie = movie,
                        onFavoriteClick = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}