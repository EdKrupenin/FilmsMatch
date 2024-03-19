package com.example.domain.model

data class FilmDetailsDomain(
    val kinopoiskHDId: String,
    val slogan: String,
    val description: String,
    val ratingMpaa: String,
    val shortDescription: String,
    val ratingAgeLimits: String,
    val countries: List<String>,
) {
    companion object {
        val DEFAULT = FilmDetailsDomain(
            "", "", "", "", "", "", emptyList()
        )
    }

    fun isEmpty(): Boolean {
        return kinopoiskHDId.isEmpty() &&
                slogan.isEmpty() &&
                description.isEmpty() &&
                ratingMpaa.isEmpty() &&
                shortDescription.isEmpty() &&
                ratingAgeLimits.isEmpty() &&
                countries.isEmpty()
    }
}