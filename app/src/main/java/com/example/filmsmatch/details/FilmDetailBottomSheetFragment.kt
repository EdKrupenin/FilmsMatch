package com.example.filmsmatch.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.filmsmatch.R
import com.example.filmsmatch.base.ErrorType
import com.example.filmsmatch.databinding.FilmDetailBottomSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: FilmDetailViewModel by viewModels()
    private var _binding: FilmDetailBottomSheetFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FilmDetailBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kinopoiskId = arguments?.getInt(ARG_KINOPOISK_ID) ?: 0
        viewModel.loadMovieDetails(kinopoiskId)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    when (state) {
                        is FilmDetailState.Loading -> showLoading()
                        is FilmDetailState.Success -> showSuccess(state)
                        is FilmDetailState.Error -> showError(state)
                    }
                }
            }
        }
    }

    private fun showLoading() {

    }

    private fun showSuccess(success: FilmDetailState.Success) {
        val movieDetails = success.movieDetails

        binding.sloganField.isVisible = movieDetails.slogan.isNotBlank()
        binding.sloganTextView.setText(movieDetails.slogan)

        val description = movieDetails.description.ifBlank { movieDetails.shortDescription }
        binding.descriptionField.isVisible = description.isNotBlank()
        binding.descriptionTextView.setText(description)

        binding.countriesField.isVisible = movieDetails.countries.isNotEmpty()
        binding.countriesTextView.setText(movieDetails.countries.joinToString(separator = " • "))

        val ageLimits = listOfNotNull(
            movieDetails.ratingMpaa.takeIf { it.isNotBlank() }?.uppercase(),
            movieDetails.ratingAgeLimits.filter { it.isDigit() }.takeIf { it.isNotBlank() }
                ?.plus("+")
        ).joinToString(separator = " • ")
        binding.ratingAgeLimitsField.isVisible = ageLimits.isNotEmpty()
        binding.ratingAgeLimitsTextView.setText(ageLimits)
    }

    private fun showError(state: FilmDetailState.Error) {
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.nestedScroll.visibility = View.GONE
        binding.errorLayout.errorTitle.setText(
            when (state.errorType) {
                ErrorType.EMPTY_RESPONSE -> R.string.empty_description_response_hint_error_layout
                ErrorType.BAD_REQUEST -> R.string.network_error_hint_error_layout
                ErrorType.NETWORK_ERROR -> R.string.network_error_hint_error_layout
                else -> R.string.unknown_error_hint_error_layout
            }
        )
        binding.errorLayout.retry.apply {
            setText(
                if (state.retryAction) R.string.retry_button_error_layout else R.string.back_button_error_layout
            )
            setOnClickListener {
                if (state.retryAction) retryAction() else dismiss()
            }
        }
    }

    private fun retryAction() {
        viewModel.loadMovieDetails(viewModel.kinopoiskId) // Пытаемся загрузить жанры снова
        // Скрываем разметку ошибки и показываем ShimmerFrameLayout и ChipGroup
        binding.errorLayout.root.visibility = View.GONE
        binding.nestedScroll.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_KINOPOISK_ID = "kinopoisk_id"
        fun newInstance(kinopoiskId: Int): FilmDetailBottomSheetFragment {
            val fragment = FilmDetailBottomSheetFragment()
            val args = Bundle().apply {
                putInt(ARG_KINOPOISK_ID, kinopoiskId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}