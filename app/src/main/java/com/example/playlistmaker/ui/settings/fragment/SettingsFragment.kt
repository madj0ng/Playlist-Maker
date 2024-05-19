package com.example.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.model.ThemeState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SettingsFragment : Fragment() {

    private val viewModel by activityViewModel<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Нажатие кнопки выбора темы
        binding.themeSwitcher.setOnCheckedChangeListener { buttonView, checked ->
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

        viewModel.observeTheme().observe(viewLifecycleOwner) {
            renderSwitcher(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderSwitcher(state: ThemeState) {
        binding.themeSwitcher.isChecked = when (state) {
            is ThemeState.Dark -> true
            is ThemeState.Light -> false
        }
    }
}