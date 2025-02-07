package alexus.studio.mujfilm.data.model

import com.google.gson.annotations.SerializedName

data class TmdbResponse<T>(
    val page: Int,
    val results: List<T>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val poster_path: String?,
    @SerializedName("vote_average")
    val vote_average: Double,
    @SerializedName("release_date")
    val release_date: String?,
    @SerializedName("genre_ids")
    val genre_ids: List<Int>?
)
