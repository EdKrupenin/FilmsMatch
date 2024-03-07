package com.example.filmsmatch.list

import com.example.data.GenreDomain
import com.example.data.MovieDomain

sealed class FilmListState {
    object Loading : FilmListState()
    data class Success(
        val films: List<MovieDomain>, // Список доступных фильмов
        val currentPage: Int, // Текущая страница в списке фильмов
        val sortingOrder: String, // Порядок сортировки
    ) : FilmListState()
    data class Error(val errorMessage: String) : FilmListState()
}