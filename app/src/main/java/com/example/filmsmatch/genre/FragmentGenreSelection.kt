package com.example.filmsmatch.genre

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.FragmentGenreSelectionBinding

class FragmentGenreSelection : Fragment(R.layout.fragment_genre_selection) {

    private lateinit var binding: FragmentGenreSelectionBinding
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Используем привязки для доступа к элементам пользовательского интерфейса
        binding = FragmentGenreSelectionBinding.bind(view)
        binding.continueButton.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            navController.navigate(R.id.action_fragmentGenreSelection_to_fragmentRecycler)
        }
        binding.backButton.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            navController.navigate(R.id.action_any_to_start_screen)
        }
    }
}