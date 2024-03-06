package com.example.filmsmatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем "Edge-to-Edge" (расширенный режим экрана)
        enableEdgeToEdge()

        // Устанавливаем макет для активити из файла activity_main.xml
        setContentView(R.layout.activity_main)

        // Настраиваем обработчик оконных вставок (отступов вокруг системных панелей)
        setupWindowInsets()

        // Настраиваем навигацию в приложении с использованием Navigation Component
        setupNavigation()
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

    // Функция для настройки навигации с использованием Navigation Component
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}

