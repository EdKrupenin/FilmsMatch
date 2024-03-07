package com.example.data.network.genre

import retrofit2.http.GET

/**
 * Retrofit service interface for fetching genre data from the API.
 */
interface GenresApiService {
    /**
     * Fetches genre data from the API.
     * @return [GenresResponse] containing the list of genres.
     */
    @GET("/api/v2.2/films/filters")
    suspend fun getGenres(): GenresResponse
}
