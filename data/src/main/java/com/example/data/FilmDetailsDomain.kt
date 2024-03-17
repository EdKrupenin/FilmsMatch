package com.example.data

data class FilmDetailsDomain(
    val kinopoiskHDId: String,
    val slogan: String,
    val description: String,
    val ratingMpaa: String,
    val shortDescription: String,
    val ratingAgeLimits: String,
    val countries: List<String>,
)