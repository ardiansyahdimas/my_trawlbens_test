package com.test.core.utils.mapper


import com.test.core.data.source.local.entity.GenreEntity
import com.test.core.data.source.remote.response.GenresMovieItem
import com.test.core.domain.model.GenreModel

object GenreMovieDataMapper {
    fun mapResponsesToEntities(
        input: List<GenresMovieItem>
    ): List<GenreEntity> {
        val genreList = ArrayList<GenreEntity>()
        input.map {
            val review = GenreEntity(
                id = it.id,
                name  = it.name
            )
            genreList.add(review)
        }
        return genreList
    }

    fun mapEntitiesToDomain(input: List<GenreEntity>): List<GenreModel> =
        input.map {
            GenreModel(
                id = it.id,
                name  = it.name
            )
        }
}