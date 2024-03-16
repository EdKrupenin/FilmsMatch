package com.example.filmsmatch.links

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.filmsmatch.R
import com.example.filmsmatch.base.ErrorType
import com.example.filmsmatch.databinding.FilmLinksDialogFragmentBinding
import com.example.filmsmatch.links.recycler.LinksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmLinksDialogFragment : DialogFragment() {

    private var _binding: FilmLinksDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilmLinksViewModel by viewModels()


    private val linksAdapter by lazy {
        LinksAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FilmLinksDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_FilmsMatch)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kinopoiskId = arguments?.getInt(ARG_KINOPOISK_ID) ?: 0
        setupObservers()
        viewModel.loadLinks(kinopoiskId)
        binding.linksRecyclerView.adapter = linksAdapter
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        val window = dialog?.window
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { state ->
                    when (state) {
                        is FilmLinksState.Loading -> showLoading()
                        is FilmLinksState.Success -> showSuccess(state)
                        is FilmLinksState.Error -> showError(state)
                    }
                }
            }
        }
    }

    private fun showError(state: FilmLinksState.Error) {
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.linksRecyclerView.visibility = View.GONE
        binding.errorLayout.errorTitle.setText(
            when (state.errorType) {
                ErrorType.EMPTY_RESPONSE -> R.string.empty_links_response_hint_error_layout
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
        viewModel.loadLinks(viewModel.kinopoiskId)
        binding.errorLayout.root.visibility = View.GONE
        binding.linksRecyclerView.visibility = View.VISIBLE
    }

    private fun showSuccess(state: FilmLinksState.Success) {
        linksAdapter.submitList(state.movieLinks)
        binding.errorLayout.root.visibility = View.GONE
        binding.linksRecyclerView.visibility = View.VISIBLE
    }

    private fun showLoading() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_KINOPOISK_ID = "kinopoisk_id"
        fun newInstance(kinopoiskId: Int): FilmLinksDialogFragment {
            val fragment = FilmLinksDialogFragment()
            val args = Bundle().apply {
                putInt(ARG_KINOPOISK_ID, kinopoiskId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
