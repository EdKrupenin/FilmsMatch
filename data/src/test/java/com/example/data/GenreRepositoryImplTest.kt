package com.example.data

import com.example.data.network.genre.GenreApiResponse
import com.example.data.network.genre.GenresResponse
import com.example.data.network.genre.IGenresApiService
import com.example.data.repository.GenreRepositoryImpl
import com.example.domain.FilmsMatchError
import com.example.domain.cache.IGenreCache
import com.example.domain.model.GenreDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GenreRepositoryImplTest {

    @Mock
    lateinit var getGenreService: IGenresApiService

    @Mock
    lateinit var genreCache: IGenreCache

    private lateinit var genreRepositoryImpl: GenreRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        genreRepositoryImpl = GenreRepositoryImpl(getGenreService, genreCache)
    }

    @Test
    fun `when getGenres is called and cache is empty, should fetch from network and return genres`() =
        runTest {
            val genresApiResponse = GenresResponse(
                genres = listOf(
                    GenreApiResponse(name = "Comedy", id = 1),
                    GenreApiResponse(name = "Drama", id = 2)
                )
            )
            `when`(getGenreService.getGenres()).thenReturn(Response.success(genresApiResponse))
            `when`(genreCache.genresFromNetwork).thenReturn(emptyList())

            val result = genreRepositoryImpl.getGenres()

            assertEquals(
                expected = listOf(
                    GenreDomain(name = "Comedy", id = "1"),
                    GenreDomain(name = "Drama", id = "2")
                ),
                actual = result.getOrNull()
            )
        }

    @Test
    fun `when getGenres is called and cache is not empty, should return genres from cache`() =
        runTest {
            val cachedGenres = listOf(GenreDomain("Comedy", "1"), GenreDomain("Drama", "2"))
            `when`(genreCache.genresFromNetwork).thenReturn(cachedGenres)
            val result = genreRepositoryImpl.getGenres()
            assertTrue(result.isSuccess)
            assertEquals(cachedGenres, result.getOrNull())
            verify(getGenreService, never()).getGenres()
        }

    @Test
    fun `when getGenres is called and network request fails, should return error`() = runTest {
        `when`(genreCache.genresFromNetwork).thenReturn(emptyList())
        val errorMessage = "Network error"
        `when`(getGenreService.getGenres()).thenReturn(
            Response.error(
                404,
                errorMessage.toResponseBody()
            )
        )
        val result = genreRepositoryImpl.getGenres()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.NetworkError)
    }

    @Test
    fun `when getGenres is called and network request returns empty list, should return empty result`() =
        runTest {
            `when`(genreCache.genresFromNetwork).thenReturn(emptyList())
            val response = Response.success(GenresResponse(emptyList()))
            `when`(getGenreService.getGenres()).thenReturn(response)
            val result = genreRepositoryImpl.getGenres()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FilmsMatchError.EmptyResponse)
        }

    @Test
    fun `when getGenres is called bad network request fails, should return error`() = runTest {
        `when`(genreCache.genresFromNetwork).thenReturn(emptyList())
        val errorMessage = "Network error"
        `when`(getGenreService.getGenres()).thenReturn(
            Response.error(
                400,
                errorMessage.toResponseBody()
            )
        )
        val result = genreRepositoryImpl.getGenres()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.BadRequest)
    }

    @Test
    fun `get selectedGenres should return correct values from cache`() = runTest {
        val expectedSelectedGenres = listOf(GenreDomain("1", "Comedy"), GenreDomain("2", "Drama"))
        `when`(genreCache.selectedGenres).thenReturn(MutableStateFlow(expectedSelectedGenres))
        val actualSelectedGenres = genreRepositoryImpl.selectedGenres
        assertEquals(expectedSelectedGenres, actualSelectedGenres)
    }

    @Test
    fun `set selectedGenres should update cache with correct values`() = runTest {
        val newSelectedGenres = listOf(GenreDomain("3", "Action"), GenreDomain("4", "Thriller"))
        genreRepositoryImpl.selectedGenres = newSelectedGenres
        verify(genreCache).updateSelectedGenres(newSelectedGenres)
    }
}
