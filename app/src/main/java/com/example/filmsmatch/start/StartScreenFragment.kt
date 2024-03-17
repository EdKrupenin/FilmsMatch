package com.example.filmsmatch.start

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filmsmatch.R
import com.example.filmsmatch.databinding.StartScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartScreenFragment : Fragment(R.layout.start_screen_fragment) {

    private lateinit var binding: StartScreenFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Используем привязки для доступа к элементам пользовательского интерфейса
        binding = StartScreenFragmentBinding.bind(view)
        binding.swipeModeButton.setOnClickListener {
            // Выполняем переход к целевому фрагменту с помощью экшена
            findNavController().navigate(R.id.action_fragmentStartScreen_to_fragmentGenreSelection)
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
        Toast.makeText(requireActivity(), R.string.warning_dev_tool_msg, Toast.LENGTH_SHORT)
            .show()
    }
}
