package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dscvit.songified.R
import com.dscvit.songified.databinding.ListItemSingleSongbookBinding
import com.dscvit.songified.model.SingleSongbookSong

class SongbookSongAdapter : RecyclerView.Adapter<SongbookSongAdapter.SongViewHolder>() {

    var songsList: MutableList<SingleSongbookSong> = mutableListOf()
    var selectedSongBookId = ""

    fun updateSongsList(newSongs: List<SingleSongbookSong>, songbookId: String) {
        songsList = newSongs as MutableList<SingleSongbookSong>
        selectedSongBookId = songbookId
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
            Glide.with(binding.root.context)
                .load(song.coverArt)
                .fallback(R.drawable.fallback_cover_art)
                .placeholder(R.drawable.fallback_cover_art)
                .into(binding.imgListItemCoverSongbookSong)
        }
    }
}
