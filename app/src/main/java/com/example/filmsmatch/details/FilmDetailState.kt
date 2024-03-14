package com.example.filmsmatch.details

import com.example.data.MovieDetailsDomain
import com.example.filmsmatch.base.BaseState

sealed class FilmDetailState : BaseState() {
    object Loading : FilmDetailState()
    data class Success(val movieDetails: MovieDetailsDomain) : FilmDetailState()
    data class Error(val errorMessage: String, val retryAction: Boolean) : FilmDetailState()
}