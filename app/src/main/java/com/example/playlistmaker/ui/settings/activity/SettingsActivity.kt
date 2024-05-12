package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.model.ThemeState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Нажатие кнопки выбора темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateSwitcher(checked)
        }

        // Нажатие иконки Пользовательское соглашение
        binding.agreement.setOnClickListener {
            viewModel.openTerms()
        }

        // Нажатие иконки Написать в поддержку
        binding.sendHelp.setOnClickListener {
            viewModel.openSupport()
        }

        // Нажатие иконки Поделиться приложением
        binding.social.setOnClickListener {
            viewModel.shareApp()
        }

        viewModel.observeTheme().observe(this) {
            renderSwitcher(it)
        }
    }

    private fun renderSwitcher(state: ThemeState) {
        binding.themeSwitcher.isChecked = when (state) {
            ThemeState.Dark -> true
            ThemeState.Light -> false
        }
    }
}