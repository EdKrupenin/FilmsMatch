package com.example.filmsmatch.list

import com.example.data.FilmDomain
import com.example.filmsmatch.base.BaseState
import com.example.filmsmatch.base.ErrorType

sealed class FilmListState : BaseState() {
    data object Loading : FilmListState()
    data class Success(
        val films: List<FilmDomain>,
    ) : FilmListState()

    data class Error(val errorType: ErrorType, val retryAction: Boolean) : FilmListState()
}