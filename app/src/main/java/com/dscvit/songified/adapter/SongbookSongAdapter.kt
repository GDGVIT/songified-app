package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.dscvit.songified.R
import com.dscvit.songified.databinding.ListItemSimpleSongbooksBinding
import com.dscvit.songified.databinding.ListItemSingleSongbookBinding
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
        ListItemSingleSongbookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songsList[position])
    }

    override fun getItemCount() = songsList.size

    class SongViewHolder(val binding: ListItemSingleSongbookBinding) : RecyclerView.ViewHolder(binding.root) {




        fun bind(song: SingleSongbookSong) {
            binding.tvListItemSingleSongbookSongName.text = song.songTitle
            binding.tvListItemSingleSongbookArtistName.text = song.artist
        }
    }

}