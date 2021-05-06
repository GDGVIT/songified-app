package com.dscvit.songified.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.R
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
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_song_info,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SongCommentViewHolder, position: Int) {
        holder.bind(songCommentsList[position])
    }

    override fun getItemCount() = songCommentsList.size

    class SongCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUserName=view.findViewById(R.id.tv_user_list_item_song_info) as TextView
        private val tvComment=view.findViewById(R.id.tv_details_list_item_song_info) as TextView

        fun bind(songComment: SongComment) {
            tvUserName.text = "${songComment.user.userName} | Level ${songComment.user.userLevel}"
            tvComment.text=songComment.comment


        }
    }

}