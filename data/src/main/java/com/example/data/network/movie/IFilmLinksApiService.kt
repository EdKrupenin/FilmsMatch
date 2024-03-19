package com.example.data.network.movie

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IFilmLinksApiService {
    /**
     * Retrieves filmsList details by ID from the API.
     * @param kinopoiskId The id of movie.
     * @return [FilmLinksListApiResponse] containing the list of filmsList by genres.
     */
    @GET("api/v2.2/films/{id}/external_sources?page=1")
    suspend fun getFilmLinks(
        @Path("id") kinopoiskId: Int,
    ): Response<FilmLinksListApiResponse>
}