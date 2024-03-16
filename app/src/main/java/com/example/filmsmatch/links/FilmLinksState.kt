package com.example.filmsmatch.links

import com.example.data.MovieLinkDomain
import com.example.filmsmatch.base.BaseState
import com.example.filmsmatch.base.ErrorType

sealed class FilmLinksState : BaseState() {
    data object Loading : FilmLinksState()
    data class Success(val movieLinks: List<MovieLinkDomain>) : FilmLinksState()
    data class Error(val errorType: ErrorType, val retryAction: Boolean) : FilmLinksState()
}
