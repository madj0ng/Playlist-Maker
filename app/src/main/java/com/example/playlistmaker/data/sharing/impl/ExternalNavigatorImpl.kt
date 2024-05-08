package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context
) :
    ExternalNavigator {
    override fun shareLink() {
        // Поделиться приложением
        val type = context.getString(R.string.sharing_type)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, getShareAppLink())
            setType(type)
        }

        startIntent(shareIntent)
    }

    override fun openLink() {
//        // Пользовательское соглашение
        val url = Uri.parse(getTermsLink())
        val urlIntent = Intent(Intent.ACTION_VIEW, url)

        startIntent(urlIntent)
    }

    override fun openEmail() {
        // Написать в поддержку
        val uri = context.getString(R.string.mail_data_uri)
        val emailData = getSupportEmailData()
        val mailtoIntent = Intent(Intent.ACTION_SENDTO)
        mailtoIntent.data = Uri.parse(uri)
        mailtoIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.address))
        mailtoIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        mailtoIntent.putExtra(Intent.EXTRA_TEXT, emailData.message)

        startIntent(mailtoIntent)
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