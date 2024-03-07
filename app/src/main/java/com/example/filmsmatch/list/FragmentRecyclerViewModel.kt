package com.example.filmsmatch.list

import MovieRepositoryImpl
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.data.MovieCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ORDER_RATING = "RATING"
@HiltViewModel
class FragmentRecyclerViewModel @Inject constructor(
    private val genreCacheManager: GenreCacheManager,
    private val movieCacheManager: MovieCacheManager,
    private val repository: MovieRepositoryImpl,
) : ViewModel() {
    private val _filmListState =
        MutableStateFlow<FilmListState>(FilmListState.Loading)
    val filmListState: StateFlow<FilmListState> = _filmListState

    init {
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _filmListState.value =
                FilmListState.Loading // Перед загрузкой показываем состояние загрузки
            try {
                // Получаем id жанра из кэша
                val selectedGenres = genreCacheManager.genreCache.value.selectedGenres
                if (selectedGenres.isNotEmpty()) {
                    val genresString = selectedGenres.joinToString(",") { it.id }
                    // Получаем текущий номер страницы из кэша фильмов, если он есть, иначе используем 1
                    val currentPage = movieCacheManager.movieCache.value.currentPage
                    // Получаем список фильмов с текущей страницы и обновляем кэш
                    val moviePage = repository.getMovie(genresString, ORDER_RATING, currentPage)
                    movieCacheManager.updateMovieData(moviePage.movies, moviePage.currentPage)
                    _filmListState.value = FilmListState.Success(moviePage.movies, moviePage.currentPage, ORDER_RATING)
                } else {
                    _filmListState.value = FilmListState.Error("No selected genres")
                }

            } catch (e: Exception) {
                e.message?.let { Log.d("FragmentRecyclerViewModel", it) }
                _filmListState.value =
                    FilmListState.Error("Failed to load movie: ${e.message}")
            }
        }
    }
}