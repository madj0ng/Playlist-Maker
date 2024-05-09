package com.example.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.model.Track

class SearchAdapter(private val clickListener: SearchClickListener) :
    RecyclerView.Adapter<SearchViewHolder>() { //, Observable {

    var tracks = ArrayList<Track>()
//    private val subscriber = mutableListOf<Observer>()
//    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return SearchViewHolder(
            TrackViewBinding.inflate(layoutInspector, parent, false),
//            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        /*holder.itemView.setOnClickListener {
            notifySubscriber(tracks[position])
            // Переход в плеер
            startPlayerActivity(holder.itemView.context, tracks[position])
        }*/
    }

    override fun getItemCount() = tracks.size

    /*override fun addSubscriber(observer: Observer) {
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
    }*/

    interface SearchClickListener {
        fun onTrackClick(track: Track)
    }
}