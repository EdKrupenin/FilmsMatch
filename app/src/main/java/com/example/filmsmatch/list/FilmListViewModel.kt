package com.example.filmsmatch.list

import androidx.lifecycle.viewModelScope
import com.example.data.FilmsListDomain
import com.example.data.FilmDomain
import com.example.domain.FilmsMatchError
import com.example.domain.FilmsRepository
import com.example.filmsmatch.base.BaseViewModel
import com.example.filmsmatch.base.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmListViewModel @Inject constructor(
    private val repository: FilmsRepository,
) : BaseViewModel<FilmListState>(FilmListState.Loading) {

    private var currentPage: Int = 1

    init {
        loadMovie()
    }

    fun loadMovie(page: Int = currentPage) {
        viewModelScope.launch {
            setState(FilmListState.Loading)
            val result = repository.getFilms(page)
            handleMovieResult(result, isNextPage = page > currentPage)
        }
    }

    fun loadNextPage() {
        loadMovie(currentPage + 1)
    }

    fun removeFilm(film: FilmDomain) {
        viewModelScope.launch {
            if (stateFlow.value is FilmListState.Success) {
                val currentList = (stateFlow.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                setState(
                    (stateFlow.value as FilmListState.Success).copy(films = currentList)
                )
            }
        }
    }

    fun saveFilmId(film: FilmDomain) {
        viewModelScope.launch {
            if (stateFlow.value is FilmListState.Success) {
                val currentList = (stateFlow.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                // TODO SAVE ID in cache - Tinder BO
                setState(
                    (stateFlow.value as FilmListState.Success).copy(films = currentList)
                )
            }
        }
    }

    private fun handleMovieResult(result: Result<FilmsListDomain>, isNextPage: Boolean) {
        result.onSuccess { movieList ->
            currentPage = movieList.currentPage
            val updatedList = if (isNextPage) {
                (stateFlow.value as? FilmListState.Success)?.films.orEmpty().toMutableList().apply {
                    addAll(movieList.filmsList)
                }
            } else {
                movieList.filmsList
            }
            setState(FilmListState.Success(updatedList))
        }.onFailure { error ->
            val errorType = when (error) {
                is FilmsMatchError.EmptyResponse -> ErrorType.EMPTY_RESPONSE
                is FilmsMatchError.BadRequest -> ErrorType.BAD_REQUEST
                is FilmsMatchError.NetworkError -> ErrorType.NETWORK_ERROR
                else -> ErrorType.UNKNOWN
            }
            setState(FilmListState.Error(errorType, error is FilmsMatchError.NetworkError))
        }
    }


}