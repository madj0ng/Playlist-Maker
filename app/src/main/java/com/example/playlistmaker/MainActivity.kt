package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Кнопка поиск
        val search = findViewById<Button>(R.id.search)
        val intent = Intent(this, SearchActivity::class.java)
        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(intent)
            }
        }
        search.setOnClickListener(searchClickListener)

        // Кнопка Медиа
        val media = findViewById<Button>(R.id.media)
        media.setOnClickListener{
            startActivity(Intent(this, MediaActivity::class.java))
        }

        // Кнопка Настройка
        val settings = findViewById<Button>(R.id.settings)
        settings.setOnClickListener(this@MainActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}