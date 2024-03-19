package com.example.domain.model
/**
 * Data class representing a genre domain entity.
 *
 * @property name The name of the genre.
 * @property id The unique identifier (slug) of the genre.
 */
data class GenreDomain (
    val name: String,
    val id : String,
)