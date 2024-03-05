import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GenreCacheManager
import com.example.domain.GenreRepository
import com.example.filmsmatch.genre.GenreSelectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val genreCacheManager: GenreCacheManager
    private val repository : GenreRepository
) : ViewModel() {

    private val _genreSelectionState = MutableStateFlow<GenreSelectionState>(GenreSelectionState.Loading)
    val genreSelectionState: StateFlow<GenreSelectionState> = _genreSelectionState

    // Функция для загрузки списка доступных жанров
    fun loadGenres() {
        viewModelScope.launch {
            _genreSelectionState.value = GenreSelectionState.Loading // Перед загрузкой показываем состояние загрузки
            try {
                val genres = repository.getGenres() // Метод для получения списка жанров из репозитория
                _genreSelectionState.value = GenreSelectionState.Loaded(genres, emptyList(), "Default")
            } catch (e: Exception) {
                _genreSelectionState.value = GenreSelectionState.Error("Failed to load genres: ${e.message}")
            }
        }
    }
}
