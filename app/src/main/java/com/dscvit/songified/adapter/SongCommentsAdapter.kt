package com.dscvit.songified.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.R
import com.dscvit.songified.databinding.ListItemSimpleSongbooksBinding
import com.dscvit.songified.databinding.ListItemSongInfoBinding
import com.dscvit.songified.model.Song
import com.dscvit.songified.model.SongComment

class SongCommentsAdapter : RecyclerView.Adapter<SongCommentsAdapter.SongCommentViewHolder>() {

    var songCommentsList: MutableList<SongComment> = mutableListOf()

    fun updateSongCommentsList(newComments: List<SongComment>) {
        songCommentsList = newComments as MutableList<SongComment>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        songCommentsList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SongCommentViewHolder(
        ListItemSongInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongCommentViewHolder, position: Int) {
        holder.bind(songCommentsList[position])
    }

    override fun getItemCount() = songCommentsList.size

    class SongCommentViewHolder(val binding: ListItemSongInfoBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(songComment: SongComment) {
            
            binding.tvUserListItemSongInfo.text = "${songComment.user.userName} | Level ${songComment.user.userLevel}"
            binding.tvDetailsListItemSongInfo.text=songComment.comment


        }
    }

}