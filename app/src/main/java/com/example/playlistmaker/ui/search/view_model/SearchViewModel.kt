package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.DebounceUtils.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.util.DebounceUtils.TIME_DEBOUNCE_EMPTY
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor,
    private val debounceUtils: DebounceUtils
) : AndroidViewModel(application) {

    private var stateLiveData = MutableLiveData<SearchState>(SearchState.Loading)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var clearIconLiveData = MutableLiveData<ClearIconState>(ClearIconState.None())
    fun observeIconState(): LiveData<ClearIconState> = clearIconLiveData

    private var listLiveData = MutableLiveData<AdapterState>(AdapterState.Search())
    fun observelist(): LiveData<AdapterState> = listLiveData

    private var showTrackTrigger = SingleLiveEvent<TrackTriggerState>()
    fun observeShowTrackTrigger(): LiveData<TrackTriggerState> = showTrackTrigger

    private var latestSearchText: String? = null
    private var hasEditTextFocus: Boolean? = null

    //    // Формирование отложенной задачи поиска
    private var requestDebounce: Job? = null

    fun startActivityPlayer(track: Track, fromType: String) {
        val state = when (fromType) {
            TYPE_SEARCH -> startPlayerFromSearch(track)

            TYPE_HISTORY -> startPlayerFromHistory(track)
            else -> TrackTriggerState.Empty(0)
        }
        showTrackTrigger.postValue(state)
    }

    private fun startPlayerFromSearch(track: Track): TrackTriggerState {
        setHistory(track)
        return TrackTriggerState.Search(track.trackId)
    }

    private fun startPlayerFromHistory(track: Track): TrackTriggerState {
        return TrackTriggerState.History(track.trackId)
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

                searchDebounce(changedText, SEARCH_DEBOUNCE_DELAY)
//                searchRequestDebounce(changedText)
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
    private fun setHistory(track: Track) {
        viewModelScope.launch {
            searchInteractor.setHistory(track)
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            searchInteractor
                .getHistory()
                .collect { pair ->
                    historyResult(pair.first, pair.second)
                }
        }
    }

    private fun historyResult(historyTracks: ArrayList<Track>?, errorMessage: String?) {
        val tracks = ArrayList<Track>()

        if (historyTracks != null) {
            tracks.clear()
            tracks.addAll(historyTracks)
        }
        when {
            errorMessage != null -> {
                renderState(SearchState.Failure(errorMessage))
            }

            (tracks.isNotEmpty()) -> {
                renderList(AdapterState.History(tracks))
                renderState(SearchState.History)
            }

            else -> {
                renderState(SearchState.Search)
            }
        }
    }

    // Методы операций со списком найденных треков
    // Повторное выполнение задачи поиска через CLICK_DEBOUNCE_DELAY
    fun search() {
        if (debounceUtils.clickDebounce(viewModelScope)) {
            searchDebounce(this.latestSearchText ?: "", TIME_DEBOUNCE_EMPTY)
        }
    }

    private fun searchDebounce(changedText: String, delayTime: Long) {
        requestDebounce?.cancel()
        requestDebounce = viewModelScope.launch {
            delay(delayTime)
            searchRequest(changedText)
        }
    }

    // Задача поиска в новом потоке
    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        searchResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun searchResult(foundTracks: ArrayList<Track>?, errorMessage: String?) {
        val tracks = ArrayList<Track>()

        if (foundTracks != null) {
            tracks.clear()
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                renderState(SearchState.Failure(errorMessage))
            }

            tracks.isEmpty() -> {
                renderState(SearchState.Error)
            }

            else -> {
                renderState(SearchState.Search)
                renderList(AdapterState.Search(tracks))
            }
        }
    }

    // Логика при очистке списков
    fun historyClear() {
        if (debounceUtils.clickDebounce(viewModelScope)) {
            renderList(AdapterState.History(listOf()))
            renderState(SearchState.Search)
            viewModelScope.launch {
                searchInteractor.clearHistory()
            }

        }
    }

    fun searchClear() {
        if (debounceUtils.clickDebounce(viewModelScope)) {
            renderClearIcon(ClearIconState.None())
            renderList(AdapterState.Search(listOf()))
            requestDebounce?.cancel()
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
}