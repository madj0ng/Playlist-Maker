package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.HISTORY_ADAPTER
import com.example.playlistmaker.creator.SEARCH_ADAPTER
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchActivity : AppCompatActivity() {

    private val handler: Handler = getKoin().get()

    // Список треков
    private val tracksAdapter: SearchAdapter by inject<SearchAdapter>(named(SEARCH_ADAPTER))

    // Список истории
    private val historyAdapter: SearchAdapter by inject<SearchAdapter>(named(HISTORY_ADAPTER))

    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    private lateinit var binding: ActivitySearchBinding

    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Список
        binding.trackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Нажатие иконки назад экрана Настройки
        binding.back.setOnClickListener {
            super.finish()
        }

        // Обработчик нажатия иконки удаления
        binding.clearIcon.setOnClickListener {
            // Скрытие клавиатуры
            setInvisibleKeyboard()

            // Очистка поискового запроса
            viewModel.searchClear()
        }

        // Обработчик нажатия очистить историю
        binding.historyGroupButton.setOnClickListener {
            viewModel.historyClear()
        }

        // События изменения поля EditText
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeEditText(s?.toString(), binding.inputEditText.hasFocus())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        // Событие нажатия "DONE"
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search()
                true
            }
            false
        }

        // Отслеживание состояния фокуса поля ввода
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onEditTextFocus(
                hasFocus = hasFocus,
                changedText = binding.inputEditText.text.toString()
            )
        }

        // Событие нажатия "Обновить"
        binding.errorConnectButton.setOnClickListener {
            viewModel.search()
        }

        // Фокус на строке поиска
        binding.inputEditText.requestFocus()

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeIconState().observe(this) {
            setIconState(it)
        }

        viewModel.observelist().observe(this) {
            setAdapterlist(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacksAndMessages(null)

        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        viewModel.onDestroy()
    }

    private fun setIconState(state: ClearIconState) {
        when (state) {
            is ClearIconState.None -> {
                showClearIcon(state.isVisible)
                // Очистка поискового запроса
                clearEditText()
            }

            is ClearIconState.Show -> showClearIcon(state.isVisible)
        }
    }

    private fun showClearIcon(isVisible: Boolean) {
        binding.clearIcon.isVisible = isVisible
    }

    private fun clearEditText() {
        binding.inputEditText.text = null
    }

    private fun setInvisibleKeyboard() {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Error -> showError()
            is SearchState.Failure -> showFailure()
            is SearchState.History -> showHistory()
            is SearchState.Loading -> showLoading()
            is SearchState.Search -> showSearch()
        }
    }

    private fun showLoading() {
        binding.historyGroupTitle.visibility = View.GONE
        binding.historyGroupButton.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.errorConnectGroup.visibility = View.GONE
        binding.errorFoundGroup.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showSearch() {
        binding.historyGroupTitle.visibility = View.GONE
        binding.historyGroupButton.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
        binding.errorConnectGroup.visibility = View.GONE
        binding.errorFoundGroup.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showHistory() {
        binding.historyGroupTitle.visibility = View.VISIBLE
        binding.historyGroupButton.visibility = View.VISIBLE
        binding.trackList.visibility = View.VISIBLE
        binding.errorConnectGroup.visibility = View.GONE
        binding.errorFoundGroup.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showFailure() {
        binding.historyGroupTitle.visibility = View.GONE
        binding.historyGroupButton.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.errorConnectGroup.visibility = View.VISIBLE
        binding.errorFoundGroup.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showError() {
        binding.historyGroupTitle.visibility = View.GONE
        binding.historyGroupButton.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.errorConnectGroup.visibility = View.GONE
        binding.errorFoundGroup.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun setAdapterlist(adapter: AdapterState) {
        when (adapter) {
            is AdapterState.History -> setAdapterHistory(adapter.data)
            is AdapterState.Search -> setAdapterSearch(adapter.data)
        }
    }

    private fun setAdapterHistory(tracks: List<Track>) {
        binding.trackList.adapter = historyAdapter
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    private fun setAdapterSearch(tracks: List<Track>) {
        binding.trackList.adapter = tracksAdapter
        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }
}