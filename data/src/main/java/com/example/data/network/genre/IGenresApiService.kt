package com.example.data.network.genre

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit service interface for fetching genre model from the API.
 */
interface IGenresApiService {
    /**
     * Fetches genre model from the API.
     * @return [GenresResponse] containing the list of genres.
     */
    @GET("/api/v2.2/films/filters")
    suspend fun getGenres(): Response<GenresResponse>
}
