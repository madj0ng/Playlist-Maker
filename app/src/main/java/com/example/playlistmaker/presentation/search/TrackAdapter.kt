package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.PlayerActivity
import com.google.gson.Gson

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>(), Observable {
    var tracks = ArrayList<Track>()
    private val subscriber = mutableListOf<Observer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        )
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            notifySubscriber(tracks[position])
            // Переход в плеер
            startPlayerActivity(holder.itemView.context, tracks[position])
        }
    }

    override fun addSubscriber(observer: Observer){
        subscriber.add(observer)
    }

    override fun notifySubscriber(track: Track) {
        subscriber.forEach {
            it.addToList(track)
        }
    }

    private fun startPlayerActivity(context: Context, track: Track){
        val player = Intent(context, PlayerActivity::class.java)
        player.putExtra(PlayerActivity.TRACK_KEY, Gson().toJson(track) )
        context.startActivity(player)
    }
}