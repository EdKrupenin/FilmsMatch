package com.example.domain

import com.example.data.GenreDomain

/**
 * Repository interface for fetching genre data.
 */
interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreDomain>>
}