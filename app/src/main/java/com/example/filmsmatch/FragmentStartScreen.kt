package com.example.filmsmatch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filmsmatch.databinding.FragmentStartScreenBinding

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

    }

}
