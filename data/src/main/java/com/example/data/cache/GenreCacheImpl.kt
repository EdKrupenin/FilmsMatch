package com.example.data.cache

import com.example.domain.cache.IGenreCache
import com.example.domain.model.GenreDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GenreCacheImpl @Inject constructor() : IGenreCache {
    private var _genresFromNetwork: List<GenreDomain> = emptyList()
    override val genresFromNetwork: List<GenreDomain> get() = _genresFromNetwork

    private val _selectedGenres = MutableStateFlow<List<GenreDomain>>(emptyList())
    override val selectedGenres: StateFlow<List<GenreDomain>> get() = _selectedGenres.asStateFlow()

    override fun updateGenresFromNetwork(genres: List<GenreDomain>) {
        _genresFromNetwork = genres
    }

    override fun updateSelectedGenres(genres: List<GenreDomain>) {
        _selectedGenres.value = genres
    }
}