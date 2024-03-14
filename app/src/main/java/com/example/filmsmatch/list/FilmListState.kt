package com.example.filmsmatch.list

import com.example.data.MovieDomain
import com.example.filmsmatch.base.BaseState

sealed class FilmListState : BaseState() {
    data object Loading : FilmListState()
    data class Success(
        val films: List<MovieDomain>, // Список доступных фильмов
    ) : FilmListState()
    data class Error(val errorMessage: String, val retryAction: Boolean) : FilmListState()
}