package alexus.studio.mujfilm.data.model

data class CsfdMovie(
    val id: String,
    val title: String,
    val originalTitle: String?,
    val year: Int,
    val director: String,
    val description: String,
    val posterUrl: String,
    val rating: Int,
    val genres: List<String>,
    val csfdUrl: String,
    val actors: List<String>,
    val country: String,
    val isFavorite: Boolean = false
)
