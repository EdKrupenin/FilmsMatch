package com.example.filmsmatch.genre

import com.example.data.GenreDomain
import com.example.data.SortingOption
import com.example.filmsmatch.base.BaseState

sealed class GenreSelectionState : BaseState() {
    data object Loading : GenreSelectionState()
    data class Loaded(
        val genres: List<GenreDomain>, // Список доступных жанров
        val selectedGenres: List<GenreDomain>, // Список выбранных жанров
        val sortingOrder: SortingOption, // Порядок сортировки
        val accessNextButton: Boolean = false,
        val accessSortingButton: Boolean = false,
        val sortingOptions: List<SortingOption> // Список всех вариантов сортировки
    ) : GenreSelectionState()

    data class Error(val message: String, val retryAction: Boolean) : GenreSelectionState()
}