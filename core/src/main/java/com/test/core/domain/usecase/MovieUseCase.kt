package com.test.core.domain.usecase

import com.test.core.data.Resource
import com.test.core.domain.model.GenreModel
import com.test.core.domain.model.MovieModel
import com.test.core.domain.model.ReviewMovieModel
import kotlinx.coroutines.flow.Flow

interface MovieUseCase {
    fun getMovies(type:String, page:Int): Flow<Resource<List<MovieModel>>>
    fun getReviewsMovieByMovieId(movieId:Int): Flow<Resource<List<ReviewMovieModel>>>
    fun getGenresMovie():Flow<Resource<List<GenreModel>>>
    fun getGenreMovieId(genreIds:List<Int>):Flow<Resource<List<GenreModel>>>
    fun getFavoriteMovies(): Flow<List<MovieModel>>
    fun setFavoriteMovie(movie: MovieModel, state: Boolean)
}