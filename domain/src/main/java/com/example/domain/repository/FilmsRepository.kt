package com.example.domain.repository

import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain

/**
 * Repository interface for fetching movie model.
 */
interface FilmsRepository {
    suspend fun getFilms(page: Int): Result<FilmsListDomain>
    suspend fun getFilmDetails(kinopoiskId: Int): Result<FilmDetailsDomain>
    suspend fun getFilmLinks(kinopoiskId: Int): Result<List<FilmLinkDomain>>
}