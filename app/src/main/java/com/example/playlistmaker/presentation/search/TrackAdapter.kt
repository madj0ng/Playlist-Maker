package com.example.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R

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
}