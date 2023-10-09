package com.test.core.data.source.local

import com.test.core.data.source.local.entity.GenreEntity
import com.test.core.data.source.local.entity.MovieEntity
import com.test.core.data.source.local.entity.ReviewMovieEntity
import com.test.core.data.source.local.room.GenreDao
import com.test.core.data.source.local.room.MovieDao
import com.test.core.data.source.local.room.ReviewMovieDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val reviewMovieDao: ReviewMovieDao,
    private val genreMovieDao: GenreDao
) {
//    MOVIE
    fun getMoviesByType(type:String):Flow<List<MovieEntity>> = movieDao.getMoviesByType(type)
    suspend fun insertMovies(movieList: List<MovieEntity>) = movieDao.insertMovies(movieList)
    fun getFavoriteMovies(): Flow<List<MovieEntity>> = movieDao.getFavoriteMovies()
    fun setFavoriteMovie(movie: MovieEntity, newState: Boolean) {
        movie.isFavorite = newState
        movieDao.updateFavoriteMovie(movie)
    }

//    REVIEW MOVIE
    fun getReviewsMovieByMovieId(movieId:Int):Flow<List<ReviewMovieEntity>> = reviewMovieDao.getMoviesByMovieId(movieId)
    suspend fun insertReviewsMovie(reviewList: List<ReviewMovieEntity>) = reviewMovieDao.insertReviewsMovie(reviewList)

//   GENRE MOVIE
    fun getGenreMovieById(genreIds:List<Int>):Flow<List<GenreEntity>> = genreMovieDao.getGenreMovieById(genreIds)
    fun getGenresMovie():Flow<List<GenreEntity>> = genreMovieDao.getGenresMovie()
    suspend fun insertGenres(genreList: List<GenreEntity>) = genreMovieDao.insertGenres(genreList)
}