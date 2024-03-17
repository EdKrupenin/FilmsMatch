package com.example.domain

import com.example.data.GenreCacheManager
import com.example.data.GenreDomain
import com.example.data.MovieCacheManager
import com.example.data.FilmsListDomain
import com.example.data.FilmDetailsDomain
import com.example.data.FilmLinkDomain
import com.example.data.SortingOption
import com.example.data.network.genre.GenresApiService
import com.example.data.network.movie.FilmDetailsApiService
import com.example.data.network.movie.FilmLinksApiService
import com.example.data.network.movie.FilmsListByGenresApiService
import retrofit2.Response
import java.util.Calendar
import javax.inject.Inject

/**
 * Provides operations for fetching movie data including list, details, and links.
 * It utilizes caching to optimize data retrieval and reduce network requests.
 */
class ContentRepository @Inject constructor(
    private val genreCacheManager: GenreCacheManager,
    private val movieCacheManager: MovieCacheManager,
    private val getGenreService: GenresApiService,
    private val getFilmsService: FilmsListByGenresApiService,
    private val getFilmDetailsService: FilmDetailsApiService,
    private val getFilmLinksService: FilmLinksApiService,
) : GenreRepository, FilmsRepository, SortingOptionsProvider {

    /**
     * Fetches a list of genres from the network or cache. This function first checks if the genre data
     * is already available in the cache. If not, it makes a network request to fetch the genres,
     * caches them for future use, and then returns the fetched list.
     *
     * @return A [Result] containing a list of [GenreDomain] representing the genres on success,
     * or an error on failure. The error could indicate a network issue, a bad request, or an empty response.
     */
    override suspend fun getGenres(): Result<List<GenreDomain>> {
        val cachedGenres = genreCacheManager.genreCache.value.genresFromNetwork
        if (cachedGenres.isNotEmpty()) {
            return Result.success(cachedGenres)
        }

        return handleResponse({ getGenreService.getGenres() },
            { response ->
                response.genres.takeIf { it.isNotEmpty() }?.map { it.toGenreDomain() }
                    ?: throw FilmsMatchError.EmptyResponse
            },
            { genres -> genreCacheManager.updateGenreData(genres) })

    }

    /**
     * Fetches a page of filmsList based on user-selected genres and order. It tries to serve data from cache first
     * before fetching from network. This approach reduces network usage and provides a faster user experience.
     *
     * @param page The page number of movie data to fetch.
     * @return A [Result] containing either a [FilmsListDomain] object or an error.
     */
    override suspend fun getFilms(page: Int): Result<FilmsListDomain> {
        val genresString = generateGenresString()
        val orderString = generateOrderString()

        validatePageAndGenres(page, genresString).onFailure { return Result.failure(it) }

        return checkCachedMovies(page, genresString, orderString) ?: handleResponse(
            {
                getFilmsService.getFilmListByGenres(
                    genres = genresString,
                    order = orderString,
                    yearTo = Calendar.getInstance().get(Calendar.YEAR),
                    page = page
                )
            },
            { response ->
                response.takeIf { it.total > 0 }?.toFilmsListData()
                    ?: throw FilmsMatchError.EmptyResponse
            },
            { movieData ->
                movieCacheManager.updateFilmsData(
                    movieData.filmsList, page, movieData.totalPages, genresString, orderString
                )
            })
    }

    /**
     * Retrieves detailed information about a specific movie. It first tries to retrieve the data from cache.
     * If the cache doesn't contain the requested data, it fetches it from the network and updates the cache.
     *
     * @param kinopoiskId Unique identifier for the movie.
     * @return A [Result] containing either a [FilmDetailsDomain] object or an error.
     */
    override suspend fun getFilmDetails(kinopoiskId: Int): Result<FilmDetailsDomain> {
        movieCacheManager.getFilmDetails(kinopoiskId)?.let {
            return Result.success(it)
        }

        return handleResponse({ getFilmDetailsService.getFilmDetails(kinopoiskId) },
            { response ->
                response.takeUnless { it.isEmpty() }?.toFilmDetailsDomain()
                    ?: throw FilmsMatchError.EmptyResponse
            },
            { movieDetails -> movieCacheManager.updateFilmDetails(kinopoiskId, movieDetails) })
    }

    /**
     * Fetches watch links for a movie, allowing users to know where the movie is available for streaming.
     * Similar to movie details, it attempts to fetch this data from cache before resorting to a network request.
     *
     * @param kinopoiskId Unique identifier for the movie whose links are being requested.
     * @return A [Result] containing either a list of [FilmLinkDomain] objects or an error.
     */
    override suspend fun getFilmLinks(kinopoiskId: Int): Result<List<FilmLinkDomain>> {
        movieCacheManager.getFilmLinks(kinopoiskId).takeUnless { it.isEmpty() }?.let {
            return Result.success(it)
        }

        return handleResponse({ getFilmLinksService.getFilmLinks(kinopoiskId) },
            { response ->
                response.items.takeIf { it.isNotEmpty() }?.map { it.toFilmLinkDomain() }
                    ?: throw FilmsMatchError.EmptyResponse
            },
            { movieLinks -> movieCacheManager.updateFilmLinks(kinopoiskId, movieLinks) })
    }

    override val sortingOptions = listOf(
        SortingOption("По рейтингу", "RATING"),
        SortingOption("По количеству голосов", "NUM_VOTE"),
        SortingOption("По году", "YEAR")
    )

    // Additional private helper functions

    /**
     * Returns a comma-separated string of selected genres.
     *
     * @return A string representing the selected genres.
     */
    private fun generateGenresString(): String =
        genreCacheManager.genreCache.value.selectedGenres.joinToString(",") { it.id }

    /**
     * Returns the selected order string from the cache.
     *
     * @return A string representing the selected order.
     */
    private fun generateOrderString(): String =
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
    private fun validatePageAndGenres(page: Int, genresString: String): Result<Unit> {
        return runCatching {
            if (genresString.isEmpty()) {
                throw FilmsMatchError.BadRequest
            }
            if (page > movieCacheManager.movieCache.value.totalPages) {
                throw FilmsMatchError.EmptyResponse
            }
        }
    }

    /**
     * Checks if the movie data for the requested page is already cached and matches the current genres and order.
     * If cached data is available and matches the request parameters, it is returned as a successful Result.
     *
     * @param page The requested page number.
     * @param genresString The genres string used for filtering filmsList.
     * @param orderString The order string used for sorting filmsList.
     * @return A [Result] containing cached [FilmsListDomain] if available; null otherwise.
     */
    private fun checkCachedMovies(
        page: Int,
        genresString: String,
        orderString: String,
    ): Result<FilmsListDomain>? {
        val cachedMovies = movieCacheManager.getFilmsByPage(page)
        return if (cachedMovies.isNotEmpty() && movieCacheManager.movieCache.value.currentPage == page && movieCacheManager.movieCache.value.currentGenres == genresString && movieCacheManager.movieCache.value.currentOrder == orderString) {
            Result.success(
                FilmsListDomain(
                    cachedMovies,
                    page, movieCacheManager.movieCache.value.totalPages,
                    genresString,
                    orderString
                )
            )
        } else null
    }

    /**
     * Performs a network request using the provided call function. Handles success and failure cases,
     * converting them to a Result type that can be used by the calling functions.
     *
     * @param T The type of the expected response.
     * @param call A suspend function that performs the network request.
     * @return A [Result] containing the [Response] if successful or an error if the request fails.
     */
    private suspend fun <T> performRequest(call: suspend () -> Response<T>): Result<T> {
        return runCatching {
            val response = call()
            if (response.isSuccessful) {
                response.body() ?: throw FilmsMatchError.EmptyResponse
            } else {
                throw when (response.code()) {
                    400 -> FilmsMatchError.BadRequest
                    else -> FilmsMatchError.NetworkError
                }
            }
        }
    }

    /**
     * Handles the network response for a given request. On success, processes the response using the onSuccess function,
     * updates the cache with the result using updateCache, and returns the processed data as a Result.
     *
     * @param T The type of the raw network response.
     * @param R The type of the processed data to be returned.
     * @param request A suspend function that performs the network request.
     * @param onSuccess A function to process the raw response on success.
     * @param updateCache A function to update the cache with the processed data.
     * @return A [Result] containing the processed data if successful or an error if the operation fails.
     */
    private suspend fun <T, R> handleResponse(
        request: suspend () -> Response<T>,
        onSuccess: (T) -> R,
        updateCache: (R) -> Unit,
    ): Result<R> {
        return performRequest(request).mapCatching { response ->
            val result = onSuccess(response)
            updateCache(result)
            result
        }
    }

}