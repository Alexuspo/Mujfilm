package alexus.studio.mujfilm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "poster_path") @SerializedName("poster_path") val posterPath: String?,
    @ColumnInfo(name = "release_date") @SerializedName("release_date") val releaseDate: String?,
    val overview: String?,
    @ColumnInfo(name = "vote_average") @SerializedName("vote_average") val voteAverage: Double?,
    @ColumnInfo(name = "genre_ids") val genreIds: List<Int>? = emptyList(),
    @ColumnInfo(name = "is_selected") var isSelected: Boolean = false
)

data class Genre(
    val id: Int,
    val name: String
)
