package alexus.studio.mujfilm.db

import androidx.room.*
import alexus.studio.mujfilm.data.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): Movie?

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun setFavorite(movieId: Int, isFavorite: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :movieId AND isFavorite = 1)")
    suspend fun isMovieFavorite(movieId: Int): Boolean
}
