package com.example.data.cache

import com.example.domain.cache.IFilmsCache
import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain
import javax.inject.Inject

internal class FilmsCacheImpl @Inject constructor() : IFilmsCache {
    private var _filmsListCache = FilmsListDomain.DEFAULT
    override val filmsListCache: FilmsListDomain get() = _filmsListCache

    override fun updateFilmsListData(data: FilmsListDomain) {
        _filmsListCache = data
    }

    override fun updateFilmDetails(kinopoiskId: Int, data: FilmDetailsDomain) {
        val updatedFilms = filmsListCache.filmsList.map { film ->
            if (film.kinopoiskId == kinopoiskId) {
                film.copy(details = data)
            } else {
                film
            }
        }
        updateFilmsList(updatedFilms)
    }

    override fun getFilmDetails(kinopoiskId: Int): FilmDetailsDomain {
        return filmsListCache.filmsList.first { it.kinopoiskId == kinopoiskId }.details
    }

    override fun updateFilmLinks(kinopoiskId: Int, data: List<FilmLinkDomain>) {
        val updatedMovies = filmsListCache.filmsList.map { film ->
            if (film.kinopoiskId == kinopoiskId) {
                film.copy(links = data)
            } else {
                film
            }
        }
        updateFilmsList(updatedMovies)
    }

    override fun getFilmLinks(kinopoiskId: Int): List<FilmLinkDomain> {
        return filmsListCache.filmsList.first { it.kinopoiskId == kinopoiskId }.links
    }

    private fun updateFilmsList(filmsList: List<FilmDomain>) {
        _filmsListCache = _filmsListCache.copy(filmsList = filmsList)
    }
}