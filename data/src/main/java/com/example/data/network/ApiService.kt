package com.example.data.network

import retrofit2.http.GET

interface ApiService {
    @GET("/v1/movie/possible-values-by-field?field=genres.name")
    suspend fun getGenres(): List<GenreApiResponse>
}