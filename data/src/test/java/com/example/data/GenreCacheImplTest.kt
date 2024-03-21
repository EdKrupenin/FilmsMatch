package com.example.data

import com.example.data.cache.GenreCacheImpl
import com.example.domain.cache.IGenreCache
import com.example.domain.model.GenreDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GenreCacheImplTest {

    private lateinit var genreCache: IGenreCache

    @BeforeEach
    fun setUp() {
        genreCache = GenreCacheImpl()
    }

    @Test
    fun `updateGenresFromNetwork should update genres from network correctly`() = runTest {
        val expectedGenres = listOf(GenreDomain("Comedy", "1"), GenreDomain("Drama", "2"))

        genreCache.updateGenresFromNetwork(expectedGenres)

        assertEquals(expectedGenres, genreCache.genresFromNetwork)
    }

    @Test
    fun `updateSelectedGenres should update selected genres correctly`() = runTest {
        val expectedSelectedGenres =
            listOf(GenreDomain("Action", "3"), GenreDomain("Thriller", "4"))

        genreCache.updateSelectedGenres(expectedSelectedGenres)

        assertEquals(expectedSelectedGenres, genreCache.selectedGenres.first())
    }
}
