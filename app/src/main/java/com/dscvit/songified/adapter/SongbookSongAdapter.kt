package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.dscvit.songified.R
import com.dscvit.songified.model.SingleSongbookSong


class SongbookSongAdapter : RecyclerView.Adapter<SongbookSongAdapter.SongViewHolder>() {

    var songsList: MutableList<SingleSongbookSong> = mutableListOf()

    fun updateSongsList(newSongs: List<SingleSongbookSong>) {
        songsList = newSongs as MutableList<SingleSongbookSong>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        songsList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_single_songbook,
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
            view.findViewById<TextView>(R.id.tv_list_item_single_songbook_song_name);

        private val tvArtistName =
            view.findViewById(R.id.tv_list_item_single_songbook_artist_name) as TextView

        fun bind(song: SingleSongbookSong) {
            songNameText.text = song.songTitle
            tvArtistName.text = song.artist
        }
    }

}