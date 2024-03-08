package com.example.filmsmatch.list

import com.example.data.MovieDomain
import com.example.filmsmatch.base.BaseState

sealed class FilmListState : BaseState(){
    object Loading : FilmListState()
    data class Success(
        val films: List<MovieDomain>, // Список доступных фильмов
        val currentPage: Int, // Текущая страница в списке фильмов
        val totalPage: Int, // Максимальная страница в списке фильмов
        val sortingOrder: String, // Порядок сортировки
    ) : FilmListState()
    data class Edge(val message: String) : FilmListState()
    data class Error(val errorMessage: String) : FilmListState()
}