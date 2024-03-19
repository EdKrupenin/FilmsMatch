package com.example.data.repository

import com.example.data.network.genre.IGenresApiService
import com.example.domain.cache.IGenreCache
import com.example.domain.model.GenreDomain
import com.example.domain.repository.GenreRepository
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val getGenreService: IGenresApiService,
    private val genreCache: IGenreCache,
) : BaseRepository(), GenreRepository {
    /**
     * Fetches a list of genres from the network or cache. This function first checks if the genre model
     * is already available in the cache. If not, it makes a network request to fetch the genres,
     * caches them for future use, and then returns the fetched list.
     *
     * @return A [Result] containing a list of [GenreDomain] representing the genres on success,
     * or an error on failure. The error could indicate a network issue, a bad request, or an empty response.
     */
    override suspend fun getGenres(): Result<List<GenreDomain>> {
        val cachedGenres = genreCache.genresFromNetwork
        if (cachedGenres.isNotEmpty()) {
            return Result.success(cachedGenres)
        }

        return fetchAndUpdateCache(
            performNetworkRequest = { getGenreService.getGenres() },
            processResponse = { response -> response.genres.map { it.toGenreDomain() } },
            updateCache = { genres -> genreCache.updateGenresFromNetwork(genres) },
            isEmptyResponse = { response -> response.genres.isEmpty() }
        )

    }

    /**
     * Represents the genres selected by the user.
     *
     * This property provides both access to the current list of selected genres
     * and the ability to update that list. Fetching the property retrieves the
     * selected genres from the cache via `genreCacheManagerOld`. Setting the property
     * updates the selected genres in the cache, thereby ensuring that any changes
     * in the selection are reflected across the application.
     *
     * The getter returns the current list of selected genres as stored in the cache.
     * The setter takes a list of `GenreDomain` objects and updates the cache with this new list.
     */
    override var selectedGenres: List<GenreDomain>
        get() = genreCache.selectedGenres.value
        set(value) {
            genreCache.updateSelectedGenres(value)
        }
}
