package com.example.domain.cache

import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain

interface IFilmsCache {
    val filmsListCache: FilmsListDomain
    fun updateFilmsListData(data: FilmsListDomain)
    fun getFilmDetails(kinopoiskId: Int): FilmDetailsDomain
    fun updateFilmDetails(kinopoiskId: Int, data: FilmDetailsDomain)
    fun getFilmLinks(kinopoiskId: Int): List<FilmLinkDomain>
    fun updateFilmLinks(kinopoiskId: Int, data: List<FilmLinkDomain>)
}

