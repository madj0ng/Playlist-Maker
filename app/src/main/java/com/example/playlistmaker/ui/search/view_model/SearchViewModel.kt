package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.util.HandlerUtils
import com.example.playlistmaker.util.HandlerUtils.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData

class SearchViewModel(
    private val application: Application,
    private val searchInteractor: SearchInteractor
) : AndroidViewModel(application) {

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                SearchViewModel(
                    app,
                    Creator.provideSearchInteractor(app)
                )
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private var stateLiveData = MutableLiveData<SearchState>(SearchState.Search)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var clearIconLiveData = MutableLiveData<ClearIconState>(ClearIconState.None())
    fun observeIconState(): LiveData<ClearIconState> = clearIconLiveData

    private var listLiveData = MutableLiveData<AdapterState>(AdapterState.Search())
    fun observelist(): LiveData<AdapterState> = listLiveData

    private var latestSearchText: String? = null
    private var hasEditTextFocus: Boolean? = null

    fun onDestroy() {
        searchInteractor.saveHistory()
    }

    fun startActiviryPlayer(track: Track) {
        runActivityPlayer(searchInteractor.setTrack(track))
    }

    fun changeEditText(
        changedText: String?,
        hasFocus: Boolean
    ) {
        if (isNewSearchText(changedText ?: "")) {
            // Скрытие или отображение кнопки удаления
            if (changedText.isNullOrEmpty()) {
                renderClearIcon(ClearIconState.None())
                // Список истории просмотров
                if (hasFocus) {
                    getHistory()
                }
            } else {
                // Список найденных треков
                renderClearIcon(ClearIconState.Show())
                searchDebounce(changedText = changedText)
            }
        } else {
            return
        }
    }

    fun onEditTextFocus(hasFocus: Boolean, changedText: String) {
        if (isNewEditTextFocus(hasFocus)) {
            if (isNewSearchText(changedText)) {
                if (hasFocus && changedText.isEmpty()) {
                    getHistory()
                }
            }
        }
    }

    // Методы для работы со списком истории просмотренных треков
    fun setHistory(track: Track) {
        searchInteractor.setHistory(track)
    }

    private fun getHistory() {
        removeToken()

        searchInteractor.getHistory(consumer = object : Consumer<ArrayList<Track>> {
            override fun consume(data: ConsumerData<ArrayList<Track>>) {
                val tracks = ArrayList<Track>()
                when (data) {
                    is ConsumerData.Data -> {
                        if (data.value != null) {
                            tracks.clear()
                            tracks.addAll(data.value)
                        }

                        when {
                            (tracks.isNotEmpty()) -> {
                                renderList(AdapterState.History(tracks))
                                renderState(SearchState.History)
                            }

                            else -> {
                                renderState(SearchState.Search)
                            }

                        }
                    }

                    is ConsumerData.Error -> {}
                }
            }
        })
    }

    // Методы операций со списком найденных треков
    // Повторное выполнение задачи поиска через CLICK_DEBOUNCE_DELAY
    fun search() {
        if (HandlerUtils.clickDebounce(handler)) {
            removeToken()
            searchRequest(this.latestSearchText ?: "")
        }
    }

    // Формирование отложенной задачи поиска
    private fun searchDebounce(changedText: String) {
        removeToken()

        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    // Задача поиска в новом потоке
    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            searchInteractor.searchTracks(
                expression = newSearchText,
                consumer = object : Consumer<ArrayList<Track>> {
                    override fun consume(data: ConsumerData<ArrayList<Track>>) {
                        val tracks = ArrayList<Track>()
                        when (data) {
                            is ConsumerData.Data -> {
                                if (data.value != null) {
                                    tracks.clear()
                                    tracks.addAll(data.value)
                                }

                                when {
                                    tracks.isEmpty() -> {
                                        renderState(SearchState.Error)
                                    }

                                    else -> {
                                        renderList(AdapterState.Search(tracks))
                                        renderState(SearchState.Search)
                                    }
                                }
                            }

                            is ConsumerData.Error -> {
                                renderState(
                                    SearchState.Failure(
                                        errorMessage = data.message
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    // Логика при очистке списков
    fun historyClear() {
        if (HandlerUtils.clickDebounce(handler)) {
            removeToken()
            renderList(AdapterState.History(listOf()))
            renderState(SearchState.Search)
            searchInteractor.clearHistory()
        }
    }

    fun searchClear() {
        if (HandlerUtils.clickDebounce(handler)) {
            removeToken()
            renderClearIcon(ClearIconState.None())
            renderList(AdapterState.Search(listOf()))
            getHistory()
        }
    }

    private fun runActivityPlayer(trackString: String) {
        val player = Intent(application, PlayerActivity::class.java)
        player.putExtra(PlayerActivity.TRACK_KEY, trackString)
        player.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.applicationContext.startActivity(player)
    }

    private fun isNewSearchText(newSearchText: String): Boolean {
        return when (this.latestSearchText != newSearchText) {
            true -> {
                this.latestSearchText = newSearchText
                true
            }

            false -> {
                false
            }
        }
    }

    private fun isNewEditTextFocus(newHasEditTextFocus: Boolean): Boolean {
        return when (this.hasEditTextFocus != newHasEditTextFocus) {
            true -> {
                this.hasEditTextFocus = newHasEditTextFocus
                true
            }

            false -> {
                false
            }
        }
    }

    private fun renderList(state: AdapterState) {
        listLiveData.postValue(state)
    }

    private fun renderClearIcon(state: ClearIconState) {
        clearIconLiveData.postValue(state)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private fun removeToken() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    override fun onCleared() {
        removeToken()
    }
}