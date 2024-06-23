package com.example.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.model.NetworkResponse
import com.example.playlistmaker.data.search.model.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val itunesSearchService: ItunesSearchApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply {
                resultCode = -1
                errMessage = context.getString(R.string.error_connect_find)
            }
        }

        if (dto !is TracksSearchRequest) {
            return NetworkResponse().apply {
                resultCode = 400
                errMessage = context.getString(R.string.error_server)
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val resp: NetworkResponse = itunesSearchService.getTracks(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Throwable) {
                NetworkResponse().apply {
                    resultCode = 500
                    errMessage = context.getString(R.string.error_server)
                }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}