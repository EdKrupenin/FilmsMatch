package com.example.data.network.genre

import com.example.domain.model.GenreDomain
import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response model for a genre from the API.
 * @property name The name of the genre.
 * @property id The identifier of the genre.
 */
data class GenreApiResponse(
    @SerializedName("genre")
    val name: String,
    @SerializedName("id")
    val id: Int,
) {
    /**
     * Converts the [GenreApiResponse] object to a [GenreDomain] object.
     * @return [GenreDomain] object created from the [GenreApiResponse].
     */
    fun toGenreDomain(): GenreDomain {
        return GenreDomain(name = this.name, id = this.id.toString())
    }
}

/**
 * Data class representing the response model for multiple genres from the API.
 * @property genres List of [GenreApiResponse] objects representing genres.
 */
data class GenresResponse(
    @SerializedName("genres")
    val genres: List<GenreApiResponse>
)
