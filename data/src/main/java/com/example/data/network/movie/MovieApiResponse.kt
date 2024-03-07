package com.example.data.network.movie

import com.example.data.MovieDomain
import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response model for a list of movies from the API.
 * @property total The total number of movies in the response.
 * @property totalPages The total number of pages.
 * @property items The list of movie items.
 */
data class MovieListApiResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("items")
    val items: List<MovieApiResponse>
)

/**
 * Data class representing the response model for a movie from the API.
 * @property kinopoiskId The Kinopoisk ID of the movie.
 * @property imdbId The IMDb ID of the movie.
 * @property nameRu The Russian name of the movie.
 * @property nameEn The English name of the movie.
 * @property nameOriginal The original name of the movie.
 * @property countries The list of countries associated with the movie.
 * @property genres The list of genres associated with the movie.
 * @property ratingKinopoisk The Kinopoisk rating of the movie.
 * @property ratingImdb The IMDb rating of the movie.
 * @property year The release year of the movie.
 * @property type The type of the movie.
 * @property posterUrl The URL of the movie poster.
 * @property posterUrlPreview The URL of the preview image of the movie poster.
 */
data class MovieApiResponse(
    @SerializedName("kinopoiskId")
    val kinopoiskId: Int,
    @SerializedName("imdbId")
    val imdbId: String?,
    @SerializedName("nameRu")
    val nameRu: String,
    @SerializedName("nameEn")
    val nameEn: String?,
    @SerializedName("nameOriginal")
    val nameOriginal: String,
    @SerializedName("countries")
    val countries: List<Country>,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("ratingKinopoisk")
    val ratingKinopoisk: Double,
    @SerializedName("ratingImdb")
    val ratingImdb: Double?,
    @SerializedName("year")
    val year: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("posterUrl")
    val posterUrl: String,
    @SerializedName("posterUrlPreview")
    val posterUrlPreview: String,
)

/**
 * Extension function to convert [MovieApiResponse] objects to [MovieDomain] objects.
 * @return [MovieDomain] object created from the [MovieApiResponse].
 */
fun MovieApiResponse.toMovieDomain(): MovieDomain {
    val countries = this.countries.map { it.country }
    val genres = this.genres.map { it.genre }

    return MovieDomain(
        kinopoiskId = this.kinopoiskId,
        imdbId = this.imdbId,
        nameRu = this.nameRu,
        nameEn = this.nameEn,
        nameOriginal = this.nameOriginal,
        countries = countries,
        genres = genres,
        ratingKinopoisk = this.ratingKinopoisk,
        ratingImdb = this.ratingImdb,
        year = this.year,
        type = this.type,
        posterUrl = this.posterUrl,
        posterUrlPreview = this.posterUrlPreview
    )
}

/**
 * Data class representing a country associated with a movie.
 * @property country The name of the country.
 */
data class Country(
    @SerializedName("country")
    val country: String,
)

/**
 * Data class representing a genre associated with a movie.
 * @property genre The name of the genre.
 */
data class Genre(
    @SerializedName("genre")
    val genre: String,
)
