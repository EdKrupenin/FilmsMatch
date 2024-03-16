package com.example.domain

import com.example.data.GenreCacheManager
import com.example.data.MovieCacheManager
import com.example.data.MovieData
import com.example.data.MovieDetailsDomain
import com.example.data.MovieLinkDomain
import com.example.data.network.movie.MovieDetailsApiService
import com.example.data.network.movie.MovieLinksApiService
import com.example.data.network.movie.MoviesByGenresApiService
import com.example.data.network.movie.toMovieDomain
import javax.inject.Inject

/**
 * Repository interface for fetching movie data.
 */
interface MovieRepository {
    suspend fun getMovie(page: Int): Result<MovieData>

    suspend fun getMovieDetails(kinopoiskId: Int): Result<MovieDetailsDomain>
    suspend fun getMovieLinks(kinopoiskId: Int): Result<List<MovieLinkDomain>>
}

/**
 * Implementation of the [MovieRepository] interface.
 * @property genreCacheManager Manages cache for genre data.
 * @property movieCacheManager Manages cache for movie data.
 * @property getMovieService API service for fetching movies.
 * @property getMovieDetailsService API service for fetching movie details.
 */
class MovieRepositoryImpl @Inject constructor(
    private val genreCacheManager: GenreCacheManager,
    private val movieCacheManager: MovieCacheManager,
    private val getMovieService: MoviesByGenresApiService,
    private val getMovieDetailsService: MovieDetailsApiService,
    private val getMovieLinksService: MovieLinksApiService,
) : MovieRepository {

    /**
     * Fetches movies based on the selected genres and order. If the requested page is already cached,
     * returns cached data; otherwise, fetches data from the network and caches it
     *
     * @param page The page number to fetch.
     * @return [MovieData] containing the list of movies, current page, and total pages.
     * @throws IllegalArgumentException If no genres are selected.
     * @throws IllegalStateException If the requested page number is greater than the total pages available.
     */
    override suspend fun getMovie(page: Int): Result<MovieData> {
        val genresString = getSelectedGenresString()
        val orderString = getSelectedOrderString()
        val totalPages = movieCacheManager.movieCache.value.totalPages
        return runCatching {
            validatePageAndGenres(page, totalPages, genresString)
            // Check if the movies for the requested page are already cached
            val cachedMovies = movieCacheManager.getMoviesByPage(page)
            if (cachedMovies.isNotEmpty() &&
                movieCacheManager.movieCache.value.currentPage == page &&
                movieCacheManager.movieCache.value.currentGenres == genresString &&
                movieCacheManager.movieCache.value.currentOrder == orderString
            ) {
                // Return the cached movies if they exist
                MovieData(cachedMovies, page, totalPages, genresString, orderString)
            } else {
                // Fetch movies from the network and cache them if not already cached
                fetchAndCacheMovies(page, genresString, orderString).getOrThrow()
            }
        }
    }

    /**
     * Retrieves the details of a movie with the specified ID. It first attempts to fetch the details
     * from the cache. If the details are not found in the cache, it fetches them from the network,
     * caches them for future use, and then returns the details.
     *
     * @param kinopoiskId The ID of the movie for which details are to be fetched.
     * @return [MovieDetailsDomain] containing the details of the movie.
     * @throws IllegalStateException if the details cannot be found in the cache after fetching from the network.
     */
    override suspend fun getMovieDetails(kinopoiskId: Int): Result<MovieDetailsDomain> {
        // Get cached movie details if available
        return runCatching {
            // Return cached details if found
            movieCacheManager.getMovieDetails(kinopoiskId)
            // Fetch movie details from the network and cache them if not already cached
                ?: fetchAndCacheMovieDetails(kinopoiskId).getOrThrow()
        }
    }

    override suspend fun getMovieLinks(kinopoiskId: Int): Result<List<MovieLinkDomain>> {
        return runCatching {
            // Return cached details if found
            movieCacheManager.getMovieLinks(kinopoiskId).ifEmpty {
                // Fetch movie details from the network and cache them if not already cached
                fetchAndCacheMovieLinks(kinopoiskId).getOrThrow()
            }
        }
    }

    /**
     * Returns a comma-separated string of selected genres.
     *
     * @return A string representing the selected genres.
     */
    private fun getSelectedGenresString(): String =
        genreCacheManager.genreCache.value.selectedGenres.joinToString(",") { it.id }

    /**
     * Returns the selected order string from the cache.
     *
     * @return A string representing the selected order.
     */
    private fun getSelectedOrderString(): String =
        genreCacheManager.genreCache.value.selectedOrder?.value.toString()

    /**
     * Validates the page number and genres string.
     *
     * @param page The page number to validate.
     * @param totalPages The total number of pages available.
     * @param genresString The string representing the selected genres.
     * @throws IllegalArgumentException If no genres are selected.
     * @throws IllegalStateException If the requested page number is greater than the total pages available.
     */
    private fun validatePageAndGenres(page: Int, totalPages: Int, genresString: String) {
        if (genresString.isEmpty()) {
            throw FilmsMatchError.BadRequest
        }
        if (page > totalPages) {
            throw FilmsMatchError.EmptyResponse
        }
    }

    /**
     * Fetches movies from the network, caches them, and returns the movie data.
     *
     * @param page The page number to fetch.
     * @param genresString The string representing the selected genres.
     * @param orderString The string representing the selected order.
     * @return [MovieData] containing the fetched and cached movies.
     */
    private suspend fun fetchAndCacheMovies(
        page: Int,
        genresString: String,
        orderString: String,
    ): Result<MovieData> {
        return runCatching {
            val response = getMovieService.getMoviesByGenres(
                genres = genresString, order = orderString, page = page
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.items.isEmpty()) throw FilmsMatchError.EmptyResponse
                val movies = body.items.map { it.toMovieDomain() }
                movieCacheManager.updateMovieData(
                    movies,
                    page,
                    body.totalPages,
                    genresString,
                    orderString
                )
                movieCacheManager.movieCache.value
            } else {
                when (response.code()) {
                    400 -> throw FilmsMatchError.BadRequest
                    else -> throw FilmsMatchError.NetworkError
                }
            }
        }

    }

    /**
     * Fetches movie details from the network, caches them, and returns the movie details.
     *
     * @param kinopoiskId The ID of the movie for which details are to be fetched.
     * @return [MovieDetailsDomain] containing the fetched movie details.
     */
    private suspend fun fetchAndCacheMovieDetails(kinopoiskId: Int): Result<MovieDetailsDomain> {
        return runCatching {
            val response = getMovieDetailsService.getMovieDetails(kinopoiskId)
            if (response.isSuccessful) {
                val movieDetails = response.body()!!.toMovieDetailsDomain()
                if (movieDetails.isEmpty()) throw FilmsMatchError.EmptyResponse
                // Cache the movie details
                movieCacheManager.updateMovieDetails(kinopoiskId, movieDetails)
                // Return the movie details from cache
                movieCacheManager.getMovieDetails(kinopoiskId)!!
            } else {
                when (response.code()) {
                    400 -> throw FilmsMatchError.BadRequest
                    else -> throw FilmsMatchError.NetworkError
                }
            }
        }
    }

    private suspend fun fetchAndCacheMovieLinks(kinopoiskId: Int): Result<List<MovieLinkDomain>> {
        return runCatching {
            val response = getMovieLinksService.getMovieLinks(kinopoiskId)
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.items.isEmpty()) throw FilmsMatchError.EmptyResponse
                val links = body.items.map { it.toMovieLinkDomain() }
                movieCacheManager.updateMovieLinks(kinopoiskId, links)
                // Return the movie details from cache
                movieCacheManager.getMovieLinks(kinopoiskId)
            } else {
                when (response.code()) {
                    400 -> throw FilmsMatchError.BadRequest
                    else -> throw FilmsMatchError.NetworkError
                }
            }
        }
    }
}