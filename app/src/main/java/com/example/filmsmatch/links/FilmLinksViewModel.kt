package com.example.filmsmatch.links

import androidx.lifecycle.viewModelScope
import com.example.domain.FilmsMatchError
import com.example.domain.repository.FilmsRepository
import com.example.filmsmatch.base.BaseViewModel
import com.example.filmsmatch.base.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmLinksViewModel @Inject constructor(
    private val repository: FilmsRepository,
) : BaseViewModel<FilmLinksState>(FilmLinksState.Loading) {
    private var _kinopoiskId: Int = 0
    val kinopoiskId: Int
        get() = _kinopoiskId

    fun loadLinks(kinopoiskId: Int) {
        _kinopoiskId = kinopoiskId
        viewModelScope.launch {
            setState(FilmLinksState.Loading)
            val result = repository.getFilmLinks(kinopoiskId)
            result.onSuccess { movieLinks ->
                setState(FilmLinksState.Success(movieLinks))
            }.onFailure { error ->
                val errorType = when (error) {
                    is FilmsMatchError.EmptyResponse -> ErrorType.EMPTY_RESPONSE
                    is FilmsMatchError.NetworkError -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.UNKNOWN
                }
                setState(FilmLinksState.Error(errorType, error is FilmsMatchError.NetworkError))
            }
        }
    }
}
