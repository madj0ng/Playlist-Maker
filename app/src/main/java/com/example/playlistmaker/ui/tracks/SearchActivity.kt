package com.example.playlistmaker.ui.tracks

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.SEARCH_PREFERENCES
import com.example.playlistmaker.presentation.tracks.SearchHistory
import com.example.playlistmaker.presentation.tracks.SearchViewGroup
import com.example.playlistmaker.utils.HandlerUtils

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val SEARCH_TEXT_VAL = ""

        const val SEARCH_VIEW_GROUP = "SEARCH_MESSAGE"
        const val SEARCH_VIEW_GROUP_VAL = "FIND"
    }

    // Строка поиска
    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val clearButton: ImageView by lazy { findViewById(R.id.clearIcon) }

    // Переменная для хранения текста поискового запроса
    private var searhText: String = SEARCH_TEXT_VAL

    // Переменная для хранения класса сообщения
    private var searchViewGroup: String = SEARCH_VIEW_GROUP_VAL

    // Список треков
    private val trackRecycler: RecyclerView by lazy { findViewById(R.id.trackList) }
    private val tracksAdapter = TracksAdapter()

    // Список истории
    private val historyGroupTitle: TextView by lazy { findViewById(R.id.historyGroupTitle) }
    private val historyGroupButton: Button by lazy { findViewById(R.id.historyGroupButton) }

    private val historyAdapter = TracksAdapter()
    private lateinit var history: SearchHistory

    // Данные в случае ошибок
    private val errorFoundGroup: LinearLayout by lazy { findViewById(R.id.errorFoundGroup) }
    private val errorConnectGroup: LinearLayout by lazy { findViewById(R.id.errorConnectGroup) }
    private val errorConnectButton: Button by lazy { findViewById(R.id.errorConnectButton) }

    //
    private val back: ImageView by lazy { findViewById(R.id.back) }
    private val progressBar: ProgressBar by lazy { findViewById(R.id.progressBar) }

    // Отложенная очередь задач
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { itunesSeach() }

    private var consumerRunnable: Runnable? = null

    private val getTrackIneractor = Creator.provideTracksIneractor()

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
        tracksAdapter.tracks.clear()
        tracksAdapter.addSubscriber(history)

        // Список истории просмотров
        historyAdapter.tracks.clear()
        history.setHistory()

        // Нажатие иконки назад экрана Настройки
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
        onEditTextChanged()
        trackRecycler.adapter = getAdapter()

        // Событие нажатия "DONE"
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clickTrack()
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
            clickTrack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (consumerRunnable != null) {
            handler.removeCallbacks(consumerRunnable!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняется значение переменной searhText с текстом поискового запроса
        outState.putString(SEARCH_EDIT_TEXT, searhText)
        // Сохраняется значение searchViewGroup состояние отображения
        outState.putString(SEARCH_VIEW_GROUP, searchViewGroup)
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
    }

    override fun onStop() {
        super.onStop()

        history.saveHistory()
    }

    private fun onEditTextChanged() {

        inputEditText.doOnTextChanged { text, start, before, count ->
            // Скрытие или отображение кнопки удаления
            clearButton.isVisible = !text.isNullOrEmpty()
            searhText = text.toString()

            // Отображение истории при улосвии фокуса и пустого поля поиска
            if (inputEditText.hasFocus() && text?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) {
                showViewGroup(SearchViewGroup.HISTORY)
            } else {
                showViewGroup(SearchViewGroup.FIND)
                // Поиск трека
                searchTrack()
            }
            trackRecycler.adapter = getAdapter()
        }
    }

    private fun searchTrack() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, HandlerUtils.SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickTrack() {
        if (HandlerUtils.clickDebounce(handler)) {
            handler.removeCallbacks(searchRunnable)
            itunesSeach()
        }
    }

    private fun itunesSeach() {
        if (searhText.isNotEmpty()) {
            showViewGroup(SearchViewGroup.PROGRESS)

            getTrackIneractor.searchTracks(
                expression = searhText,
                consumer = object : Consumer<ArrayList<Track>> {
                    override fun consume(data: ConsumerData<ArrayList<Track>>) {
                        if (consumerRunnable != null) {
                            handler.removeCallbacks(consumerRunnable!!)
                        }
                        val newConsumerRunnable = Runnable {
                            tracksAdapter.tracks.clear()
                            val viewGroupCls: SearchViewGroup
                            when (data) {
                                is ConsumerData.Data -> {
                                    viewGroupCls = if (data.value.isNotEmpty()) {
                                        tracksAdapter.tracks.addAll(data.value)
                                        SearchViewGroup.FIND
                                    } else {
                                        SearchViewGroup.ERROR
                                    }
                                }

                                is ConsumerData.Error -> {
                                    viewGroupCls = SearchViewGroup.FAILURE
                                    showError(data.message)
                                }
                            }
                            showViewGroup(viewGroupCls)
                            tracksAdapter.notifyDataSetChanged()
                        }
                        handler.post(newConsumerRunnable)
                        consumerRunnable = newConsumerRunnable
                    }
                }
            )
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getAdapter(): TracksAdapter {
        return when (searchViewGroup) {
            SearchViewGroup.HISTORY.toString() -> historyAdapter
            else -> tracksAdapter
        }
    }

    private fun showViewGroup(viewGroupStr: String) {
        showViewGroup(
            when (viewGroupStr) {
                SearchViewGroup.FIND.toString() -> SearchViewGroup.FIND
                SearchViewGroup.HISTORY.toString() -> SearchViewGroup.HISTORY
                SearchViewGroup.ERROR.toString() -> SearchViewGroup.ERROR
                SearchViewGroup.FAILURE.toString() -> SearchViewGroup.FAILURE
                SearchViewGroup.PROGRESS.toString() -> SearchViewGroup.PROGRESS
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
                progressBar.visibility = View.GONE
            }

            SearchViewGroup.PROGRESS -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.GONE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }

            SearchViewGroup.HISTORY -> {
                historyGroupTitle.visibility = View.VISIBLE
                historyGroupButton.visibility = View.VISIBLE
                trackRecycler.visibility = View.VISIBLE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            SearchViewGroup.ERROR -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.GONE
                errorConnectGroup.visibility = View.GONE
                errorFoundGroup.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            SearchViewGroup.FAILURE -> {
                historyGroupTitle.visibility = View.GONE
                historyGroupButton.visibility = View.GONE
                trackRecycler.visibility = View.GONE
                errorConnectGroup.visibility = View.VISIBLE
                errorFoundGroup.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun clearEditText(inputEditText: EditText?) {
        inputEditText?.text = null
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText?.windowToken, 0)
        // Очищение список треков и обновление отображения всего списка
        clearList(tracksAdapter)
    }

    private fun clearList(adapter: TracksAdapter) {
        // Очищаем список треков
        adapter.tracks.clear()
        // Обновляем отображение всего списка
        adapter.notifyDataSetChanged()
    }
}