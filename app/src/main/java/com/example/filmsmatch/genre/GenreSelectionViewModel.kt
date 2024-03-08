package com.example.filmsmatch.genre

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.data.GenreDomain
import com.example.domain.GenreRepository
import com.example.domain.SortingOptionsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val genreCacheManager: GenreCacheManager,
    private val repository: GenreRepository,
    private val sortingOptionsProvider: SortingOptionsProvider,
) : ViewModel() {

    private val _genreSelectionState =
        MutableStateFlow<GenreSelectionState>(GenreSelectionState.Loading)
    val genreSelectionState: StateFlow<GenreSelectionState> = _genreSelectionState

    init {
        loadGenres()
    }

    // Функция для загрузки списка доступных жанров
    fun loadGenres() {
        viewModelScope.launch {
            _genreSelectionState.value =
                GenreSelectionState.Loading // Перед загрузкой показываем состояние загрузки
            try {
                val genresFromNetwork =
                    genreCacheManager.genreCache.value.genresFromNetwork // Получаем данные из кэша
                val selectedGenres =
                    genreCacheManager.genreCache.value.selectedGenres // Получаем данные из кэша
                if (genresFromNetwork.isNotEmpty()) {
                    // Если данные есть в кэше, используем их
                    _genreSelectionState.value = GenreSelectionState.Loaded(
                        genresFromNetwork,
                        selectedGenres,
                        sortingOptionsProvider.sortingOptions.first(),
                        selectedGenres.isNotEmpty(),
                        true,
                        sortingOptionsProvider.sortingOptions
                    )
                } else {
                    // Если данных в кэше нет, загружаем их из репозитория
                    val genres = repository.getGenres()
                    val order = genreCacheManager.genreCache.value.selectedOrder
                        ?: sortingOptionsProvider.sortingOptions.first()
                    // Обновляем данные в genreCacheManager
                    genreCacheManager.updateGenreData(genres, order)
                    _genreSelectionState.value = GenreSelectionState.Loaded(
                        genreCacheManager.genreCache.value.genresFromNetwork,
                        selectedGenres,
                        sortingOptionsProvider.sortingOptions.first(),
                        selectedGenres.isNotEmpty(),
                        true,
                        sortingOptionsProvider.sortingOptions
                    )
                }
            } catch (e: Exception) {
                e.message?.let { Log.d("GenreSelectionViewModel", it) }
                _genreSelectionState.value =
                    GenreSelectionState.Error("Failed to load genres: ${e.message}")
            }
        }
    }

    fun updateSelectedOrder(position: Int) {
        viewModelScope.launch {
            val currentState = _genreSelectionState.value
            if (currentState is GenreSelectionState.Loaded) {
                val updatedSelectedItem = currentState.sortingOptions[position]
                val newState = currentState.copy(sortingOrder = updatedSelectedItem)
                _genreSelectionState.value = newState
                // Обновляем genreCacheManager
                genreCacheManager.updateSelectedOrder(updatedSelectedItem)
                _genreSelectionState.value = newState
            }
        }
    }

    // Функция для обновления состояния и genreCacheManager при выборе или отмене выбора чипов
    fun updateSelectedGenres(genre: GenreDomain, isSelected: Boolean) {
        viewModelScope.launch {
            val currentState = _genreSelectionState.value
            if (currentState is GenreSelectionState.Loaded) {
                val updatedSelectedGenres = if (isSelected) {
                    currentState.selectedGenres + genre
                } else {
                    currentState.selectedGenres - genre
                }
                val updatedState = currentState.copy(selectedGenres = updatedSelectedGenres)
                _genreSelectionState.value = updatedState

                // Обновляем genreCacheManager
                genreCacheManager.updateSelectedGenres(updatedSelectedGenres)

                // Обновляем состояние доступности кнопки в зависимости от измененного списка выбранных жанров
                val isButtonEnabled =
                    updatedSelectedGenres.isNotEmpty() // Например, кнопка доступна, если есть выбранные жанры
                val newState = updatedState.copy(accessNextButton = isButtonEnabled)
                _genreSelectionState.value = newState
            }
        }
    }
}
