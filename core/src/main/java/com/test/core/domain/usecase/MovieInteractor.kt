package com.test.core.domain.usecase

import com.test.core.data.Resource
import com.test.core.domain.model.GenreModel
import com.test.core.domain.model.MovieModel
import com.test.core.domain.model.ReviewMovieModel
import com.test.core.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieInteractor @Inject constructor(private val movieRepository: IMovieRepository):
    MovieUseCase {

    override fun getMovies(type:String, page:Int): Flow<Resource<List<MovieModel>>> = movieRepository.getMovies(type, page)
    override fun getReviewsMovieByMovieId(movieId: Int): Flow<Resource<List<ReviewMovieModel>>> = movieRepository.getReviewsMovieByMovieId(movieId)
    override fun getGenresMovie(): Flow<Resource<List<GenreModel>>>  = movieRepository.getGenresMovie()
    override fun getGenreMovieId(genreIds: List<Int>): Flow<Resource<List<GenreModel>>>  = movieRepository.getGenreMovieId(genreIds)
    override fun getFavoriteMovies(): Flow<List<MovieModel>>  = movieRepository.getFavoriteMovies()
    override fun setFavoriteMovie(movie: MovieModel, state: Boolean)  = movieRepository.setFavoriteMovie(movie,state)
}