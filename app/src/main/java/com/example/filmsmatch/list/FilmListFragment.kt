package com.example.filmsmatch.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentRecyclerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val VISIBLE_THRESHOLD = 5
@AndroidEntryPoint
class FilmListFragment : Fragment(R.layout.fragment_recycler) {
    private var _binding: FragmentRecyclerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentRecyclerViewModel by viewModels()
    private val filmAdapter by lazy {
        FilmListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Обработка нажатия на кнопку назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val manager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        filmAdapter.apply {
            // Удаление фильма
            onNotTodayClick = { film ->
                viewModel.removeFilm(film)
            }
            // Сохранение ID фильма
            onJustRightClick = { film ->
                viewModel.saveFilmId(film)
            }
        }
        binding.recyclerView.apply {
            layoutManager = manager
            adapter = filmAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    // Если мы дошли до конца и не загружаем в данный момент
                    if (totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(filmAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmListState.collect { state ->
                    when (state) {
                        is FilmListState.Loading -> showLoadingState()
                        is FilmListState.Success -> showLoadedState(state)
                        is FilmListState.Error -> showErrorState()
                        is FilmListState.Edge -> showErrorState()
                    }
                }
            }
        }
    }

    private fun showLoadedState(state: FilmListState.Success) {
        binding.errorLayout.root.visibility = View.GONE
        binding.shimmerContainer.root.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        stopShimmerAnimation()
        filmAdapter.submitList(state.films)
    }

    private fun showErrorState() {
        Log.d("FragmentRecyclerViewModel", "Error")
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.shimmerContainer.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showLoadingState() {
        Log.e("FragmentRecyclerViewModel", "Loading")
        binding.errorLayout.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        startShimmerAnimation()
    }

    private fun startShimmerAnimation() {
        binding.shimmerContainer.shimmerLayout.startShimmer() // Запускаем анимацию shimmer
    }

    private fun stopShimmerAnimation() {
        binding.shimmerContainer.shimmerLayout.stopShimmer() // Останавливаем анимацию shimmer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очистка ссылки на binding для избежания утечек памяти
    }
}

