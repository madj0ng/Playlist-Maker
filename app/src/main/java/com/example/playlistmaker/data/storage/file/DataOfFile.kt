package com.example.playlistmaker.data.storage.file

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.storage.SetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset

class DataOfFile(
    val application: Application
) : SetItem<Uri, Uri?> {
    companion object {
        private const val NAME_PREFIX = "myalbum"
    }

    override suspend fun set(item: Uri): Uri? {
//        return withContext(Dispatchers.IO) {
            //создаём экземпляр класса File, который указывает на нужный каталог
            val filePath =
                File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), NAME_PREFIX)
            //создаем каталог, если он не создан
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            //генерируем случайное имя
            val fileName =
                NAME_PREFIX + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
            //создаём экземпляр класса File, который указывает на файл внутри каталога
            val file = File(filePath, fileName)

            // создаём входящий поток байтов из выбранной картинки
            val inputStream = application.contentResolver.openInputStream(item)
            // создаём исходящий поток байтов в созданный выше файл
            val outputStream = FileOutputStream(file)
            // записываем картинку с помощью BitmapFactory
            return if (BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            ) {
                Uri.fromFile(file)
            } else {
                null
            }
//        }
    }
}