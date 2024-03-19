package com.example.filmsmatch.genre

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.model.GenreDomain
import com.example.filmsmatch.R
import com.example.filmsmatch.base.BaseFragment
import com.example.filmsmatch.base.ErrorType
import com.example.filmsmatch.databinding.GenresSelectionFragmentBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

@AndroidEntryPoint
class GenreSelectionFragment :
    BaseFragment<GenresSelectionFragmentBinding, GenreSelectionViewModel, GenreSelectionState>(
        GenresSelectionFragmentBinding::inflate
    ) {
    override val viewModel: GenreSelectionViewModel by viewModels()
    override fun setupUI() {
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentGenreSelection_to_fragmentRecycler)
        }
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
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
        val error = state as GenreSelectionState.Error
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.nestedScroll.visibility = View.GONE
        binding.errorLayout.errorTitle.setText(
            when (error.errorType) {
                ErrorType.EMPTY_RESPONSE -> R.string.empty_genres_response_hint_error_layout
                ErrorType.BAD_REQUEST -> R.string.network_error_hint_error_layout
                ErrorType.NETWORK_ERROR -> R.string.network_error_hint_error_layout
                else -> R.string.unknown_error_hint_error_layout
            }
        )
        binding.errorLayout.retry.apply {
            setText(
                if (error.retryAction) R.string.retry_button_error_layout else R.string.back_button_error_layout
            )
            setOnClickListener {
                if (error.retryAction) retryAction() else findNavController().navigateUp()
            }
        }
    }

    // Показывает состояние загрузки: запускает анимацию ShimmerFrameLayout и добавляет пустые чипы
    override fun showLoading() {
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.GONE
        binding.nestedScroll.visibility = View.VISIBLE
        startShimmerAnimation()
        addEmptyChips()
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
            R.layout.dropdown_list_item,
            genreState.sortingOptionDomains.map { it.description })
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

    private fun retryAction() {
        viewModel.loadGenres() // Пытаемся загрузить жанры снова
        // Скрываем разметку ошибки и показываем ShimmerFrameLayout и ChipGroup
        binding.errorLayout.root.visibility = View.GONE
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.genreChipGroup.visibility = View.VISIBLE
    }

    private fun addEmptyChips() {
        val count = 20 //TODO в зависимости от разрешения экрана
        val inflater = LayoutInflater.from(requireContext())
        for (i in 1..count) {
            val chip = inflater.inflate(
                R.layout.chip_sceleton,
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