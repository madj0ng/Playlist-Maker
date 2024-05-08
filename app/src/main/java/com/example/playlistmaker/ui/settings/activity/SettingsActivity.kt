package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.model.ThemeState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)    //setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

//        val back = findViewById<ImageView>(R.id.back)
//        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
//        val agreement = findViewById<ImageView>(R.id.agreement)
//        val sendHelp = findViewById<ImageView>(R.id.sendHelp)
//        val social = findViewById<ImageView>(R.id.social)

//        val themeState = viewModel.observeTheme().value
//        if (themeState != null) {
//            renderSwitcher(themeState)
//        } else {
//            renderSwitcher(
//                if (resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES) {
//                    ThemeState.Light
//                } else {
//                    ThemeState.Dark
//                }
//            )
//        }
//        val settingTheme = ThemeLocalStorage(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE))
//        // Установка переключателя в соответствии с сохраненным параметром или темой устройства
//        binding.themeSwitcher.isChecked =
//            settingTheme.read(resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES)

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Нажатие кнопки выбора темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateSwitcher(checked)

//            settingTheme.write(checked)  // Сохранение параметра
        }

        // Нажатие иконки Пользовательское соглашение
        binding.agreement.setOnClickListener {
            viewModel.openTerms()

//            val url = Uri.parse(getString(R.string.url_offer))
//            val urlIntent = Intent(Intent.ACTION_VIEW, url)
//
//            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
//            startIntent(urlIntent)
        }

        // Нажатие иконки Написать в поддержку
        binding.sendHelp.setOnClickListener {
            viewModel.openSupport()

//            val message =
//                getString(R.string.mail_text)     //"Спасибо разработчикам и разработчицам за крутое приложение!"
//            val address = getString(R.string.mail_address)  //"madj0ng@yandex.ru"
//            val subject =
//                getString(R.string.mail_subject)  //"Сообщение разработчикам и разработчицам приложения Playlist Maker"
//            val uri = getString(R.string.mail_data_uri)     //"mailto:"
//            val mailtoIntent = Intent(Intent.ACTION_SENDTO)
//            mailtoIntent.data = Uri.parse(uri)
//            mailtoIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
//            mailtoIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
//            mailtoIntent.putExtra(Intent.EXTRA_TEXT, message)
//
//            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
//            startIntent(mailtoIntent)
        }

        // Нажатие иконки Поделиться приложением
        binding.social.setOnClickListener {
            viewModel.shareApp()

//            val message = getString(R.string.sharing_text)
//            val type = getString(R.string.sharing_type)
//            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                putExtra(Intent.EXTRA_TEXT, message)
//                setType(type)
//            }
//
//            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
//            startIntent(shareIntent)
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

    /*    private fun startIntent(intent: Intent) {
            try {
                startActivity(intent)
            } catch (err: Exception) {
                Toast.makeText(this, getString(R.string.error_find), Toast.LENGTH_SHORT).show()
            }
        }*/
}