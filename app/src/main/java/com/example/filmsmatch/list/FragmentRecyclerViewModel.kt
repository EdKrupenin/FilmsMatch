package com.example.filmsmatch.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.data.MovieCacheManager
import com.example.data.MovieDomain
import com.example.domain.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ORDER_RATING = "RATING"

@HiltViewModel
class FragmentRecyclerViewModel @Inject constructor(
    genreCacheManager: GenreCacheManager,
    private val movieCacheManager: MovieCacheManager,
    private val repository: MovieRepository,
) : ViewModel() {
    private val _filmListState =
        MutableStateFlow<FilmListState>(FilmListState.Loading)

    // Получаем id жанра из кэша
    private val genresString: String =
        genreCacheManager.genreCache.value.selectedGenres.joinToString(",") { it.id }
    val filmListState: StateFlow<FilmListState> = _filmListState

    init {
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _filmListState.value =
                FilmListState.Loading // Перед загрузкой показываем состояние загрузки
            try {
                if (genresString.isNotEmpty()) {
                    // Получаем текущий номер страницы из кэша фильмов, если он есть, иначе используем 1
                    var currentPage = movieCacheManager.movieCache.value.currentPage ?: 1
                    if (currentPage <= 0) currentPage = 1
                    // Получаем список фильмов с текущей страницы и обновляем кэш
                    val moviePage = repository.getMovie(genresString, ORDER_RATING, currentPage)
                    movieCacheManager.updateMovieData(moviePage.movies, moviePage.currentPage)
                    _filmListState.value =
                        FilmListState.Success(
                            moviePage.movies,
                            moviePage.currentPage,
                            moviePage.totalPage,
                            ORDER_RATING
                        )
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

    fun removeFilm(film: MovieDomain) {
        viewModelScope.launch {
            if (_filmListState.value is FilmListState.Success) {
                val currentList =
                    (_filmListState.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                _filmListState.value =
                    (_filmListState.value as FilmListState.Success).copy(films = currentList)
            }
        }
    }

    fun saveFilmId(film: MovieDomain) {
        viewModelScope.launch {
            if (_filmListState.value is FilmListState.Success) {
                val currentList =
                    (_filmListState.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                // TODO SAVE ID in cashe - Tinder BO
                _filmListState.value =
                    (_filmListState.value as FilmListState.Success).copy(films = currentList)
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            // Проверяем, не достигли ли мы конца списка
            val currentState = _filmListState.value
            if (currentState is FilmListState.Success) {
                // Здесь определите логику для проверки, есть ли еще страницы для загрузки
                val nextPage = currentState.currentPage + 1
                if (nextPage > currentState.totalPage) {
                    _filmListState.value = FilmListState.Edge("No more films in this genres")
                    return@launch
                }
                _filmListState.value =
                    FilmListState.Loading // Перед загрузкой показываем состояние загрузки
                try {
                    val moviePage = repository.getMovie(genresString, ORDER_RATING, nextPage)
                    movieCacheManager.updateMovieData(moviePage.movies, moviePage.currentPage)
                    val updatedList = currentState.films.toMutableList().apply {
                        addAll(moviePage.movies)
                    }
                    _filmListState.value = FilmListState.Success(
                        updatedList,
                        moviePage.currentPage,
                        moviePage.totalPage,
                        ORDER_RATING
                    )
                } catch (e: Exception) {
                    e.message?.let { Log.d("FragmentRecyclerViewModel", it) }
                    _filmListState.value =
                        FilmListState.Error("Failed to load movie: ${e.message}")
                }
            }
        }
    }

}