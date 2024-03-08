package com.example.domain

import com.example.data.MovieDomain
import com.example.data.network.movie.MoviesByGenresApiService
import com.example.data.network.movie.toMovieDomain
import javax.inject.Inject

/**
 * Repository interface for fetching movie data.
 */
interface MovieRepository {
    suspend fun getMovie(genres: String, order: String, page: Int): MoviePage
}

/**
 * Data class representing a page of movies.
 * @property movies List of movies on the current page.
 * @property currentPage The current page number.
 */
data class MoviePage(
    val movies: List<MovieDomain>,
    val currentPage: Int,
    val totalPage: Int,
)

/**
 * Implementation of the [MovieRepository] interface.
 * @property MoviesByGenresApiService The API service used for fetching genre data.
 */
class MovieRepositoryImpl @Inject constructor(private val apiService: MoviesByGenresApiService) :
    MovieRepository {
    /**
     * Asynchronously fetches movie data from the API service and maps it to [MovieDomain] objects.
     * @return A [MoviePage] object containing the list of movies and the current page number.
     */
    override suspend fun getMovie(genres: String, order: String, page: Int): MoviePage {
        // Retrieve movie data from the API service
        val genresResponse =
            apiService.getMoviesByGenres(genres = genres, order = order, page = page)
        // Map movie API response objects to MovieDomain objects
        val movies = genresResponse.items.map { it.toMovieDomain() }
        // Return MoviePage with movies list and current page number
        return MoviePage(movies, page, genresResponse.totalPages)
    }
}