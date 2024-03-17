package com.example.data

/**
 * Represents the domain model for a movie.
 * @property kinopoiskId The Kinopoisk ID of the movie.
 * @property imdbId The IMDb ID of the movie.
 * @property nameRu The Russian name of the movie.
 * @property nameEn The English name of the movie.
 * @property nameOriginal The original name of the movie.
 * @property countries The list of countries where the movie was produced.
 * @property genres The list of genres associated with the movie.
 * @property ratingKinopoisk The Kinopoisk rating of the movie.
 * @property ratingImdb The IMDb rating of the movie.
 * @property year The release year of the movie.
 * @property type The type of the movie (e.g., FILM, SERIES).
 * @property posterUrl The URL of the movie's poster.
 * @property posterUrlPreview The URL of the movie's poster preview.
 * @property details The additional info for film.
 */
data class FilmDomain(
    val kinopoiskId: Int,
    val imdbId: String?,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val countries: List<String>,
    val genres: List<String>,
    val ratingKinopoisk: Double?,
    val ratingImdb: Double?,
    val year: Int,
    val type: String,
    val posterUrl: String?,
    val posterUrlPreview: String?,
    val details: FilmDetailsDomain?,
    val links: List<FilmLinkDomain>
)
