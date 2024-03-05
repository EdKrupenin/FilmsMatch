package com.example.filmsmatch.genre

import com.example.data.GenreDomain

sealed class GenreSelectionState {
    data object Loading : GenreSelectionState()
    data class Loaded(
        val genres: List<GenreDomain>, // Список доступных жанров
        val selectedGenres: List<GenreDomain>, // Список выбранных жанров
        val sortingOrder: String // Порядок сортировки
    ) : GenreSelectionState()

    data class Error(val message: String) : GenreSelectionState()
}