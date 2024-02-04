package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.api.itunessearch.ItunesSearchApi
import com.example.playlistmaker.api.itunessearch.ItunesSearchResponse
import com.example.playlistmaker.api.itunessearch.SearchMessageCode
import com.example.playlistmaker.utils.FormatUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val SEARCH_EDIT_TEXT_VAL = ""

        const val SEARCH_MESSAGE_OBJ = "SEARCH_MESSAGE"
        val SEARCH_MESSAGE_OBJ_VAL: SearchMessageCode = SearchMessageCode.SUCCESS

        const val SEARCH_TRACK_LIST = "SEARCH_TRACK_LIST"

        const val BASE_SEARCH_URL = "https://itunes.apple.com"
    }

    // Строка поиска
    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val clearButton: ImageView by lazy { findViewById(R.id.clearIcon) }

    // Переменная для хранения текста поискового запроса
    private var searhEditText: String = SEARCH_EDIT_TEXT_VAL

    // Переменная для хранения класса сообщения
    private var searchMessage: SearchMessageCode = SearchMessageCode.SUCCESS

    // Сервис поиска треков
    private val itunesSearchBaseUrl = BASE_SEARCH_URL
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesSearchService = retrofit.create(ItunesSearchApi::class.java)

    // Список треков
    private val trackRecycler: RecyclerView by lazy { findViewById(R.id.trackList) }
    private val trackAdapter = TrackAdapter()
    private var trackList = ArrayList<Track>()

    // Данные в случае ошибок
    private val placeholderMessage: LinearLayout by lazy { findViewById(R.id.placeholderMessage) }
    private val messageText: TextView by lazy { findViewById(R.id.messageText) }
    private val messageButton: Button by lazy { findViewById(R.id.messageButton) }
    private val messageImg: ImageView by lazy { findViewById(R.id.messageImg) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Нажатие иконки назад экрана Настройки
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            super.finish()
        }

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
                showMessage(SEARCH_MESSAGE_OBJ_VAL)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        // Фокус на строке поиска
        setFocus(inputEditText)

        // Событие нажатия "DONE"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                itunesSeach(searhEditText)
                true
            }
            false
        }

        // Список
        trackRecycler.layoutManager = LinearLayoutManager(this)
        trackAdapter.setTracks(trackList)
        trackRecycler.adapter = trackAdapter

        // Событие нажатия "Обновить"
        messageButton.setOnClickListener {
            itunesSeach(searhEditText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняется значение переменной searhEditText с текстом поискового запроса
        outState.putString(SEARCH_EDIT_TEXT, searhEditText)
        outState.putSerializable(SEARCH_MESSAGE_OBJ, searchMessage)
        outState.putSerializable(SEARCH_TRACK_LIST, trackList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Сохраненный текст
        searhEditText = savedInstanceState.getString(SEARCH_EDIT_TEXT, SEARCH_EDIT_TEXT_VAL)
        inputEditText.setText(searhEditText)
        // Перемещение курсора в конец текста
        inputEditText.setSelection(inputEditText.length())

        // Сохраняем состояние сообщения-заглушки
        searchMessage = savedInstanceState.getSerializable(SEARCH_MESSAGE_OBJ) as SearchMessageCode
        showMessage(searchMessage)

        // Сохраняем состояние списка
        trackList.clear()
        trackList.addAll(savedInstanceState.getSerializable(SEARCH_TRACK_LIST) as ArrayList<Track>)
        trackAdapter.notifyDataSetChanged()
    }

    private fun itunesSeach(text: String) {
        if (text.isNotEmpty()) {
            itunesSearchService
                .getTracks(text)
                .enqueue(object : Callback<ItunesSearchResponse> {
                    override fun onResponse(
                        call: Call<ItunesSearchResponse>,
                        response: Response<ItunesSearchResponse>
                    ) {
                        when (response.code()) {
                            SearchMessageCode.SUCCESS.code -> {
                                trackList.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    //trackList.addAll(response.body()?.results!!)
                                    for (trak in response.body()?.results!!) {
                                        trak.trackTime =
                                            FormatUtils.formatLongToTrakTime(trak.trackTimeMillis)
                                        trackList.add(trak)
                                    }
                                    trackAdapter.notifyDataSetChanged()
                                }
                                if (trackList.isEmpty()) {
                                    showMessage(SearchMessageCode.ERROR)
                                } else {
                                    showMessage(SearchMessageCode.SUCCESS)
                                }
                            }

                            else -> showMessage(SearchMessageCode.FAILURE)
                        }
                    }

                    override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                        showMessage(SearchMessageCode.FAILURE)
                    }
                })
        }
    }

    private fun showMessage(mesCode: SearchMessageCode) {
        searchMessage = mesCode
        if (mesCode.message.isNotEmpty()) {
            var textRes = resources.getIdentifier(mesCode.message, "string", packageName)
            textRes = if (textRes == 0) R.string.error_text else textRes
            val text = resources.getString(textRes)

            var imageRes = resources.getIdentifier(mesCode.image, "drawable", packageName)
            imageRes = if (imageRes == 0) R.drawable.error_find else imageRes

            // Картинка сообщения-заглушки
            messageImg.setImageResource(imageRes)

            // Текст сообщения-заглушки
            messageText.text = text

            // Отображение сообщения-заглушки и вложенных View элементов
            placeholderMessage.visibility = mesCode.viewMessage
            messageButton.visibility = mesCode.viewButtom
            messageText.visibility = mesCode.viewText

            // Очищение список треков и обновление отображения всего списка
            clearTrackList()

        } else {
            placeholderMessage.visibility = mesCode.viewMessage
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearEditText(inputEditText: EditText?) {
        inputEditText?.text = null
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
        // Очищение список треков и обновление отображения всего списка
        clearTrackList()
    }

    private fun setFocus(inputEditText: EditText?) {
        inputEditText?.requestFocus()
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun clearTrackList() {
        // Очищаем список треков
        trackList.clear()

        // Обновляем отображение всего списка
        trackAdapter.notifyDataSetChanged()
    }

    /*private fun buildMock(): List<Track> {
        return mutableListOf(
            Track(
                "Smells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen Spirit",
                "NirvanaNirvanaNirvanaNirvanaNirvanaNirvanaNirvana",
                "5:01",
                "https://1111is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }*/
}