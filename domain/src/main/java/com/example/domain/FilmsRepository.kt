package com.example.domain

import com.example.data.FilmsListDomain
import com.example.data.FilmDetailsDomain
import com.example.data.FilmLinkDomain

/**
 * Repository interface for fetching movie data.
 */
interface FilmsRepository {
    suspend fun getFilms(page: Int): Result<FilmsListDomain>
    suspend fun getFilmDetails(kinopoiskId: Int): Result<FilmDetailsDomain>
    suspend fun getFilmLinks(kinopoiskId: Int): Result<List<FilmLinkDomain>>
}