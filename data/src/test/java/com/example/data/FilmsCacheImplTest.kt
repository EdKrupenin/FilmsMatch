package com.example.data

import com.example.data.cache.FilmsCacheImpl
import com.example.domain.cache.IFilmsCache
import com.example.domain.model.FilmDetailsDomain
import com.example.domain.model.FilmDomain
import com.example.domain.model.FilmLinkDomain
import com.example.domain.model.FilmsListDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class FilmsCacheImplTest {

    private lateinit var filmsCache: IFilmsCache

    @BeforeEach
    fun setUp() {
        filmsCache = FilmsCacheImpl()
    }


    @Test
    fun `updateFilmsListData should update films list correctly`(): Unit = runTest {
        val expectedFilmsList = FilmsListDomain(
            filmsList = listOf(
                FilmDomain(
                    kinopoiskId = 1,
                    imdbId = null,
                    nameRu = "nameRu",
                    nameEn = "nameEn",
                    nameOriginal = "nameOriginal",
                    countries = emptyList(),
                    genres = emptyList(),
                    ratingKinopoisk = null,
                    ratingImdb = null,
                    year = 2000,
                    type = "type",
                    posterUrl = null,
                    posterUrlPreview = null,
                    details = FilmDetailsDomain.DEFAULT,
                    emptyList()
                )
            ),
            currentPage = 1,
            totalPages = 1,
            currentGenres = "",
            currentOrder = ""
        )

        filmsCache.updateFilmsListData(expectedFilmsList)

        assertEquals(expectedFilmsList, filmsCache.filmsListCache)
    }

    @Test
    fun `updateFilmDetails should update film details correctly`() = runTest {
        val initialFilm = FilmDomain(
            kinopoiskId = 1,
            imdbId = null,
            nameRu = "nameRu",
            nameEn = "nameEn",
            nameOriginal = "nameOriginal",
            countries = emptyList(),
            genres = emptyList(),
            ratingKinopoisk = null,
            ratingImdb = null,
            year = 2000,
            type = "type",
            posterUrl = null,
            posterUrlPreview = null,
            details = FilmDetailsDomain.DEFAULT,
            emptyList()
        )
        val expectedFilmDetails = FilmDetailsDomain.DEFAULT.copy(description = "Test Description")
        filmsCache.updateFilmsListData(
            FilmsListDomain(
                filmsList = listOf(initialFilm),
                currentPage = 1,
                totalPages = 1,
                currentGenres = "",
                currentOrder = ""
            )
        )

        filmsCache.updateFilmDetails(kinopoiskId = 1, data = expectedFilmDetails)

        val updatedFilm = filmsCache.getFilmDetails(kinopoiskId = 1)
        assertEquals(expectedFilmDetails, updatedFilm)
    }

    @Test
    fun `updateFilmLinks should update film links correctly`() = runTest {
        // Подготовка
        val initialFilm = FilmDomain(
            kinopoiskId = 1,
            imdbId = null,
            nameRu = "nameRu",
            nameEn = "nameEn",
            nameOriginal = "nameOriginal",
            countries = emptyList(),
            genres = emptyList(),
            ratingKinopoisk = null,
            ratingImdb = null,
            year = 2000,
            type = "type",
            posterUrl = null,
            posterUrlPreview = null,
            details = FilmDetailsDomain.DEFAULT,
            emptyList()
        )
        val expectedFilmLinks = listOf(
            FilmLinkDomain(
                platformIconUrl = "",
                platformName = "Test",
                platformLink = "http://example.com"
            )
        )
        filmsCache.updateFilmsListData(
            FilmsListDomain(
                filmsList = listOf(initialFilm),
                currentPage = 1,
                totalPages = 1,
                currentGenres = "",
                currentOrder = ""
            )
        )

        // Действие
        filmsCache.updateFilmLinks(kinopoiskId = 1, data = expectedFilmLinks)

        // Проверка
        val updatedFilmLinks = filmsCache.getFilmLinks(kinopoiskId = 1)
        assertEquals(expectedFilmLinks, updatedFilmLinks)
    }

    @Test
    fun `updateFilmDetails with non-existing kinopoiskId should not modify cache`() = runTest {
        // Подготовка
        val nonExistingKinopoiskId = 999 // ID, которого нет в кеше
        val filmDetails = FilmDetailsDomain.DEFAULT.copy(description = "Test Description")
        // Предполагаем, что в кеше уже есть какие-то данные
        val initialFilmsList = FilmsListDomain(
            filmsList = listOf(),
            currentPage = 1,
            totalPages = 1,
            currentGenres = "",
            currentOrder = ""
        )
        filmsCache.updateFilmsListData(initialFilmsList)

        filmsCache.updateFilmDetails(kinopoiskId = nonExistingKinopoiskId, data = filmDetails)

        val result =
            filmsCache.filmsListCache.filmsList.find { it.kinopoiskId == nonExistingKinopoiskId }
        assertTrue(
            result == null,
            "Фильм с несуществующим kinopoiskId не должен был быть обновлен."
        )
    }

    @Test
    fun `updateFilmLinks with non-existing kinopoiskId should not modify cache`() = runTest {
        val nonExistingKinopoiskId = 999
        val filmLinks = listOf(
            FilmLinkDomain(
                platformIconUrl = "",
                platformName = "Test",
                platformLink = "http://example.com"
            )
        )

        val initialFilmsList = FilmsListDomain(
            filmsList = listOf(),
            currentPage = 1,
            totalPages = 1,
            currentGenres = "",
            currentOrder = ""
        )
        filmsCache.updateFilmsListData(initialFilmsList)

        filmsCache.updateFilmLinks(kinopoiskId = nonExistingKinopoiskId, data = filmLinks)

        val result =
            filmsCache.filmsListCache.filmsList.find { it.kinopoiskId == nonExistingKinopoiskId }
        assertTrue(
            result == null,
            "Ссылки для фильма с несуществующим kinopoiskId не должны были быть обновлены."
        )
    }
}