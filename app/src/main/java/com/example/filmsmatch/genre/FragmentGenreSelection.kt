package com.example.filmsmatch.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.data.GenreDomain
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentGenreSelectionBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class FragmentGenreSelection : Fragment(R.layout.fragment_genre_selection) {

    private lateinit var binding: FragmentGenreSelectionBinding
    private val genreViewModel: GenreSelectionViewModel by viewModels()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Используем привязки для доступа к элементам пользовательского интерфейса
        binding = FragmentGenreSelectionBinding.bind(view)
        binding.continueButton.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            navController.navigate(R.id.action_fragmentGenreSelection_to_fragmentRecycler)
        }
        binding.toolbar.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            navController.navigate(R.id.action_any_to_start_screen)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                genreViewModel.genreSelectionState.collect { genreState ->
                    when (genreState) {
                        is GenreSelectionState.Error -> showErrorState()
                        is GenreSelectionState.Loaded -> showLoadedState(genreState)
                        GenreSelectionState.Loading -> showLoadingState()
                    }
                }
            }
        }
    }

    // Показывает разметку ошибки и скрывает ShimmerFrameLayout и ChipGroup
    private fun showErrorState() {
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.nestedScroll.visibility = View.GONE
        binding.errorLayout.retry.setOnClickListener { retryButton() }
    }

    // Показывает загруженные жанры и останавливает анимацию ShimmerFrameLayout
    private fun showLoadedState(genreState: GenreSelectionState.Loaded) {
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
            genreViewModel.updateSelectedOrder(position)
        }
    }

    // Показывает состояние загрузки: запускает анимацию ShimmerFrameLayout и добавляет пустые чипы
    private fun showLoadingState() {
        binding.continueButton.isEnabled = false
        binding.errorLayout.root.visibility = View.GONE
        binding.nestedScroll.visibility = View.VISIBLE
        startShimmerAnimation()
        addEmptyChips(20)
    }

    private fun startShimmerAnimation() {
        binding.shimmerContainer.startShimmer() // Запускаем анимацию shimmer
    }

    private fun stopShimmerAnimation() {
        binding.shimmerContainer.stopShimmer() // Останавливаем анимацию shimmer
    }

    private fun retryButton() {
        genreViewModel.loadGenres() // Пытаемся загрузить жанры снова
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
                genreViewModel.updateSelectedGenres(genre, isChecked)
            }
            binding.genreChipGroup.addView(chip)
        }
    }
}