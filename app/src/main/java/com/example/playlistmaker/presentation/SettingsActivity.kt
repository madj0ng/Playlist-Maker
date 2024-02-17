package com.example.playlistmaker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.settings.SettingTheme
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageView>(R.id.back)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val agreement = findViewById<ImageView>(R.id.agreement)
        val sendHelp = findViewById<ImageView>(R.id.sendHelp)
        val social = findViewById<ImageView>(R.id.social)

        val settingTheme = SettingTheme(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE))

        // Установка переключатея в соответствии с сохраненным параметром или темой устройства
        themeSwitcher.isChecked =
            settingTheme.read(resources.configuration.uiMode == AppCompatDelegate.MODE_NIGHT_YES)

        // Нажатие иконки назад экрана Настройки
        back.setOnClickListener {
            super.finish()
        }

        // Нажатие кнопки выбора темы
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            settingTheme.write(checked)  // Сохранение параметра
        }

        // Нажатие иконки Пользовательское соглашение
        agreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_offer))
            val urlIntent = Intent(Intent.ACTION_VIEW, url)

            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
            startIntent(urlIntent)
        }

        // Нажатие иконки Написать в поддержку
        sendHelp.setOnClickListener {
            val message =
                getString(R.string.mail_text)     //"Спасибо разработчикам и разработчицам за крутое приложение!"
            val address = getString(R.string.mail_address)  //"madj0ng@yandex.ru"
            val subject =
                getString(R.string.mail_subject)  //"Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val uri = getString(R.string.mail_data_uri)     //"mailto:"
            val mailtoIntent = Intent(Intent.ACTION_SENDTO)
            mailtoIntent.data = Uri.parse(uri)
            mailtoIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            mailtoIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            mailtoIntent.putExtra(Intent.EXTRA_TEXT, message)

            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
            startIntent(mailtoIntent)
        }

        // Нажатие иконки Поделиться приложением
        social.setOnClickListener {
            val message = getString(R.string.sharing_text)
            val type = getString(R.string.sharing_type)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, message)
                setType(type)
            }

            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
            startIntent(shareIntent)
        }
    }

    private fun startIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (err: Exception) {
            Toast.makeText(this, getString(R.string.error_find), Toast.LENGTH_SHORT).show()
        }
    }
}