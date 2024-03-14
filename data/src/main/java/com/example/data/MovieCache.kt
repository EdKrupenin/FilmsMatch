package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieCacheManager @Inject constructor() {
    // Кэш фильмов, представленный в виде MutableStateFlow
    private val _movieCache = MutableStateFlow(
        MovieData(
            emptyList(),
            1, 1, "", ""
        )
    )

    // Публичный доступ к кэшу фильмов через StateFlow
    val movieCache: StateFlow<MovieData> get() = _movieCache.asStateFlow()

    /**
     * Обновляет список фильмов в кэше.
     * @param movies список фильмов для обновления.
     */
    private fun updateMovies(movies: List<MovieDomain>) {
        _movieCache.value = _movieCache.value.copy(movies = movies)
    }

    /**
     * Обновляет данные о фильмах в кэше.
     * @param movies список фильмов для обновления.
     * @param currentPage номер текущей страницы для обновления.
     * @param totalPages общее количество страниц для обновления.
     * @param currentGenres .
     * @param currentOrder .
     */
    fun updateMovieData(
        movies: List<MovieDomain>, currentPage: Int, totalPages: Int, currentGenres: String,
        currentOrder: String,
    ) {
        _movieCache.value = _movieCache.value.copy(
            movies = movies,
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
    fun updateMovieDetails(kinopoiskId: Int, movieDetails: MovieDetailsDomain) {
        val updatedMovies = movieCache.value.movies.map { movie ->
            if (movie.kinopoiskId == kinopoiskId) {
                movie.copy(details = movieDetails)
            } else {
                movie
            }
        }
        updateMovies(updatedMovies)
    }

    /**
     * Получает детали фильма из кэша по идентификатору kinopoiskId.
     * @param kinopoiskId идентификатор фильма.
     * @return MovieDetailsDomain или null, если фильм с таким идентификатором не найден.
     */
    fun getMovieDetails(kinopoiskId: Int): MovieDetailsDomain? {
        return movieCache.value.movies.firstOrNull { it.kinopoiskId == kinopoiskId }?.details
    }

    /**
     * Получает список фильмов из кэша по номеру страницы.
     * @param page номер страницы.
     * @return Список фильмов на указанной странице.
     */
    fun getMoviesByPage(page: Int): List<MovieDomain> {
        // Предполагается, что фильмы в кэше уже разбиты по страницам
        return movieCache.value.movies
    }
}

/**
 * Data class representing a set of movies and the current page number.
 *
 * @property movies List of movies.
 * @property currentPage Current page number.
 * @property totalPages Total number of pages.
 */
data class MovieData(
    val movies: List<MovieDomain>,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val currentGenres: String,
    val currentOrder: String,
)
