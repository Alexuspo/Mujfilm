package alexus.studio.mujfilm.data.model

data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val director: String,
    val description: String,
    val posterUrl: String,
    val rating: Int,
    val genres: List<String> = emptyList(),
    val isFavorite: Boolean = false
)
