package com.example.data

/**
 * Data class representing a set of filmsList and the current page number.
 *
 * @property filmsList List of films.
 * @property currentPage Current page number.
 * @property totalPages Total number of pages.
 */
data class FilmsListDomain(
    val filmsList: List<FilmDomain>,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val currentGenres: String,
    val currentOrder: String,
)