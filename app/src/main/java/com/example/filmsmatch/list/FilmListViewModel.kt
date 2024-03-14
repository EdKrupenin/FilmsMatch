package com.example.filmsmatch.list

import androidx.lifecycle.viewModelScope
import com.example.data.MovieData
import com.example.data.MovieDomain
import com.example.domain.FilmsMatchError
import com.example.domain.MovieRepository
import com.example.filmsmatch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmListViewModel @Inject constructor(
    private val repository: MovieRepository,
) : BaseViewModel<FilmListState>(FilmListState.Loading) {

    private var currentPage: Int = 1

    init {
        loadMovie()
    }

    private fun loadMovie(page: Int = currentPage) {
        viewModelScope.launch {
            setState(FilmListState.Loading)
            val result = repository.getMovie(page)
            handleMovieResult(result, isNextPage = page > currentPage)
        }
    }

    fun loadNextPage() {
        loadMovie(currentPage + 1)
    }

    fun removeFilm(film: MovieDomain) {
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

    fun saveFilmId(film: MovieDomain) {
        viewModelScope.launch {
            if (stateFlow.value is FilmListState.Success) {
                val currentList = (stateFlow.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                // TODO SAVE ID in cashe - Tinder BO
                setState(
                    (stateFlow.value as FilmListState.Success).copy(films = currentList)
                )
            }
        }
    }

    private fun handleMovieResult(result: Result<MovieData>, isNextPage: Boolean) {
        result.onSuccess { movieList ->
            currentPage = movieList.currentPage
            val updatedList = if (isNextPage) {
                (stateFlow.value as? FilmListState.Success)?.films.orEmpty().toMutableList().apply {
                    addAll(movieList.movies)
                }
            } else {
                movieList.movies
            }
            setState(FilmListState.Success(updatedList))
        }.onFailure { error ->
            val errorMessage = when (error) {
                is FilmsMatchError.EmptyResponse -> "No more films in this genre"
                is FilmsMatchError.BadRequest -> "Select too match genre"
                is FilmsMatchError.NetworkError -> "Network error"
                else -> "Unknown error"
            }
            setState(FilmListState.Error(errorMessage, error is FilmsMatchError.NetworkError))
        }
    }


}