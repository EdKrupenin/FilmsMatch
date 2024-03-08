package com.example.filmsmatch.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch


abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<*>, VS : BaseState>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeViewModel()
            }
        }
    }

    abstract fun setupUI()

    abstract suspend fun observeViewModel()

    abstract fun showLoading()
    abstract fun showSuccess(state: VS)
    abstract fun showError(state: VS)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
