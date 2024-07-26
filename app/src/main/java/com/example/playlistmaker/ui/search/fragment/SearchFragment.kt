package com.example.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.TYPE_HISTORY
import com.example.playlistmaker.creator.TYPE_SEARCH
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.search.models.AdapterState
import com.example.playlistmaker.ui.search.models.ClearIconState
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.ui.search.models.TrackTriggerState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.util.DebounceUtils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private var textWatcher: TextWatcher? = null

    // Список треков
    private lateinit var tracksAdapter: SearchAdapter
    private lateinit var onSearchTrackClickDebounce: (Track) -> Unit

    // Список истории
    private lateinit var historyAdapter: SearchAdapter
    private lateinit var onHistoryTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { track ->
            viewModel.startActivityPlayer(track, TYPE_SEARCH)
        }
        tracksAdapter = SearchAdapter { track -> onSearchTrackClickDebounce(track) }

        onHistoryTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { track ->
            viewModel.startActivityPlayer(track, TYPE_HISTORY)
        }
        historyAdapter = SearchAdapter { track -> onHistoryTrackClickDebounce(track) }

        // Список
        binding.trackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Фокус на строке поиска
        binding.inputEditText.requestFocus()

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

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeIconState().observe(viewLifecycleOwner) {
            setIconState(it)
        }

        viewModel.observelist().observe(viewLifecycleOwner) {
            setAdapterlist(it)
        }

        viewModel.observeShowTrackTrigger().observe(viewLifecycleOwner) {
            showTrackDetails(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        _binding = null
    }

    private fun setInvisibleKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
        binding.historyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
        binding.noConnectGroup.visibility = View.GONE
        binding.trackList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showSearch() {
        binding.historyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
        binding.noConnectGroup.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showHistory() {
        binding.historyGroup.visibility = View.VISIBLE
        binding.errorGroup.visibility = View.GONE
        binding.noConnectGroup.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showFailure() {
        binding.historyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.GONE
        binding.noConnectGroup.visibility = View.VISIBLE
        binding.trackList.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showError() {
        binding.historyGroup.visibility = View.GONE
        binding.errorGroup.visibility = View.VISIBLE
        binding.noConnectGroup.visibility = View.GONE
        binding.trackList.visibility = View.GONE
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

    private fun showTrackDetails(state: TrackTriggerState) {
        when (state) {
            is TrackTriggerState.History -> {
                navigateToDetails(state.trackId, state.dataType)
            }

            is TrackTriggerState.Search -> {
                navigateToDetails(state.trackId, state.dataType)
            }

            else -> {}
        }
    }

    private fun navigateToDetails(trackId: Int, trackDataType: String) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
//            R.id.action_searchFragment_to_playerActivity,
            PlayerFragment.createArgs(trackId, trackDataType)
        )
    }
}