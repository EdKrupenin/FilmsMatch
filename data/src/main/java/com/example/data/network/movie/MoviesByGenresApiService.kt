package com.example.data.network.movie

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for fetching movies by genres from the API.
 */
interface MoviesByGenresApiService {
    /**
     * Retrieves movies by genres from the API.
     * @param genres A comma-separated list of genre IDs.
     * @param order The order parameter.
     * @param type The type parameter. Default value is "FILM".
     * @param page The page number.
     * @return [MovieListApiResponse] containing the list of movies by genres.
     */
    @GET("/api/v2.2/films")
    suspend fun getMoviesByGenres(
        @Query("genres") genres: String,
        @Query("order") order: String,
        @Query("type") type: String = "FILM",
        @Query("page") page: Int,
    ): MovieListApiResponse
}
