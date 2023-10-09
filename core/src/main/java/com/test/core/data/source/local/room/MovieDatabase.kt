package com.test.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.core.data.source.local.entity.GenreEntity
import com.test.core.data.source.local.entity.MovieEntity
import com.test.core.data.source.local.entity.ReviewMovieEntity

@Database(entities = [
    MovieEntity::class,
    ReviewMovieEntity::class,
    GenreEntity::class
                     ], version = 2, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun reviewMovieDao(): ReviewMovieDao
    abstract fun genreMovieDao(): GenreDao
}