package com.example.filmsmatch.start

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentStartScreen : Fragment(R.layout.fragment_start_screen) {

    private lateinit var binding: FragmentStartScreenBinding
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Используем привязки для доступа к элементам пользовательского интерфейса
        binding = FragmentStartScreenBinding.bind(view)
        binding.swipeModeButton.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            navController.navigate(R.id.action_fragmentStartScreen_to_fragmentGenreSelection)
        }

        binding.profileButton.setOnClickListener {
            showToDo()
        }

        binding.rouletteModeButton.setOnClickListener {
            showToDo()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Если это ваш стартовый фрагмент, закройте приложение
                if (isEnabled) {
                    activity?.finish()
                } else {
                    // Иначе, обработайте нажатие кнопки назад стандартным образом
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showToDo() {
        Toast.makeText(requireActivity(), "Этот функционал еще не доступен", Toast.LENGTH_SHORT)
            .show()
    }
}
