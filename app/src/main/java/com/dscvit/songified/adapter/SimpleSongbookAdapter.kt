package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.dscvit.songified.R
import com.dscvit.songified.databinding.ListItemSimpleSongbooksBinding
import com.dscvit.songified.model.Songbook


class SimpleSongbookAdapter : RecyclerView.Adapter<SimpleSongbookAdapter.SongbookViewHolder>() {

    var songbookList: MutableList<Songbook> = mutableListOf()

    fun updateSongbookList(newSongs: List<Songbook>) {
        songbookList = newSongs as MutableList<Songbook>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        songbookList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongbookViewHolder(
        ListItemSimpleSongbooksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongbookViewHolder, position: Int) {
        holder.bind(songbookList[position])
    }

    override fun getItemCount() = songbookList.size

    class SongbookViewHolder(val binding: ListItemSimpleSongbooksBinding) : RecyclerView.ViewHolder(binding.root) {



        fun bind(songbook: Songbook) {
            binding.tvListItemSimpleSongbookName.text = songbook.name

        }
    }

}