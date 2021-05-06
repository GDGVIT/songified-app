package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.dscvit.songified.R
import com.dscvit.songified.model.Song


class SongListAdapter : RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {

    var songsList: MutableList<Song> = mutableListOf()

    fun updateSongsList(newSongs: List<Song>) {
        songsList = newSongs as MutableList<Song>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        songsList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_song_search_result,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songsList[position])
    }

    override fun getItemCount() = songsList.size

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val songNameText =
            view.findViewById<TextView>(R.id.tv_list_item_search_result_song_name);
        private val artistNameText =
            view.findViewById<TextView>(R.id.tv_list_item_search_result_song_artist_name)

        fun bind(song: Song) {
            songNameText.text = song.song_title
            artistNameText.text=song.artist.artist_title

        }
    }

}