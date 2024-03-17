package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieCacheManager @Inject constructor() {
    private val _movieCache = MutableStateFlow(
        FilmsListDomain(
            emptyList(),
            1, 1, "", ""
        )
    )

    val movieCache: StateFlow<FilmsListDomain> get() = _movieCache.asStateFlow()

    /**
     * Обновляет список фильмов в кэше.
     * @param filmsList список фильмов для обновления.
     */
    private fun updateFilmsList(filmsList: List<FilmDomain>) {
        _movieCache.value = _movieCache.value.copy(filmsList = filmsList)
    }

    /**
     * Обновляет данные о фильмах в кэше.
     * @param filmsList список фильмов для обновления.
     * @param currentPage номер текущей страницы для обновления.
     * @param totalPages общее количество страниц для обновления.
     * @param currentGenres .
     * @param currentOrder .
     */
    fun updateFilmsData(
        filmsList: List<FilmDomain>, currentPage: Int, totalPages: Int, currentGenres: String,
        currentOrder: String,
    ) {
        _movieCache.value = _movieCache.value.copy(
            filmsList = filmsList,
            currentPage = currentPage,
            totalPages = totalPages,
            currentGenres = currentGenres,
            currentOrder = currentOrder
        )
    }

    /**
     * Обновляет детали фильма в кэше.
     * @param kinopoiskId идентификатор фильма для обновления.
     * @param movieDetails детали фильма для обновления.
     */
    fun updateFilmDetails(kinopoiskId: Int, movieDetails: FilmDetailsDomain) {
        val updatedMovies = movieCache.value.filmsList.map { movie ->
            if (movie.kinopoiskId == kinopoiskId) {
                movie.copy(details = movieDetails)
            } else {
                movie
            }
        }
        updateFilmsList(updatedMovies)
    }

    /**
     * Получает детали фильма из кэша по идентификатору kinopoiskId.
     * @param kinopoiskId идентификатор фильма.
     * @return FilmDetailsDomain или null, если фильм с таким идентификатором не найден.
     */
    fun getFilmDetails(kinopoiskId: Int): FilmDetailsDomain? {
        return movieCache.value.filmsList.firstOrNull { it.kinopoiskId == kinopoiskId }?.details
    }

    /**
     * Получает список фильмов из кэша по номеру страницы.
     * @param page номер страницы.
     * @return Список фильмов на указанной странице.
     */
    fun getFilmsByPage(page: Int): List<FilmDomain> {
        // Предполагается, что фильмы в кэше уже разбиты по страницам
        return movieCache.value.filmsList
    }

    fun updateFilmLinks(kinopoiskId: Int, links: List<FilmLinkDomain>) {
        val updatedMovies = movieCache.value.filmsList.map { movie ->
            if (movie.kinopoiskId == kinopoiskId) {
                movie.copy(links = links)
            } else {
                movie
            }
        }
        updateFilmsList(updatedMovies)
    }

    fun getFilmLinks(kinopoiskId: Int): List<FilmLinkDomain> {
        return movieCache.value.filmsList.first { it.kinopoiskId == kinopoiskId }.links
    }
}

