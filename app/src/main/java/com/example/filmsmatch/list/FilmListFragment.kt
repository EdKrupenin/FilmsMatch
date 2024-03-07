package com.example.filmsmatch.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentRecyclerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmListFragment : Fragment(R.layout.fragment_recycler) {
    private lateinit var binding: FragmentRecyclerBinding
    private val viewModel: FragmentRecyclerViewModel by viewModels()
    private val filmAdapter by lazy {
        FilmListAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val manager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.recyclerView.apply {
            layoutManager = manager
            adapter = filmAdapter
        }
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(filmAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmListState.collect { state ->
                    when (state) {
                        is FilmListState.Loading -> {
                            // Показываем прогресс загрузки или что-то еще
                        }

                        is FilmListState.Success -> {
                            filmAdapter.submitList(state.films)
                        }

                        is FilmListState.Error -> {
                            // Показываем сообщение об ошибке или что-то еще
                        }
                    }
                }
            }
        }
    }
}

