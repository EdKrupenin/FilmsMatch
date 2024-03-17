package com.example.data.network.movie

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for fetching filmsList by genres from the API.
 */
interface FilmsListByGenresApiService {
    /**
     * Retrieves filmsList by genres from the API.
     * @param genres A comma-separated list of genre IDs.
     * @param order The order parameter.
     * @param type The type parameter. Default value is "FILM".
     * @param page The page number.
     * @return [FilmsListApiResponse] containing the list of filmsList by genres.
     */
    @GET("/api/v2.2/films")
    suspend fun getFilmListByGenres(
        @Query("genres") genres: String,
        @Query("order") order: String,
        @Query("type") type: String = "FILM",
        @Query("yearTo") yearTo: Int,
        @Query("page") page: Int,
    ): Response<FilmsListApiResponse>
}

interface FilmDetailsApiService {
    /**
     * Retrieves filmsList details by ID from the API.
     * @param kinopoiskId The id of movie.
     * @return [FilmLinksListApiResponse] containing the list of filmsList by genres.
     */
    @GET("api/v2.2/films/{id}")
    suspend fun getFilmDetails(
        @Path("id") kinopoislId: Int
    ): Response<FilmDetailApiResponse>
}

interface FilmLinksApiService {
    /**
     * Retrieves filmsList details by ID from the API.
     * @param kinopoiskId The id of movie.
     * @return [MovieLinksApiResponse] containing the list of filmsList by genres.
     */
    @GET("api/v2.2/films/{id}/external_sources?page=1")
    suspend fun getFilmLinks(
        @Path("id") kinopoislId: Int
    ): Response<FilmLinksListApiResponse>
}