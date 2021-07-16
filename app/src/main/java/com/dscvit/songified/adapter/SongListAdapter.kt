package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.databinding.ListItemSongSearchResultBinding
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
        ListItemSongSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songsList[position])
    }

    override fun getItemCount() = songsList.size

    class SongViewHolder(val binding: ListItemSongSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.tvListItemSearchResultSongName.text = song.song_title
            binding.tvListItemSearchResultSongArtistName.text = song.artist.artist_title
        }
    }
}
