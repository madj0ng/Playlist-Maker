package com.example.playlistmaker.ui.playlistadd.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistaddBinding
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlistadd.models.AlbumDialogState
import com.example.playlistmaker.ui.playlistadd.view_model.PlaylistAddViewModel
import com.example.playlistmaker.util.FormatUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistAddFragment : Fragment() {
    companion object {
        private const val ALBUM_TYPE = "album_type"

        fun createArgs(albumType: String): Bundle =
            bundleOf(
                ALBUM_TYPE to albumType
            )
    }

    private var _binding: FragmentPlaylistaddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistAddViewModel by viewModel()

    private val formatUtils: FormatUtils = getKoin().get()

    //регистрируем событие, которое вызывает photo picker
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            //обрабатываем событие выбора пользователем фотографии
            if (uri != null) {
                viewModel.changeAlbumUri(uri)
            }
        }

    private var nameTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistaddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.playlistadd_dialog_title))
            .setMessage(getString(R.string.playlistadd_dialog_message))
            .setNeutralButton(getString(R.string.playlistadd_dialog_cancel)) { dialog, which ->
                // ничего не делаем
                viewModel.setDialogResult(true)
            }.setNegativeButton(getString(R.string.playlistadd_dialog_close)) { dialog, which ->
                // выходим из окна без сохранения
                viewModel.setDialogResult(false)
            }

        // Добавление слушателя для обработки нажатия на кнопку Назад
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        // Назад
        binding.ibBack.setOnClickListener {
            viewModel.onBackPressed()
        }

        //Событие нажатия импорта фото
        binding.ivAlbum.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        //Событие нажатия создания Playlist
        binding.acbCreate.setOnClickListener {
            viewModel.albumCreate()
        }

        //Изменения поля "Название" EditText
        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeAlbumName(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        nameTextWatcher?.let { binding.etName.addTextChangedListener(it) }

        //Изменения поля "Описание" EditText
        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeAlbumDescription(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        descriptionTextWatcher?.let { binding.etDescription.addTextChangedListener(it) }

        viewModel.observerAlbum().observe(viewLifecycleOwner) { album ->
            // Наименование
            when (album.name.isNotEmpty()) {
                true -> albumNameFilled()
                false -> albumNameNotFilled()
            }
            // Описание
            when (album.description.isNotEmpty()) {
                true -> albumDescriptionFilled()
                false -> albumDescriptionNotFilled()
            }
            // Оболжка
            if (album.uri != null) {
                albumUriFill(album.uri)
            }
        }

        viewModel.observerToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewModel.observerDialog().observe(viewLifecycleOwner) {
            showDialog(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun albumNameFilled() {
        binding.acbCreate.isEnabled = true
        binding.etName.setBackgroundResource(R.drawable.sh_input_infocus)
        binding.tvUpName.isVisible = true
        binding.tvUpName.setTextColor(requireActivity().getColor(R.color.album_blue))
    }

    private fun albumNameNotFilled() {
        binding.acbCreate.isEnabled = false
        binding.etName.setBackgroundResource(R.drawable.sh_input_outfocus)
        binding.tvUpName.isVisible = false
        binding.tvUpName.setTextColor(requireActivity().getColor(R.color.album_gray))
    }

    private fun albumDescriptionFilled() {
        binding.etDescription.setBackgroundResource(R.drawable.sh_input_infocus)
        binding.tvUpDescription.isVisible = true
        binding.tvUpDescription.setTextColor(requireActivity().getColor(R.color.album_blue))
    }

    private fun albumDescriptionNotFilled() {
        binding.etDescription.setBackgroundResource(R.drawable.sh_input_outfocus)
        binding.tvUpDescription.isVisible = false
        binding.tvUpDescription.setTextColor(requireActivity().getColor(R.color.album_blue))
    }

    private fun albumUriFill(uri: Uri) {
        Glide.with(binding.ivAlbum)
            .load(uri)
            .centerCrop()
            .transform(
                RoundedCorners(
                    formatUtils.dpToPx(
                        PlayerFragment.IMG_RADIUS_PX,
                        binding.ivAlbum.context
                    )
                )
            )
            .into(binding.ivAlbum)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(dialogState: AlbumDialogState) {
        when (dialogState) {
            is AlbumDialogState.None -> {
                onBackPressedCallback.isEnabled = dialogState.isEnabled
                if (!dialogState.isEnabled) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            is AlbumDialogState.Show -> {
                onBackPressedCallback.isEnabled = dialogState.isEnabled
                confirmDialog.show()
            }
        }
    }
}