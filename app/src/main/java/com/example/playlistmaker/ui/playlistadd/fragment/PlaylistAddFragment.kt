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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistaddBinding
import com.example.playlistmaker.domain.playlistadd.model.Album
import com.example.playlistmaker.ui.album.model.AlbumState
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlistadd.models.AlbumDialogState
import com.example.playlistmaker.ui.playlistadd.models.PlaylistUi
import com.example.playlistmaker.ui.playlistadd.view_model.PlaylistAddViewModel
import com.example.playlistmaker.util.FormatUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel

open class PlaylistAddFragment : Fragment() {

    private var _binding: FragmentPlaylistaddBinding? = null
    private val binding get() = _binding!!

    open val viewModel: PlaylistAddViewModel by viewModel()

    private val formatUtils: FormatUtils = getKoin().get()

    //регистрируем событие, которое вызывает photo picker
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            //обрабатываем событие выбора пользователем фотографии
            viewModel.changeAlbumUri(uri)
        }

    private var nameTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
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

        render(
            PlaylistUi(
                requireContext().getString(R.string.playlistadd_title),
                requireContext().getString(R.string.playlistadd_create)
            )
        )

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.playlistadd_dialog_title))
            .setMessage(getString(R.string.playlistadd_dialog_message))
            .setNeutralButton(getString(R.string.playlistadd_dialog_cancel)) { _, _ ->
                // ничего не делаем
                viewModel.setDialogResult(true)
            }.setNegativeButton(getString(R.string.playlistadd_dialog_close)) { _, _ ->
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
            viewModel.albumOnClick()
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

        viewModel.observerAlbumState().observe(viewLifecycleOwner) { album ->
            render(album)
        }

        viewModel.observerAlbumName().observe(viewLifecycleOwner) { isNameFilled ->
            // Наименование
            when (isNameFilled) {
                true -> albumNameFilled()
                false -> albumNameNotFilled()
            }
        }

        viewModel.observerAlbumDescription().observe(viewLifecycleOwner) { isDescriptionFilled ->
            // Описание
            when (isDescriptionFilled) {
                true -> albumDescriptionFilled()
                false -> albumDescriptionNotFilled()
            }
        }

        viewModel.observerAlbumUri().observe(viewLifecycleOwner) { uri ->
            // Оболжка
            albumUriFill(uri)
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

    private fun render(state: AlbumState) {
        when (state) {
            is AlbumState.Loading -> {}
            is AlbumState.Content -> albumFill(state.data)
            is AlbumState.Empty -> {}
            is AlbumState.Error -> {}
        }
    }

    fun render(playlistUi: PlaylistUi) {
        binding.acbCreate.text = playlistUi.buttonText
        binding.tvTitle.text = playlistUi.titleText
    }

    private fun albumFill(album: Album) {
        albumNameFill(album.name)
        albumDescriptionFill(album.description)
        albumUriFill(album.uri)
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

    private fun albumDescriptionFill(text: String) {
        binding.etDescription.setText(text)
    }

    private fun albumNameFill(text: String) {
        binding.etName.setText(text)
    }

    private fun albumUriFill(uri: Uri?) {
        if (uri != null) {
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
            binding.ivAlbum.setBackgroundResource(R.drawable.sh_album_image)
        } else {
            binding.ivAlbum.setBackgroundResource(R.drawable.sh_album_no_image)
        }
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