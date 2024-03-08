package com.example.filmsmatch.genre

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.data.GenreDomain
import com.example.filmsmatch.base.BaseFragment
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentGenreSelectionBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

@AndroidEntryPoint
class GenreSelectionFragment :
    BaseFragment<FragmentGenreSelectionBinding, GenreSelectionViewModel, GenreSelectionState>(
        FragmentGenreSelectionBinding::inflate
    ) {
    override val viewModel: GenreSelectionViewModel by viewModels()
    override fun setupUI() {
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentGenreSelection_to_fragmentRecycler)
        }
        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_any_to_start_screen)
        }
    }

    override suspend fun observeViewModel() {
        viewModel.stateFlow.collectLatest { state ->
            when (state) {
                is GenreSelectionState.Loading -> showLoading()
                is GenreSelectionState.Loaded -> showSuccess(state)
                is GenreSelectionState.Error -> showError(state)
            }
        }
    }

    // Показывает разметку ошибки и скрывает ShimmerFrameLayout и ChipGroup
    override fun showError(state: GenreSelectionState) {
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.nestedScroll.visibility = View.GONE
        binding.errorLayout.retry.setOnClickListener { retryButton() }
    }

    // Показывает состояние загрузки: запускает анимацию ShimmerFrameLayout и добавляет пустые чипы
    override fun showLoading() {
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.GONE
        binding.nestedScroll.visibility = View.VISIBLE
        startShimmerAnimation()
        addEmptyChips(20)
    }

    // Показывает загруженные жанры и останавливает анимацию ShimmerFrameLayout
    override fun showSuccess(state: GenreSelectionState) {
        val genreState = state as GenreSelectionState.Loaded
        binding.errorLayout.root.visibility = View.GONE
        binding.shimmerContainer.visibility = View.GONE
        binding.genreChipGroup.visibility = View.VISIBLE
        stopShimmerAnimation()
        populateChipGroup(genreState.genres, genreState.selectedGenres)
        binding.continueButton.isEnabled = genreState.accessNextButton
        updateOrderButton(genreState)
    }

    private fun updateOrderButton(genreState: GenreSelectionState.Loaded) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            genreState.sortingOptions.map { it.description })
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.setText(genreState.sortingOrder.description, false)
        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateSelectedOrder(position)
        }
    }

    private fun startShimmerAnimation() {
        binding.shimmerContainer.startShimmer() // Запускаем анимацию shimmer
    }

    private fun stopShimmerAnimation() {
        binding.shimmerContainer.stopShimmer() // Останавливаем анимацию shimmer
    }

    private fun retryButton() {
        viewModel.loadGenres() // Пытаемся загрузить жанры снова
        // Скрываем разметку ошибки и показываем ShimmerFrameLayout и ChipGroup
        binding.errorLayout.root.visibility = View.GONE
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.genreChipGroup.visibility = View.VISIBLE
    }

    private fun addEmptyChips(count: Int) {
        val inflater = LayoutInflater.from(requireContext())
        for (i in 1..count) {
            val chip = inflater.inflate(
                R.layout.sceleton_chip_layout,
                binding.shimmerChipGroup,
                false
            ) as Chip
            val randomWidth = Random.Default.nextInt(250, 400)
            chip.layoutParams.width = randomWidth
            // Добавляем чип в ChipGroup
            binding.shimmerChipGroup.addView(chip)
        }
    }

    private fun populateChipGroup(
        genresList: List<GenreDomain>,
        selectedGenresList: List<GenreDomain>,
    ) {
        binding.genreChipGroup.removeAllViews()
        for (genre in genresList) {
            val chip = Chip(requireContext())
            chip.text = genre.name
            chip.isCheckable = true
            chip.isChecked = selectedGenresList.contains(genre)
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateSelectedGenres(genre, isChecked)
            }
            binding.genreChipGroup.addView(chip)
        }
    }
}