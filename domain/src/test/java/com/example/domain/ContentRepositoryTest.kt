package com.example.domain

import com.example.data.GenreCacheManager
import com.example.data.GenreData
import com.example.data.GenreDomain
import com.example.data.MovieCacheManager
import com.example.data.FilmsListDomain
import com.example.data.SortingOption
import com.example.data.network.genre.GenresApiService
import com.example.data.network.genre.GenresResponse
import com.example.data.network.movie.FilmDetailsApiService
import com.example.data.network.movie.FilmLinksApiService
import com.example.data.network.movie.FilmsListByGenresApiService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ContentRepositoryTest {

    @MockK
    private lateinit var genreCacheManager: GenreCacheManager

    @MockK
    private lateinit var movieCacheManager: MovieCacheManager

    @MockK
    private lateinit var getGenreService: GenresApiService

    @MockK
    private lateinit var getFilmsService: FilmsListByGenresApiService

    @MockK
    private lateinit var getFilmDetailsService: FilmDetailsApiService

    @MockK
    private lateinit var getFilmLinksService: FilmLinksApiService

    private lateinit var contentRepository: ContentRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Инициализация тестируемого класса с мокированными зависимостями
        contentRepository = ContentRepository(
            genreCacheManager,
            movieCacheManager,
            getGenreService,
            getFilmsService,
            getFilmDetailsService,
            getFilmLinksService
        )
    }

    @Test
    fun `getGenres returns cached genres when available`() {
        val cachedGenres = listOf(GenreDomain("1", "Action"))
        every { genreCacheManager.genreCache.value.genresFromNetwork } returns cachedGenres

        val result = runBlocking { contentRepository.getGenres() }

        assertTrue(result.isSuccess)
        assertEquals(cachedGenres, result.getOrNull())
        verify(exactly = 0) { runBlocking { getGenreService.getGenres() } }
    }

    @Test
    fun `getGenres returns empty response error when API returns empty list`() {
        every { genreCacheManager.genreCache } returns MutableStateFlow(
            GenreData(
                emptyList(),
                emptyList(), SortingOption("RATING", "RATING")
            )
        )
        coEvery { getGenreService.getGenres() } returns Response.success(GenresResponse(emptyList()))
        coEvery { genreCacheManager.updateGenreData(emptyList()) } just Runs

        val result = runBlocking { contentRepository.getGenres() }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is FilmsMatchError.EmptyResponse)
    }


    @Test
    fun `getGenres returns network error when service fails`() {
        every { genreCacheManager.genreCache } returns MutableStateFlow(
            GenreData(
                emptyList(),
                emptyList(), SortingOption("RATING", "RATING")
            )
        )
        coEvery { genreCacheManager.updateGenreData(emptyList()) } just Runs
        coEvery { getGenreService.getGenres() } throws FilmsMatchError.NetworkError

        val result = runBlocking { contentRepository.getGenres() }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.NetworkError)
    }

    //@Test
    /*fun `getFilms updates cache when new data is fetched`() {
        val newMovies = listOf(FilmApiResponse(2, nameRu = "Movie 2"))
        every {
            runBlocking {
                getFilmsService.getMoviesByGenres(
                    genres = any(), order = any(), page = any()
                )
            }
        } returns Response.success(
            FilmsListApiResponse(1, 1, newMovies)
        )
        every { movieCacheManager.getMoviesByPage(any()) } returns emptyList()

        runBlocking { contentRepository.getFilms(1) }

        verify { movieCacheManager.updateMovieData(newMovies, 1, 1, any(), any()) }
    }*/

    @Test
    fun `getFilms throws error when page number is greater than total pages`() {
        val genreData = GenreData(
            genresFromNetwork = listOf(GenreDomain("1", "Action")),
            selectedGenres = listOf(GenreDomain("1", "Action")),
            selectedOrder = SortingOption("RATING", "RATING")
        )
        every { genreCacheManager.genreCache } returns MutableStateFlow(genreData)
        every { movieCacheManager.movieCache } returns MutableStateFlow(
            FilmsListDomain(
                filmsList = emptyList(),
                currentPage = 1,
                totalPages = 1,
                currentGenres = "2",
                currentOrder = "RATING"
            )
        )

        val result = runBlocking { contentRepository.getFilms(2) }

        // Добавляем логирование
        println("Result: $result")
        println("Exception: ${result.exceptionOrNull()}")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is FilmsMatchError.EmptyResponse)
    }

    /*@Test
    fun `getFilmDetails converts response to domain object`() {
        val movieDetailsResponse = FilmDetailApiResponse(
            "1", "Movie 1", "Description", null, null, null, countries = emptyList()
        )
        coEvery { getFilmDetailsService.getMovieDetails(any()) } returns Response.success(
            movieDetailsResponse
        )

        val result = runBlocking { contentRepository.getFilmDetails(1) }

        assertTrue(result.isSuccess)
        assertEquals(
            FilmDetailsDomain("1", "Movie 1", "Description", "", "", "", emptyList()),
            result.getOrNull()
        )
    }*/

    // Дополнительные тесты для getFilms и getFilmDetails
}
