package com.example.filmsmatch.genre

import com.example.domain.model.GenreDomain
import com.example.domain.model.SortingOptionDomain
import com.example.filmsmatch.base.BaseState
import com.example.filmsmatch.base.ErrorType

sealed class GenreSelectionState : BaseState() {
    data object Loading : GenreSelectionState()
    data class Loaded(
        val genres: List<GenreDomain>, // Список доступных жанров
        val selectedGenres: List<GenreDomain>, // Список выбранных жанров
        val sortingOrder: SortingOptionDomain, // Порядок сортировки
        val accessNextButton: Boolean = false,
        val accessSortingButton: Boolean = false,
        val sortingOptionDomains: List<SortingOptionDomain> // Список всех вариантов сортировки
    ) : GenreSelectionState()

    data class Error(val errorType: ErrorType, val retryAction: Boolean) : GenreSelectionState()
}