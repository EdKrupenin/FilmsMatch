package com.example.filmsmatch.genre

import androidx.lifecycle.viewModelScope
import com.example.domain.FilmsMatchError
import com.example.domain.model.GenreDomain
import com.example.domain.repository.GenreRepository
import com.example.domain.repository.SortingOptionsRepository
import com.example.filmsmatch.base.BaseViewModel
import com.example.filmsmatch.base.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val genresRepository: GenreRepository,
    private val sortingOptionsRepository: SortingOptionsRepository,
) : BaseViewModel<GenreSelectionState>(GenreSelectionState.Loading) {

    init {
        loadGenres()
    }

    // Функция для загрузки списка доступных жанров
    fun loadGenres() {
        viewModelScope.launch {
            setState(GenreSelectionState.Loading) // Перед загрузкой показываем состояние загрузки
            val result = genresRepository.getGenres()
            result.onSuccess { genresFromNetwork ->
                val selectedGenres =
                    genresRepository.selectedGenres
                val order = sortingOptionsRepository.selectedSortingOption
                setState(
                    GenreSelectionState.Loaded(
                        genresFromNetwork,
                        selectedGenres,
                        order,
                        selectedGenres.isNotEmpty(),
                        true,
                        sortingOptionsRepository.sortingOptionDomains
                    )
                )
            }.onFailure { error ->
                val errorType = when (error) {
                    is FilmsMatchError.EmptyResponse -> ErrorType.EMPTY_RESPONSE
                    is FilmsMatchError.BadRequest -> ErrorType.BAD_REQUEST
                    is FilmsMatchError.NetworkError -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.UNKNOWN
                }
                setState(
                    GenreSelectionState.Error(
                        errorType,
                        error is FilmsMatchError.NetworkError
                    )
                )
            }
        }
    }

    fun updateSelectedOrder(position: Int) {
        viewModelScope.launch {
            val currentState = stateFlow.value
            if (currentState is GenreSelectionState.Loaded) {
                val updatedSelectedItem = currentState.sortingOptionDomains[position]
                val newState = currentState.copy(sortingOrder = updatedSelectedItem)
                sortingOptionsRepository.selectedSortingOption = updatedSelectedItem
                setState(newState)
            }
        }
    }

    // Функция для обновления состояния и genreCacheManager при выборе или отмене выбора чипов
    fun updateSelectedGenres(genre: GenreDomain, isSelected: Boolean) {
        viewModelScope.launch {
            val currentState = stateFlow.value
            if (currentState is GenreSelectionState.Loaded) {
                val updatedSelectedGenres = currentState.selectedGenres.toMutableList().apply {
                    if (isSelected) add(genre) else remove(genre)
                }
                genresRepository.selectedGenres = updatedSelectedGenres

                val newState = currentState.copy(
                    selectedGenres = updatedSelectedGenres,
                    accessNextButton = updatedSelectedGenres.isNotEmpty()
                )
                setState(newState)
            }
        }
    }
}
