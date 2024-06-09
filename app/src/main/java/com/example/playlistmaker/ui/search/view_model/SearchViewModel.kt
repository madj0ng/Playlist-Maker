package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.util.HandlerUtils
import com.example.playlistmaker.util.HandlerUtils.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.consumer.Consumer
import com.example.playlistmaker.util.consumer.ConsumerData

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor,
    private val handler: Handler,
    private val handlerUtils: HandlerUtils
) : AndroidViewModel(application) {

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private var stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var clearIconLiveData = MutableLiveData<ClearIconState>(ClearIconState.None())
    fun observeIconState(): LiveData<ClearIconState> = clearIconLiveData

    private var listLiveData = MutableLiveData<AdapterState>(AdapterState.Search())
    fun observelist(): LiveData<AdapterState> = listLiveData

    private var showTrackTrigger = SingleLiveEvent<String>()
    fun observeShowTrackTrigger(): LiveData<String> = showTrackTrigger

    private var latestSearchText: String? = null
    private var hasEditTextFocus: Boolean? = null

    fun onDestroy() {
        searchInteractor.saveHistory()
    }

    fun startActiviryPlayer(track: Track) {
        showTrackTrigger.postValue(searchInteractor.setTrack(track))
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
        if (handlerUtils.clickDebounce(handler)) {
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
        if (handlerUtils.clickDebounce(handler)) {
            removeToken()
            renderList(AdapterState.History(listOf()))
            renderState(SearchState.Search)
            searchInteractor.clearHistory()
        }
    }

    fun searchClear() {
        if (handlerUtils.clickDebounce(handler)) {
            removeToken()
            renderClearIcon(ClearIconState.None())
            renderList(AdapterState.Search(listOf()))
            getHistory()
        }
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
        return when (this.hasEditTextFocus != newHasEditTextFocus || this.hasEditTextFocus == null) {
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