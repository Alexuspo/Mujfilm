package alexus.studio.mujfilm.data

import androidx.room.*

@Entity(
    tableName = "movies",
    indices = [Index(value = ["id"], unique = true)]
)
data class Movie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String?,  // Změněno na nullable
    val poster_path: String?,
    val release_date: String?,  // Změněno na nullable
    val vote_average: Double,
    @ColumnInfo(defaultValue = "0")
    val isFavorite: Boolean = false
) {
    // Zajistíme, že žádná hodnota nebude null při ukládání do databáze
    fun toFavorite() = copy(
        overview = overview ?: "",
        release_date = release_date ?: "",
        poster_path = poster_path ?: ""
    )
}

data class MovieResponse(
    val page: Int,
    val results: List<Movie>
)
