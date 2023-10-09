package com.test.core.data

import com.test.core.NetworkBoundResource
import com.test.core.data.source.remote.response.ResultsMovieItem
import com.test.core.data.source.remote.response.ResultsReviewItem
import com.test.core.domain.model.MovieModel
import com.test.core.domain.model.ReviewMovieModel
import com.test.core.domain.repository.IMovieRepository
import com.test.core.utils.mapper.MovieDataMapper
import com.test.core.utils.mapper.ReviewMovieDataMapper
import com.test.core.data.source.local.LocalDataSource
import com.test.core.data.source.remote.RemoteDataSource
import com.test.core.data.source.remote.network.ApiResponse
import com.test.core.data.source.remote.response.GenresMovieItem
import com.test.core.domain.model.GenreModel
import com.test.core.utils.AppExecutors
import com.test.core.utils.mapper.GenreMovieDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : IMovieRepository {
    override fun getMovies(type:String, page:Int): Flow<Resource<List<MovieModel>>> =
        object : NetworkBoundResource<List<MovieModel>, List<ResultsMovieItem>>() {
            override fun loadFromDB(): Flow<List<MovieModel>>  {
                return localDataSource.getMoviesByType(type).map { MovieDataMapper.mapEntitiesToDomain(it)}
            }

            override fun shouldFetch(data: List<MovieModel>?): Boolean =
                if (page < 2) {
                    data.isNullOrEmpty()
                } else {
                    true // ganti dengan true jika ingin selalu mengambil data dari internet
                }


            override suspend fun createCall(): Flow<ApiResponse<List<ResultsMovieItem>>> = remoteDataSource.getMovies(type, page)


            override suspend fun saveCallResult(data: List<ResultsMovieItem>) {
                val movieList = MovieDataMapper.mapResponsesToEntities(type,data)
                localDataSource.insertMovies(movieList)
            }
        }.asFlow()

    override fun getReviewsMovieByMovieId(movieId:Int): Flow<Resource<List<ReviewMovieModel>>> =
        object : NetworkBoundResource<List<ReviewMovieModel>, List<ResultsReviewItem>>() {
            override fun loadFromDB(): Flow<List<ReviewMovieModel>>  {
                return localDataSource.getReviewsMovieByMovieId(movieId).map { ReviewMovieDataMapper.mapEntitiesToDomain(it)}
            }

            override fun shouldFetch(data: List<ReviewMovieModel>?): Boolean =
//                data.isNullOrEmpty()
                true // ganti dengan true jika ingin selalu mengambil data dari internet

            override suspend fun createCall(): Flow<ApiResponse<List<ResultsReviewItem>>> = remoteDataSource.getReviewsMovieByMovieId(movieId)


            override suspend fun saveCallResult(data: List<ResultsReviewItem>) {
                val reviewMovieList = ReviewMovieDataMapper.mapResponsesToEntities(movieId,data)
                localDataSource.insertReviewsMovie(reviewMovieList)
            }
        }.asFlow()

    override fun getGenresMovie(): Flow<Resource<List<GenreModel>>> =
        object : NetworkBoundResource<List<GenreModel>, List<GenresMovieItem>>() {
            override fun loadFromDB(): Flow<List<GenreModel>>  {
                return localDataSource.getGenresMovie().map { GenreMovieDataMapper.mapEntitiesToDomain(it)}
            }

            override fun shouldFetch(data: List<GenreModel>?): Boolean =
                data.isNullOrEmpty()
//                true // ganti dengan true jika ingin selalu mengambil data dari internet

            override suspend fun createCall(): Flow<ApiResponse<List<GenresMovieItem>>> = remoteDataSource.getGenresMovieId()


            override suspend fun saveCallResult(data: List<GenresMovieItem>) {
                val genreMovieList = GenreMovieDataMapper.mapResponsesToEntities(data)
                localDataSource.insertGenres(genreMovieList)
            }
        }.asFlow()

    override fun getGenreMovieId(genreIds: List<Int>): Flow<Resource<List<GenreModel>>> =
        object : NetworkBoundResource<List<GenreModel>, List<GenresMovieItem>>() {
        override fun loadFromDB(): Flow<List<GenreModel>>  {
            return localDataSource.getGenreMovieById(genreIds).map { GenreMovieDataMapper.mapEntitiesToDomain(it)}
        }

        override fun shouldFetch(data: List<GenreModel>?): Boolean = false
//                true // ganti dengan true jika ingin selalu mengambil data dari internet

        override suspend fun createCall(): Flow<ApiResponse<List<GenresMovieItem>>> {
            TODO()
        }

        override suspend fun saveCallResult(data: List<GenresMovieItem>) { TODO() }
    }.asFlow()

    override fun getFavoriteMovies(): Flow<List<MovieModel>>   =  localDataSource.getFavoriteMovies().map { MovieDataMapper.mapEntitiesToDomain(it) }

    override fun setFavoriteMovie(movie: MovieModel, state: Boolean) {
        val movieEntity = MovieDataMapper.mapDomainToEntity(movie)
        appExecutors.diskIO().execute { localDataSource.setFavoriteMovie(movieEntity, state) }
    }

}