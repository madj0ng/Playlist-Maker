package com.example.playlistmaker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Нажатие иконки назад экрана Настройки
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            super.finish()
        }

        // Нажатие иконки Пользовательское соглашение
        val agreement = findViewById<ImageView>(R.id.agreement)
        agreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_offer))
            val urlIntent = Intent(Intent.ACTION_VIEW, url)

            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
            if (checkPackageCount(urlIntent)) {
                startActivity(urlIntent)
            }
        }

        // Нажатие иконки Написать в поддержку
        val sendHelp = findViewById<ImageView>(R.id.sendHelp)
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
            if (checkPackageCount(mailtoIntent)) {
                startActivity(mailtoIntent)
            }
        }

        // Нажатие иконки Поделиться приложением
        val social = findViewById<ImageView>(R.id.social)
        social.setOnClickListener {
            val message = getString(R.string.sharing_text)
            val type = getString(R.string.sharing_type)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, message)
                setType(type)
            }

            // Вывод сообщения об ошибке в случае "ActivityNotFoundException"
            if (checkPackageCount(shareIntent)) {
                startActivity(Intent.createChooser(shareIntent, null))
            }
        }
    }

    private fun checkPackageCount(intent: Intent): Boolean {
        return if (this.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
        ) {
            true
        } else {
            Toast.makeText(this, getString(R.string.error_find), Toast.LENGTH_SHORT).show()
            false
        }

    }
}