package com.example.filmsmatch.list

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsmatch.base.BaseFragment
import com.example.filmsmatch.databinding.FragmentRecyclerBinding
import dagger.hilt.android.AndroidEntryPoint

private const val VISIBLE_THRESHOLD = 5

@AndroidEntryPoint
class FilmListFragment : BaseFragment<FragmentRecyclerBinding, FilmListViewModel, FilmListState>(
    FragmentRecyclerBinding::inflate
) {

    override val viewModel: FilmListViewModel by viewModels()
    private val filmAdapter by lazy {
        FilmListAdapter()
    }

    override fun setupUI() {
        setupRecyclerView()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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

    override suspend fun observeViewModel() {
        viewModel.stateFlow.collect { state ->
            when (state) {
                is FilmListState.Loading -> showLoading()
                is FilmListState.Success -> showSuccess(state)
                is FilmListState.Error -> showError(state)
                is FilmListState.Edge -> showError(state)
            }
        }
    }

    override fun showLoading() {
        Log.e("FragmentRecyclerViewModel", "Loading")
        binding.errorLayout.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        startShimmerAnimation()
    }

    override fun showSuccess(success: FilmListState) {
        val state = success as FilmListState.Success
        binding.errorLayout.root.visibility = View.GONE
        binding.shimmerContainer.root.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        stopShimmerAnimation()
        filmAdapter.submitList(state.films)
    }

    override fun showError(state: FilmListState) {
        Log.d("FragmentRecyclerViewModel", "Error")
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.shimmerContainer.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun startShimmerAnimation() {
        binding.shimmerContainer.shimmerLayout.startShimmer() // Запускаем анимацию shimmer
    }

    private fun stopShimmerAnimation() {
        binding.shimmerContainer.shimmerLayout.stopShimmer() // Останавливаем анимацию shimmer
    }
}

