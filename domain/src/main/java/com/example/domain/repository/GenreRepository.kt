package com.example.domain.repository

import com.example.domain.model.GenreDomain

/**
 * Repository interface for fetching genre model.
 */
interface GenreRepository {
    suspend fun getGenres(): Result<List<GenreDomain>>

    var selectedGenres :  List<GenreDomain>

}
