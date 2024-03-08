package com.example.filmsmatch.genre

import com.example.data.GenreDomain
import com.example.data.SortingOption

sealed class GenreSelectionState {
    data object Loading : GenreSelectionState()
    data class Loaded(
        val genres: List<GenreDomain>, // Список доступных жанров
        val selectedGenres: List<GenreDomain>, // Список выбранных жанров
        val sortingOrder: SortingOption, // Порядок сортировки
        val accessNextButton: Boolean = false,
        val accessSortingButton: Boolean = false,
        val sortingOptions: List<SortingOption> // Список всех вариантов сортировки
    ) : GenreSelectionState()

    data class Error(val message: String) : GenreSelectionState()
}