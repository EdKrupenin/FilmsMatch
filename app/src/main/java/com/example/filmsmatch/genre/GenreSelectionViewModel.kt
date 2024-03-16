package com.example.filmsmatch.genre

import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.data.GenreDomain
import com.example.domain.FilmsMatchError
import com.example.domain.GenreRepository
import com.example.domain.SortingOptionsProvider
import com.example.filmsmatch.base.BaseViewModel
import com.example.filmsmatch.base.ErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val genreCacheManager: GenreCacheManager,
    private val repository: GenreRepository,
    private val sortingOptionsProvider: SortingOptionsProvider,
) : BaseViewModel<GenreSelectionState>(GenreSelectionState.Loading) {

    init {
        val defaultOrder = genreCacheManager.genreCache.value.selectedOrder
            ?: sortingOptionsProvider.sortingOptions.first()
        genreCacheManager.updateSelectedOrder(defaultOrder)
        loadGenres()
    }

    // Функция для загрузки списка доступных жанров
    fun loadGenres() {
        viewModelScope.launch {
            setState(GenreSelectionState.Loading) // Перед загрузкой показываем состояние загрузки
            val result = repository.getGenres()
            result.onSuccess { genresFromNetwork ->
                val selectedGenres =
                    genreCacheManager.genreCache.value.selectedGenres
                val order = genreCacheManager.genreCache.value.selectedOrder
                    ?: sortingOptionsProvider.sortingOptions.first()
                setState(
                    GenreSelectionState.Loaded(
                        genresFromNetwork,
                        selectedGenres,
                        order,
                        selectedGenres.isNotEmpty(),
                        true,
                        sortingOptionsProvider.sortingOptions
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
                val updatedSelectedItem = currentState.sortingOptions[position]
                val newState = currentState.copy(sortingOrder = updatedSelectedItem)
                // Обновляем genreCacheManager
                genreCacheManager.updateSelectedOrder(updatedSelectedItem)
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
                // Обновляем genreCacheManager
                genreCacheManager.updateSelectedGenres(updatedSelectedGenres)

                // Обновляем состояние с новым списком выбранных жанров
                val newState = currentState.copy(
                    selectedGenres = updatedSelectedGenres,
                    accessNextButton = updatedSelectedGenres.isNotEmpty()
                )
                setState(newState)
            }
        }
    }
}
