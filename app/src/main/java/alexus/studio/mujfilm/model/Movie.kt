package alexus.studio.mujfilm.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val genreIds: List<Int> = emptyList()
)
