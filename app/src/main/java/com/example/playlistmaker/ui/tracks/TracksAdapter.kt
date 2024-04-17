package com.example.playlistmaker.ui.tracks

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.tracks.Observable
import com.example.playlistmaker.presentation.tracks.Observer
import com.example.playlistmaker.utils.HandlerUtils
import com.google.gson.Gson

class TracksAdapter : RecyclerView.Adapter<TracksViewHolder>(), Observable {
    var tracks = ArrayList<Track>()
    private val subscriber = mutableListOf<Observer>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        )
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            notifySubscriber(tracks[position])
            // Переход в плеер
            startPlayerActivity(holder.itemView.context, tracks[position])
        }
    }

    override fun addSubscriber(observer: Observer) {
        subscriber.add(observer)
    }

    override fun notifySubscriber(track: Track) {
        subscriber.forEach {
            it.addToList(track)
        }
    }

    private fun startPlayerActivity(context: Context, track: Track) {
        if (HandlerUtils.clickDebounce(handler)) {
            val player = Intent(context, PlayerActivity::class.java)
            player.putExtra(PlayerActivity.TRACK_KEY, Gson().toJson(track))
            context.startActivity(player)
        }
    }
}