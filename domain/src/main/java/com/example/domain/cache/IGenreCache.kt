package com.example.domain.cache

import com.example.domain.model.GenreDomain
import kotlinx.coroutines.flow.StateFlow

interface IGenreCache {
    val genresFromNetwork: List<GenreDomain>
    val selectedGenres: StateFlow<List<GenreDomain>>
    fun updateSelectedGenres(genres: List<GenreDomain>)
    fun updateGenresFromNetwork(genres: List<GenreDomain>)
}
