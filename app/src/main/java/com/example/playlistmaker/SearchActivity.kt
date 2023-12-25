package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val SEARCH_EDIT_TEXT_VAL = ""
    }

    // Переменная для хранения текста поискового запроса
    private var searhEditText: String = SEARCH_EDIT_TEXT_VAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Нажатие иконки назад экрана Настройки
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            super.finish()
        }

        // Строка поиска
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        // Обработчик нажатия иконки
        clearButton.setOnClickListener {
            // Очистить поисковый запроса и скрытие клавиатуры
            clearEditText(inputEditText)
        }

        // События изменения поля EditText
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searhEditText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }

        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        // Фокус на строке поиска
        setFocus(inputEditText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняется значение переменной searhEditText с текстом поискового запроса
        outState.putString(SEARCH_EDIT_TEXT, searhEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Сохраненный текст
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        searhEditText = savedInstanceState.getString(SEARCH_EDIT_TEXT, SEARCH_EDIT_TEXT_VAL)
        inputEditText.setText(searhEditText)

        // Перемещение курсора в конец текста
        inputEditText.setSelection(inputEditText.length())
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearEditText(inputEditText: EditText?) {
        inputEditText?.setText(null)
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
    }

    private fun setFocus(inputEditText: EditText?) {
        inputEditText?.requestFocus()
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
    }
}