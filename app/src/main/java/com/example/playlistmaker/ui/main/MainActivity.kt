package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.media.MediaActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.tracks.SearchActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.search)
        val media = findViewById<Button>(R.id.media)
        val settings = findViewById<Button>(R.id.settings)
        val intent = Intent(this, SearchActivity::class.java)

        // Кнопка поиск
        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(intent)
            }
        }
        search.setOnClickListener(searchClickListener)

        // Кнопка Медиа
        media.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        // Кнопка Настройка
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