package com.example.filmsmatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем макет для активити из файла activity_main.xml
        setContentView(R.layout.activity_main)

        // Включаем "Edge-to-Edge" (расширенный режим экрана)
        enableEdgeToEdge()

        // Настраиваем обработчик оконных вставок (отступов вокруг системных панелей)
        setupWindowInsets()
    }

    // Функция для настройки обработчика оконных вставок
    private fun setupWindowInsets() {
        // Устанавливаем слушатель для оконных вставок на основном макете
        // Этот слушатель позволяет управлять отступами вокруг системных панелей (например, статусной и панели навигации)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Получаем отступы для системных панелей
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Устанавливаем отступы вокруг основного макета, чтобы контент не перекрывался системными панелями
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Возвращаем те же самые вставки, чтобы не затронуть другие компоненты
            insets
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            // Если текущий фрагмент - первый фрагмент в стеке, закрываем активити
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}

