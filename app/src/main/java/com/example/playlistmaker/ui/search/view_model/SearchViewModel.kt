package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.DebounceUtils.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor,
    private val debounceUtils: DebounceUtils
) : AndroidViewModel(application) {

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

    // Формирование отложенной задачи поиска
    private val searchRequestDebounce: (String) -> Unit = debounce(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true,
    ) { newSearchText -> searchRequest(newSearchText) }


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
                searchRequestDebounce(changedText)
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
            searchRequest(this.latestSearchText ?: "")
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
            searchInteractor.clearHistory()
        }
    }

    fun searchClear() {
        if (debounceUtils.clickDebounce(viewModelScope)) {
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
}