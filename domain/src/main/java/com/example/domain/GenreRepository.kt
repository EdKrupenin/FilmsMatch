package com.example.domain

import com.example.data.GenreCacheManager
import com.example.data.GenreDomain
import com.example.data.network.genre.GenresApiService
import javax.inject.Inject

/**
 * Repository interface for fetching genre data.
 */
interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreDomain>>
}

/**
 * Implementation of the [GenreRepository] interface.
 * @property getGenreService The API service used for fetching genre data.
 */
class GenreRepositoryImpl @Inject constructor(
    private val getGenreService: GenresApiService,
    private val genreCacheManager: GenreCacheManager,
) : GenreRepository {
    /**
     * Asynchronously fetches genre data from the API service and maps it to [GenreDomain] objects.
     * @return A list of [GenreDomain] objects representing the genres.
     */
    override suspend fun getGenres(): Result<List<GenreDomain>> {
        return runCatching {
            genreCacheManager.genreCache.value.genresFromNetwork.ifEmpty {
                fetchAndCacheGenres().getOrThrow()
            }
        }
    }

    private suspend fun fetchAndCacheGenres(): Result<List<GenreDomain>> {
        return runCatching {
            val response = getGenreService.getGenres()
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.genres.isEmpty()) throw FilmsMatchError.EmptyResponse
                val genres = body.genres.map { it.toGenreDomain() }
                genreCacheManager.updateGenreData(genres)
                genreCacheManager.genreCache.value.genresFromNetwork
            } else {
                when (response.code()) {
                    400 -> throw FilmsMatchError.BadRequest
                    else -> throw FilmsMatchError.NetworkError
                }
            }
        }
    }
}