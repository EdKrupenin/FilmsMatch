package com.example.filmsmatch.list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.data.MovieCacheManager
import com.example.data.MovieDomain
import com.example.domain.MovieRepository
import com.example.filmsmatch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmListViewModel @Inject constructor(
    genreCacheManager: GenreCacheManager,
    private val movieCacheManager: MovieCacheManager,
    private val repository: MovieRepository,
) : BaseViewModel<FilmListState>(FilmListState.Loading) {
    // Получаем id жанра из кэша
    private val genresString: String =
        genreCacheManager.genreCache.value.selectedGenres.joinToString(",") { it.id }
    private val orderString: String =
        genreCacheManager.genreCache.value.selectedOrder?.value.toString()


    init {
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            setState(
                FilmListState.Loading
            ) // Перед загрузкой показываем состояние загрузки
            try {
                if (genresString.isNotEmpty()) {
                    // Получаем текущий номер страницы из кэша фильмов, если он есть, иначе используем 1
                    var currentPage = movieCacheManager.movieCache.value.currentPage ?: 1
                    if (currentPage <= 0) currentPage = 1
                    // Получаем список фильмов с текущей страницы и обновляем кэш
                    val moviePage = repository.getMovie(genresString, orderString, currentPage)
                    movieCacheManager.updateMovieData(moviePage.movies, moviePage.currentPage)
                    setState(
                        FilmListState.Success(
                            moviePage.movies,
                            moviePage.currentPage,
                            moviePage.totalPage,
                            orderString
                        )
                    )
                } else {
                    setState(FilmListState.Error("No selected genres"))
                }

            } catch (e: Exception) {
                e.message?.let { Log.d("FragmentRecyclerViewModel", it) }
                setState(
                    FilmListState.Error("Failed to load movie: ${e.message}")
                )
            }
        }
    }

    fun removeFilm(film: MovieDomain) {
        viewModelScope.launch {
            if (stateFlow.value is FilmListState.Success) {
                val currentList =
                    (stateFlow.value as FilmListState.Success).films.toMutableList()
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
                val currentList =
                    (stateFlow.value as FilmListState.Success).films.toMutableList()
                currentList.remove(film)
                // TODO SAVE ID in cashe - Tinder BO
                setState(
                    (stateFlow.value as FilmListState.Success).copy(films = currentList)
                )
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            // Проверяем, не достигли ли мы конца списка
            val currentState = stateFlow.value
            if (currentState is FilmListState.Success) {
                // Здесь определите логику для проверки, есть ли еще страницы для загрузки
                val nextPage = currentState.currentPage + 1
                if (nextPage > currentState.totalPage) {
                    setState(
                        FilmListState.Edge("No more films in this genres")
                    )
                    return@launch
                }
                setState(
                    FilmListState.Loading
                ) // Перед загрузкой показываем состояние загрузки
                try {
                    val moviePage = repository.getMovie(genresString, orderString, nextPage)
                    movieCacheManager.updateMovieData(moviePage.movies, moviePage.currentPage)
                    val updatedList = currentState.films.toMutableList().apply {
                        addAll(moviePage.movies)
                    }
                    setState(
                        FilmListState.Success(
                            updatedList,
                            moviePage.currentPage,
                            moviePage.totalPage,
                            orderString
                        )
                    )
                } catch (e: Exception) {
                    e.message?.let { Log.d("FragmentRecyclerViewModel", it) }
                    setState(
                        FilmListState.Error("Failed to load movie: ${e.message}")
                    )
                }
            }
        }
    }

}