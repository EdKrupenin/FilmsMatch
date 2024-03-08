package com.example.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class GenreCacheManager @Inject constructor() {
    // Кэш жанров, представленный в виде MutableStateFlow
    private val _genreCache = MutableStateFlow(GenreData(emptyList(), emptyList(), null))

    // Публичный доступ к кэшу жанров через StateFlow
    val genreCache: StateFlow<GenreData> get() = _genreCache.asStateFlow()

    /**
     * Обновляет список выбранных жанров в кэше.
     * @param genres список выбранных жанров для обновления.
     */
    fun updateSelectedGenres(genres: List<GenreDomain>) {
        _genreCache.value = _genreCache.value.copy(selectedGenres = genres)
    }

    /**
     * Функция для обновления данных о жанрах из сети
     * @param updateList: List<GenreDomain> - список жанров из сети
     * @param order - вариант сортировки по умолчанию
     */
    fun updateGenreData(updateList: List<GenreDomain>, order: SortingOption) {
        _genreCache.value = _genreCache.value.copy(genresFromNetwork = updateList, selectedOrder = order)
    }

    /**
     * Функция для обновления выбранного варианта сортировки
     * @param order - выбранный вариант сортировки
     */
    fun updateSelectedOrder(order: SortingOption) {
        _genreCache.value = _genreCache.value.copy(selectedOrder = order)
    }
}


/**
 * Data class representing a set of genres, including genres obtained from the network
 * and genres selected by the user.
 *
 * @property genresFromNetwork List of genres obtained from the network.
 * @property selectedGenres List of genres selected by the user.
 */
data class GenreData(
    val genresFromNetwork: List<GenreDomain>,
    val selectedGenres: List<GenreDomain>,
    val selectedOrder: SortingOption?
)
