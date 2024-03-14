package com.example.filmsmatch.details

import androidx.lifecycle.viewModelScope
import com.example.domain.FilmsMatchError
import com.example.domain.MovieRepository
import com.example.filmsmatch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
) : BaseViewModel<FilmDetailState>(FilmDetailState.Loading) {
    private var _kinopoiskId: Int = 0
    val kinopoiskId: Int
        get() = _kinopoiskId

    fun loadMovieDetails(kinopoiskId: Int) {
        _kinopoiskId = kinopoiskId
        viewModelScope.launch {
            setState(FilmDetailState.Loading)
            val result = repository.getMovieDetails(kinopoiskId)
            result.onSuccess { movieDetails ->
                setState(FilmDetailState.Success(movieDetails))
            }.onFailure { error ->
                val errorMessage = when (error) {
                    is FilmsMatchError.EmptyResponse -> "No information about this picture"
                    is FilmsMatchError.BadRequest -> "Network error"
                    is FilmsMatchError.NetworkError -> "Network error"
                    else -> "Unknown error"
                }
                setState(FilmDetailState.Error(errorMessage, error is FilmsMatchError.NetworkError))
            }
        }
    }
}