package alexus.studio.mujfilm.recommendation

import alexus.studio.mujfilm.data.model.Movie

class RecommendationEngine {
    data class UserPreferences(
        val favoriteGenres: Set<Int>,
        val averageRating: Double
    )

    fun analyzePreferences(favoriteMovies: List<Movie>): UserPreferences {
        if (favoriteMovies.isEmpty()) {
            return UserPreferences(emptySet(), 0.0)
        }

        val genreFrequency = mutableMapOf<Int, Int>()
        var totalRating = 0.0
        var ratedMoviesCount = 0

        favoriteMovies.forEach { movie ->
            movie.genreIds?.forEach { genreId ->
                genreFrequency[genreId] = (genreFrequency[genreId] ?: 0) + 1
            }
            movie.voteAverage?.let { rating ->
                totalRating += rating
                ratedMoviesCount++
            }
        }

        val favoriteGenres = genreFrequency.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
            .toSet()

        val averageRating = if (ratedMoviesCount > 0) totalRating / ratedMoviesCount else 0.0

        return UserPreferences(favoriteGenres, averageRating)
    }

    fun getRecommendations(allMovies: List<Movie>, preferences: UserPreferences): List<Movie> {
        return allMovies
            .filter { movie -> 
                val hasCommonGenres = movie.genreIds
                    ?.any { it in preferences.favoriteGenres }
                    ?: false
                val hasGoodRating = (movie.voteAverage ?: 0.0) >= preferences.averageRating - 1
                hasCommonGenres && hasGoodRating
            }
            .sortedWith(compareBy(
                { -calculateRelevanceScore(it, preferences) },
                { -(it.voteAverage ?: 0.0) }
            ))
            .take(10)
    }

    private fun calculateRelevanceScore(movie: Movie, preferences: UserPreferences): Double {
        val genreMatchCount = movie.genreIds
            ?.count { it in preferences.favoriteGenres }
            ?: 0
        val ratingBonus = if ((movie.voteAverage ?: 0.0) >= preferences.averageRating) 0.5 else 0.0
        
        return genreMatchCount.toDouble() + ratingBonus
    }
}