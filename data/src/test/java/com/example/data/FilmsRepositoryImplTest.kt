package com.example.data

import com.example.data.network.movie.Country
import com.example.data.network.movie.FilmApiResponse
import com.example.data.network.movie.FilmDetailApiResponse
import com.example.data.network.movie.FilmLinkApiResponse
import com.example.data.network.movie.FilmLinksListApiResponse
import com.example.data.network.movie.FilmsListApiResponse
import com.example.data.network.movie.IFilmDetailsApiService
import com.example.data.network.movie.IFilmLinksApiService
import com.example.data.network.movie.IFilmsListByGenresApiService
import com.example.data.repository.FilmsRepositoryImpl
import com.example.domain.FilmsMatchError
import com.example.domain.cache.IContentCache
import com.example.domain.cache.IFilmsCache
import com.example.domain.cache.IGenreCache
import com.example.domain.cache.ISortingOptionsCache
import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain
import com.example.domain.model.GenreDomain
import com.example.domain.model.SortingOptionDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import retrofit2.Response
import java.util.Calendar

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class FilmsRepositoryImplTest {

    @Mock
    lateinit var getFilmsService: IFilmsListByGenresApiService

    @Mock
    lateinit var getFilmDetailsService: IFilmDetailsApiService

    @Mock
    lateinit var getFilmLinksService: IFilmLinksApiService

    @Mock
    lateinit var contentCache: IContentCache

    @Mock
    lateinit var filmsCache: IFilmsCache

    @Mock
    lateinit var genreCacheMock: IGenreCache

    @InjectMocks
    private lateinit var filmsRepository: FilmsRepositoryImpl

    private val genreFlow = MutableStateFlow(listOf(GenreDomain("Drama", "1")))

    @BeforeEach
    fun setUp() {
        Mockito.lenient().`when`(genreCacheMock.selectedGenres).thenReturn(genreFlow)
        Mockito.lenient().`when`(contentCache.genreCache).thenReturn(genreCacheMock)
        val sortingOptionsCacheMock = mock(ISortingOptionsCache::class.java)
        Mockito.lenient().`when`(sortingOptionsCacheMock.selectedSortingOption)
            .thenReturn(SortingOptionDomain.DEFAULT)
        Mockito.lenient().`when`(contentCache.sortingOptionsCache)
            .thenReturn(sortingOptionsCacheMock)
        Mockito.lenient().`when`(filmsCache.filmsListCache).thenReturn(FilmsListDomain.DEFAULT)
        Mockito.lenient().`when`(contentCache.filmsCache).thenReturn(filmsCache)
    }

    private fun setupFilmsApiResponse(page: Int = 1): FilmsListApiResponse {
        return FilmsListApiResponse(
            page, page, listOf(
                FilmApiResponse(
                    kinopoiskId = 1,
                    imdbId = null,
                    nameRu = "name",
                    nameEn = null,
                    nameOriginal = "name",
                    countries = listOf(),
                    genres = listOf(),
                    ratingKinopoisk = 0.0,
                    ratingImdb = null,
                    year = 2000,
                    type = "FILM",
                    posterUrl = "null",
                    posterUrlPreview = "null",
                )
            )
        )
    }

    private fun assertSuccessResultWithContent(expectedContent: Any, actualResult: Result<*>) {
        assertTrue { actualResult.isSuccess }
        assertEquals(expectedContent, actualResult.getOrNull())
    }

    private fun setupCacheForGetFilms(
        page: Int = 1,
        genresString: String = "1",
        orderString: String = "RATING",
        details: FilmDetailsDomain = FilmDetailsDomain.DEFAULT,
        links: List<FilmLinkDomain> = emptyList(),
    ) {
        Mockito.lenient().`when`(filmsCache.filmsListCache).thenReturn(
            FilmsListDomain.DEFAULT.copy(
                listOf(
                    FilmDomain(
                        kinopoiskId = 1,
                        imdbId = null,
                        nameRu = "name",
                        nameEn = null,
                        nameOriginal = "name",
                        countries = listOf(),
                        genres = listOf(),
                        ratingKinopoisk = 0.0,
                        ratingImdb = null,
                        year = 2000,
                        type = "FILM",
                        posterUrl = "null",
                        posterUrlPreview = "null",
                        details = details,
                        links = links
                    )
                ),
                currentPage = page,
                totalPages = page,
                currentGenres = genresString,
                currentOrder = orderString
            )
        )
    }

    @Test
    fun `getFilms fetches from network when cache is empty and updates cache`() = runTest {
        val genresString = "1"
        val orderString = "RATING"
        val page = 1
        val filmsApiResponse = setupFilmsApiResponse()
        `when`(
            getFilmsService.getFilmListByGenres(
                genres = genresString,
                order = orderString,
                yearTo = Calendar.getInstance().get(Calendar.YEAR),
                page = page
            )
        ).thenReturn(Response.success(filmsApiResponse))

        val result = filmsRepository.getFilms(page)

        val expectedFilmsListDomain = filmsApiResponse.toFilmsListData()
        assertSuccessResultWithContent(expectedFilmsListDomain, result)
    }

    @Test
    fun `getFilms returns data from cache when available`() = runTest {
        val page = 1
        val genresString = "1"
        val orderString = "RATING"
        setupCacheForGetFilms(page, genresString, orderString)
        val result = filmsRepository.getFilms(page)
        verify(getFilmsService, never()).getFilmListByGenres(
            genres = orderString,
            order = genresString,
            yearTo = Calendar.getInstance().get(Calendar.YEAR),
            page = page
        )

        assertSuccessResultWithContent(contentCache.filmsCache.filmsListCache, result)

        verify(getFilmsService, never()).getFilmListByGenres(
            genres = orderString,
            order = genresString,
            yearTo = Calendar.getInstance().get(Calendar.YEAR),
            page = page
        )
    }

    @Test
    fun `getFilms returns BadRequest when genresString is empty`() = runTest {
        val page = 1
        `when`(genreCacheMock.selectedGenres).thenReturn(
            MutableStateFlow(emptyList())
        )
        val result = filmsRepository.getFilms(page)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.BadRequest)
    }

    @Test
    fun `getFilms returns EmptyResponse when page is greater than totalPages`() = runTest {
        val page = 10
        val result = filmsRepository.getFilms(page)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.EmptyResponse)
    }

    @Test
    fun `getFilmLinks returns links from cache if available`() = runTest {
        val kinopoiskId = 1
        val expectedLinks = listOf(
            FilmLinkDomain("", "Netflix", "http://netflix.com/movie"),
            FilmLinkDomain("", "Hulu", "http://hulu.com/movie")
        )
        setupCacheForGetFilms(links = expectedLinks)
        `when`(filmsCache.getFilmLinks(kinopoiskId)).thenReturn(
            expectedLinks
        )

        val result = filmsRepository.getFilmLinks(kinopoiskId)

        verify(
            getFilmLinksService,
            never()
        ).getFilmLinks(kinopoiskId)
        assertSuccessResultWithContent(expectedLinks, result)
    }

    @Test
    fun `getFilmLinks fetches from network and updates cache when cache is empty`() = runTest {
        val kinopoiskId = 1
        val linksApiResponse = listOf(
            FilmLinkApiResponse("", "Netflix", "http://netflix.com/movie456"),
            FilmLinkApiResponse("", "Hulu", "http://hulu.com/movie456")
        )
        val expectedLinks = linksApiResponse.map { it.toFilmLinkDomain() }
        setupCacheForGetFilms()
        `when`(filmsCache.getFilmLinks(kinopoiskId)).thenReturn(
            emptyList()
        )
        `when`(getFilmLinksService.getFilmLinks(kinopoiskId)).thenReturn(
            Response.success(
                FilmLinksListApiResponse(1, 1, linksApiResponse)
            )
        )

        val result = filmsRepository.getFilmLinks(kinopoiskId)

        verify(filmsCache).updateFilmLinks(kinopoiskId, expectedLinks)
        assertSuccessResultWithContent(expectedLinks, result)
    }

    @Test
    fun `getFilmDetails returns details from cache if present`() = runTest {
        val kinopoiskId = 1
        val expectedFilmDetails = FilmDetailsDomain(
            "SomeId",
            "Slogan",
            "Description",
            "PG-13",
            "Short desc",
            "12+",
            listOf("USA")
        )
        setupCacheForGetFilms(details = expectedFilmDetails)
        `when`(filmsCache.getFilmDetails(kinopoiskId)).thenReturn(
            expectedFilmDetails
        )

        val result = filmsRepository.getFilmDetails(kinopoiskId)

        verify(getFilmDetailsService, never()).getFilmDetails(kinopoiskId)
        assertSuccessResultWithContent(expectedFilmDetails, result)
    }

    @Test
    fun `getFilmDetails fetches from network and updates cache when cache is empty`() = runTest {
        val kinopoiskId = 1
        val filmDetailsApiResponse = FilmDetailApiResponse(
            "SomeId",
            "Slogan",
            "Description",
            "PG-13",
            "Short desc",
            "12+",
            listOf(Country("USA"))
        )
        val expectedFilmDetails = filmDetailsApiResponse.toFilmDetailsDomain()
        setupCacheForGetFilms()
        `when`(filmsCache.getFilmDetails(kinopoiskId)).thenReturn(
            FilmDetailsDomain.DEFAULT
        )
        `when`(getFilmDetailsService.getFilmDetails(kinopoiskId)).thenReturn(
            Response.success(
                filmDetailsApiResponse
            )
        )

        val result = filmsRepository.getFilmDetails(kinopoiskId)

        verify(filmsCache).updateFilmDetails(
            kinopoiskId,
            expectedFilmDetails
        )
        assertSuccessResultWithContent(expectedFilmDetails, result)
    }

    @Test
    fun `getFilmDetails returns isEmptyResponse when network response is empty`() = runTest {
        val kinopoiskId = 1
        setupCacheForGetFilms()

        `when`(filmsCache.getFilmDetails(kinopoiskId)).thenReturn(
            FilmDetailsDomain.DEFAULT
        )
        `when`(getFilmDetailsService.getFilmDetails(kinopoiskId)).thenReturn(
            Response.success(
                FilmDetailApiResponse.EMPTY
            )
        )

        val result = filmsRepository.getFilmDetails(kinopoiskId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FilmsMatchError.EmptyResponse)
    }
}