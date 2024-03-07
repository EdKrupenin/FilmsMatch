package com.example.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class MovieCacheManager @Inject constructor() {
    // Кэш фильмов, представленный в виде MutableStateFlow
    private val _movieCache = MutableStateFlow(MovieData(emptyList(), 0))

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
     * Обновляет номер текущей страницы в кэше.
     * @param page номер текущей страницы для обновления.
     */
    private fun updatePage(page: Int) {
        _movieCache.value = _movieCache.value.copy(currentPage = page)
    }

    fun updateMovieData(movies: List<MovieDomain>, currentPage: Int) {
        updateMovies(movies)
        updatePage(currentPage)
    }
}

/**
 * Data class representing a set of movies and the current page number.
 *
 * @property movies List of movies.
 * @property currentPage Current page number.
 */
data class MovieData(
    val movies: List<MovieDomain>,
    val currentPage: Int = 1
)
