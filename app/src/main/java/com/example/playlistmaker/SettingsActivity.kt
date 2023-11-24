package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Нажатие иконки экрана Настройки
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener({
            startActivity(Intent(this, MainActivity::class.java))
        })
    }
}