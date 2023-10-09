package com.test.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.test.core.data.source.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies where type=:type")
    fun getMoviesByType(type:String): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movieList: List<MovieEntity>)

    @Query("SELECT * FROM movies where isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Update
    fun updateFavoriteMovie(movie: MovieEntity)

}