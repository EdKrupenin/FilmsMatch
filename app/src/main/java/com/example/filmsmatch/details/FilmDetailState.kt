package com.example.filmsmatch.details

import com.example.data.MovieDetailsDomain
import com.example.filmsmatch.base.BaseState
import com.example.filmsmatch.base.ErrorType

sealed class FilmDetailState : BaseState() {
    data object Loading : FilmDetailState()

    data class Success(val movieDetails: MovieDetailsDomain) : FilmDetailState()
    data class Error(val errorType: ErrorType, val retryAction: Boolean) : FilmDetailState()
}