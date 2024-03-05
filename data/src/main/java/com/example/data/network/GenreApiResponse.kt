package com.example.data.network

import com.example.data.GenreDomain
import kotlinx.serialization.Serializable
/**
 * Data class representing the response model for a genre from the API.
 * @property name The name of the genre.
 * @property slug The slug identifier of the genre.
 */
@Serializable
data class GenreApiResponse(
    val name: String,
    val slug: String,
) {
    /**
     * Converts the [GenreApiResponse] object to a [GenreDomain] object.
     * @return [GenreDomain] object created from the [GenreApiResponse].
     */
    fun toGenreDomain(): GenreDomain {
        return GenreDomain(name = this.name, id = this.slug)
    }
}