package com.example.playlistmaker.presentation

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
import com.example.playlistmaker.R
import com.example.playlistmaker.api.itunessearch.ItunesSearchApi
import com.example.playlistmaker.api.itunessearch.ItunesSearchResponse
import com.example.playlistmaker.presentation.search.SearchHistory
import com.example.playlistmaker.presentation.search.SearchViewGroup
import com.example.playlistmaker.presentation.search.Track
import com.example.playlistmaker.presentation.search.TrackAdapter
import com.example.playlistmaker.utils.FormatUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val SEARCH_TEXT_VAL = ""

        const val SEARCH_VIEW_GROUP = "SEARCH_MESSAGE"
        const val SEARCH_VIEW_GROUP_VAL = "FIND"

        const val SEARCH_TRACK_LIST = "SEARCH_TRACK_LIST"

        const val BASE_SEARCH_URL = "https://itunes.apple.com"
    }

    // Строка поиска
    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val clearButton: ImageView by lazy { findViewById(R.id.clearIcon) }

    // Переменная для хранения текста поискового запроса
    private var searhText: String = SEARCH_TEXT_VAL

    // Переменная для хранения класса сообщения
    private var searchViewGroup: String = SEARCH_VIEW_GROUP_VAL

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

    // Список истории
    private val historyGroupTitle: TextView by lazy { findViewById(R.id.historyGroupTitle) }
    private val historyGroupButton: Button by lazy { findViewById(R.id.historyGroupButton) }

    private val historyAdapter = TrackAdapter()
    private lateinit var history: SearchHistory

    // Данные в случае ошибок
    private val errorFoundGroup: LinearLayout by lazy { findViewById(R.id.errorFoundGroup) }
    private val errorConnectGroup: LinearLayout by lazy { findViewById(R.id.errorConnectGroup) }
    private val errorConnectButton: Button by lazy { findViewById(R.id.errConnectButton) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        history = SearchHistory(
            getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE),
            historyAdapter
        )

        // Список
        trackRecycler.layoutManager = LinearLayoutManager(this)

        // Список треков
        trackAdapter.tracks.clear()
        trackAdapter.addSubscriber(history)

        // Список истории просмотров
        historyAdapter.tracks.clear()
        history.setHistory()

        // Нажатие иконки назад экрана Настройки
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            super.finish()
        }

        // Обработчик нажатия иконки удаления
        clearButton.setOnClickListener {
            // Очистить поисковый запроса и скрытие клавиатуры
            clearEditText(inputEditText)
        }

        // Обработчик нажатия очистить историю
        historyGroupButton.setOnClickListener {
            clearList(historyAdapter)
            showViewGroup(SearchViewGroup.FIND)
        }

        // События изменения поля EditText
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searhText = s.toString()

                // Отображение истории при улосвии фокуса и пустого поля поиска
                if (inputEditText.hasFocus() && s?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) {
                    showViewGroup(SearchViewGroup.HISTORY)
                } else {
                    showViewGroup(SearchViewGroup.FIND)
                }
                trackRecycler.adapter = getAdapter()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        trackRecycler.adapter = getAdapter()

        // Событие нажатия "DONE"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                itunesSeach(searhText)
                true
            }
            false
        }

        // Отслеживание состояния фокуса поля ввода
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) {
                showViewGroup(SearchViewGroup.HISTORY)
            } else {
                showViewGroup(SearchViewGroup.FIND)
            }
            trackRecycler.adapter = getAdapter()
        }
        // Фокус на строке поиска
        inputEditText.requestFocus()

        // Событие нажатия "Обновить"
        errorConnectButton.setOnClickListener {
            itunesSeach(searhText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняется значение переменной searhText с текстом поискового запроса
        outState.putString(SEARCH_EDIT_TEXT, searhText)
        // Сохраняется значение searchViewGroup состояние отображения
        outState.putString(SEARCH_VIEW_GROUP, searchViewGroup)
        // Сохраняется список найденных треков
        outState.putSerializable(SEARCH_TRACK_LIST, trackAdapter.tracks)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Сохраненный текст
        searhText = savedInstanceState.getString(SEARCH_EDIT_TEXT, SEARCH_TEXT_VAL)
        inputEditText.setText(searhText)
        // Перемещение курсора в конец текста
        inputEditText.setSelection(inputEditText.length())

        // Сохраняем состояние viewGroup
        searchViewGroup = savedInstanceState.getString(SEARCH_VIEW_GROUP, SEARCH_VIEW_GROUP_VAL)
        showViewGroup(searchViewGroup)

        // Сохраняем состояние списка
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(savedInstanceState.getSerializable(SEARCH_TRACK_LIST) as ArrayList<Track>)
        trackAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()

        history.saveHistory()
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
                        trackAdapter.tracks.clear()
                        val viewGroupCls: SearchViewGroup
                        when {
                            response.isSuccessful && !response.body()?.results.isNullOrEmpty() -> {
                                for (trak in response.body()?.results!!) {
                                    trak.trackTime =
                                        FormatUtils.formatLongToTrakTime(trak.trackTimeMillis)
                                    trackAdapter.tracks.add(trak)
                                }
                                viewGroupCls = SearchViewGroup.FIND
                            }

                            response.isSuccessful -> {
                                viewGroupCls = SearchViewGroup.ERROR
                            }

                            else -> {
                                viewGroupCls = SearchViewGroup.FAILURE
                            }
                        }
                        showViewGroup(viewGroupCls)
                        trackAdapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                        showViewGroup(SearchViewGroup.FAILURE)
                        clearList(trackAdapter)
                    }
                })
        }
    }

    private fun getAdapter(): TrackAdapter {
        return when (searchViewGroup) {
            SearchViewGroup.HISTORY.toString() -> historyAdapter
            else -> trackAdapter
        }
    }

    private fun showViewGroup(viewGroupStr: String) {
        showViewGroup(
            when (viewGroupStr) {
                SearchViewGroup.FIND.toString() -> SearchViewGroup.FIND
                SearchViewGroup.HISTORY.toString() -> SearchViewGroup.HISTORY
                SearchViewGroup.ERROR.toString() -> SearchViewGroup.ERROR
                SearchViewGroup.FAILURE.toString() -> SearchViewGroup.FAILURE
                else -> SearchViewGroup.FIND
            }
        )
    }

    private fun showViewGroup(viewGroupCls: SearchViewGroup) {
        searchViewGroup = viewGroupCls.toString()
        when (viewGroupCls) {
            SearchViewGroup.FIND -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.VISIBLE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.GONE
            }

            SearchViewGroup.HISTORY -> {
                historyGroupTitle.visibility = View.VISIBLE
                historyGroupButton.visibility = View.VISIBLE
                trackRecycler.visibility = View.VISIBLE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.GONE
            }

            SearchViewGroup.ERROR -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.GONE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.VISIBLE
            }

            SearchViewGroup.FAILURE -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.GONE
                errorConnectGroup.visibility = View.VISIBLE
                errorFoundGroup.visibility = View.GONE
            }
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
        clearList(trackAdapter)
    }

    private fun clearList(adapter: TrackAdapter) {
        // Очищаем список треков
        adapter.tracks.clear()
        // Обновляем отображение всего списка
        adapter.notifyDataSetChanged()
    }

    /*private fun buildMock(): ArrayList<Track> {
        return arrayListOf(
            Track(
                1,
                "Smells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen SpiritSmells Like Teen Spirit",
                "NirvanaNirvanaNirvanaNirvanaNirvanaNirvanaNirvana",
                "5:01",
                1000,
                "https://1111is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                2,
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                1000,
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                3,
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                1000,
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                4,
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                1000,
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                5,
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                1000,
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                6,
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                1000,
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }*/
}