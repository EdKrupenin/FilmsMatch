package com.example.domain

import com.example.data.GenreDomain
import com.example.data.network.ApiService
import javax.inject.Inject

/**
 * Repository interface for fetching genre data.
 */
interface GenreRepository {
    suspend fun getGenres(): List<GenreDomain>
}
/**
 * Implementation of the [GenreRepository] interface.
 * @property apiService The API service used for fetching genre data.
 */
class GenreRepositoryImpl @Inject constructor(private val apiService: ApiService) : GenreRepository {
    /**
     * Asynchronously fetches genre data from the API service and maps it to [GenreDomain] objects.
     * @return A list of [GenreDomain] objects representing the genres.
     */
    override suspend fun getGenres(): List<GenreDomain> {
        // Retrieve genre data from the API service
        val genreApiResponseList = apiService.getGenres()
        // Map genre API response objects to GenreDomain objects
        return genreApiResponseList.map { it.toGenreDomain() }
    }
}