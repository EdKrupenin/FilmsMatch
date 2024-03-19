package com.example.filmsmatch.details

import androidx.lifecycle.viewModelScope
import com.example.domain.FilmsMatchError
import com.example.domain.repository.FilmsRepository
import com.example.filmsmatch.base.BaseViewModel
import com.example.filmsmatch.base.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    private val repository: FilmsRepository,
) : BaseViewModel<FilmDetailState>(FilmDetailState.Loading) {
    private var _kinopoiskId: Int = 0
    val kinopoiskId: Int
        get() = _kinopoiskId

    fun loadMovieDetails(kinopoiskId: Int) {
        _kinopoiskId = kinopoiskId
        viewModelScope.launch {
            setState(FilmDetailState.Loading)
            val result = repository.getFilmDetails(kinopoiskId)
            result.onSuccess { movieDetails ->
                setState(FilmDetailState.Success(movieDetails))
            }.onFailure { error ->
                val errorType = when (error) {
                    is FilmsMatchError.EmptyResponse -> ErrorType.EMPTY_RESPONSE
                    is FilmsMatchError.BadRequest -> ErrorType.BAD_REQUEST
                    is FilmsMatchError.NetworkError -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.UNKNOWN
                }
                setState(FilmDetailState.Error(errorType, error is FilmsMatchError.NetworkError))
            }
        }
    }
}