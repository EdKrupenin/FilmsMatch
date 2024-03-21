package com.example.data.repository

import com.example.data.network.movie.IFilmDetailsApiService
import com.example.data.network.movie.IFilmLinksApiService
import com.example.data.network.movie.IFilmsListByGenresApiService
import com.example.domain.FilmsMatchError
import com.example.domain.cache.IContentCache
import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain
import com.example.domain.repository.FilmsRepository
import java.util.Calendar
import javax.inject.Inject

class FilmsRepositoryImpl @Inject constructor(
    private val getFilmsService: IFilmsListByGenresApiService,
    private val getFilmDetailsService: IFilmDetailsApiService,
    private val getFilmLinksService: IFilmLinksApiService,
    private val contentCache: IContentCache,
) : BaseRepository(), FilmsRepository {

    /**
     * Fetches a page of filmsList based on user-selected genres and order. It tries to serve model from cache first
     * before fetching from network. This approach reduces network usage and provides a faster user experience.
     *
     * @param page The page number of movie model to fetch.
     * @return A [Result] containing either a [FilmsListDomain] object or an error.
     */
    override suspend fun getFilms(page: Int): Result<FilmsListDomain> {
        val genresString = generateGenresString()
        val orderString = generateOrderString()

        validatePageAndGenres(page, genresString).onFailure { return Result.failure(it) }
        checkCachedMovies(
            page = page,
            genresString = genresString,
            orderString = orderString,
        )?.let { return Result.success(it) }

        return fetchAndUpdateCache(
            performNetworkRequest = {
                getFilmsService.getFilmListByGenres(
                    genres = genresString,
                    order = orderString,
                    yearTo = Calendar.getInstance().get(Calendar.YEAR),
                    page = page
                )
            },
            processResponse = { response -> response.toFilmsListData() },
            updateCache = { movieData ->
                contentCache.filmsCache.updateFilmsListData(
                    movieData.copy(
                        currentPage = page,
                        currentGenres = genresString,
                        currentOrder = orderString
                    )
                )
            },
            isEmptyResponse = { response -> response.total <= 0 }
        )
    }

    /**
     * Retrieves detailed information about a specific movie. It first tries to retrieve the model from cache.
     * If the cache doesn't contain the requested model, it fetches it from the network and updates the cache.
     *
     * @param kinopoiskId Unique identifier for the movie.
     * @return A [Result] containing either a [FilmDetailsDomain] object or an error.
     */
    override suspend fun getFilmDetails(kinopoiskId: Int): Result<FilmDetailsDomain> {
        contentCache.filmsCache.getFilmDetails(kinopoiskId).takeUnless { it.isEmpty() }?.let {
            return Result.success(it)
        }

        return fetchAndUpdateCache(
            performNetworkRequest = { getFilmDetailsService.getFilmDetails(kinopoiskId) },
            processResponse = { response -> response.toFilmDetailsDomain() },
            updateCache = { movieDetails ->
                contentCache.filmsCache.updateFilmDetails(
                    kinopoiskId,
                    movieDetails
                )
            },
            isEmptyResponse = { response -> response.isEmpty() }
        )
    }

    /**
     * Fetches watch links for a movie, allowing users to know where the movie is available for streaming.
     * Similar to movie details, it attempts to fetch this model from cache before resorting to a network request.
     *
     * @param kinopoiskId Unique identifier for the movie whose links are being requested.
     * @return A [Result] containing either a list of [FilmLinkDomain] objects or an error.
     */
    override suspend fun getFilmLinks(kinopoiskId: Int): Result<List<FilmLinkDomain>> {
        contentCache.filmsCache.getFilmLinks(kinopoiskId).takeUnless { it.isEmpty() }?.let {
            return Result.success(it)
        }

        return fetchAndUpdateCache(
            performNetworkRequest = { getFilmLinksService.getFilmLinks(kinopoiskId) },
            processResponse = { response -> response.items.map { it.toFilmLinkDomain() } },
            updateCache = { movieLinks ->
                contentCache.filmsCache.updateFilmLinks(
                    kinopoiskId,
                    movieLinks
                )
            },
            isEmptyResponse = { response -> response.items.isEmpty() }
        )
    }

    // Additional private helper functions

    /**
     * Returns a comma-separated string of selected genres.
     *
     * @return A string representing the selected genres.
     */
    private fun generateGenresString(): String =
        contentCache.genreCache.selectedGenres.value.joinToString(",") { it.id }

    /**
     * Returns the selected order string from the cache.
     *
     * @return A string representing the selected order.
     */
    private fun generateOrderString(): String =
        contentCache.sortingOptionsCache.selectedSortingOption.value

    /**
     * Validates the page number and genres string.
     *
     * @param page The page number to validate.
     * @param genresString The string representing the selected genres.
     * @throws IllegalArgumentException If no genres are selected.
     * @throws IllegalStateException If the requested page number is greater than the total pages available.
     */
    private fun validatePageAndGenres(page: Int, genresString: String): Result<Unit> {
        return runCatching {
            if (genresString.isEmpty()) {
                throw FilmsMatchError.BadRequest
            }
            if (page > contentCache.filmsCache.filmsListCache.totalPages) {
                throw FilmsMatchError.EmptyResponse
            }
        }
    }

    /**
     * Checks if the movie model for the requested page is already cached and matches the current genres and order.
     * If cached model is available and matches the request parameters, it is returned as a successful Result.
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
    ): FilmsListDomain? {
        val cachedMovies = contentCache.filmsCache.filmsListCache.filmsList
        return if (cachedMovies.isNotEmpty() && contentCache.filmsCache.filmsListCache.currentPage == page && contentCache.filmsCache.filmsListCache.currentGenres == genresString && contentCache.filmsCache.filmsListCache.currentOrder == orderString) {
            contentCache.filmsCache.filmsListCache
        } else
            null
    }
}
