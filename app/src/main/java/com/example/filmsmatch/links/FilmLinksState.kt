package com.example.filmsmatch.links

import com.example.domain.model.FilmLinkDomain
import com.example.filmsmatch.base.BaseState
import com.example.filmsmatch.base.ErrorType

sealed class FilmLinksState : BaseState() {
    data object Loading : FilmLinksState()
    data class Success(val movieLinks: List<FilmLinkDomain>) : FilmLinksState()
    data class Error(val errorType: ErrorType, val retryAction: Boolean) : FilmLinksState()
}
