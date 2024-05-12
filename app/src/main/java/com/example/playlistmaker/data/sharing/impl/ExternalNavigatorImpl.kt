package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context,
    private val intent: Intent
) : ExternalNavigator {
    override fun shareLink() {
        // Поделиться приложением
        val type = context.getString(R.string.sharing_type)
        intent.setAction(Intent.ACTION_SEND)
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, getShareAppLink())
            setType(type)
        }
        startIntent(intent)
    }

    override fun openLink() {
//        // Пользовательское соглашение
        val url = Uri.parse(getTermsLink())
        intent.setAction(Intent.ACTION_VIEW)
        intent.setData(url)
        startIntent(intent)
    }

    override fun openEmail() {
        // Написать в поддержку
        val uri = context.getString(R.string.mail_data_uri)
        val emailData = getSupportEmailData()
        intent.setAction(Intent.ACTION_SENDTO)
        intent.data = Uri.parse(uri)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.address))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        intent.putExtra(Intent.EXTRA_TEXT, emailData.message)
        startIntent(intent)
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.sharing_text)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.url_offer)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            message = context.getString(R.string.mail_text),    //"Спасибо разработчикам и разработчицам за крутое приложение!"
            address = context.getString(R.string.mail_address), //"madj0ng@yandex.ru"
            subject = context.getString(R.string.mail_subject)  //"Сообщение разработчикам и разработчицам приложения Playlist Maker"
        )
    }

    private fun startIntent(intent: Intent) {
        // ошибка в случае "ActivityNotFoundException"
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (err: Exception) {
//            val message = context.getString(R.string.error_find)
        }
    }
}