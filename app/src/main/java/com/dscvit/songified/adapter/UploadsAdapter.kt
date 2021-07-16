package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.R
import com.dscvit.songified.databinding.ListItemPreviousUploadsBinding
import com.dscvit.songified.model.UploadedSong

class UploadsAdapter : RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder>() {

    var uploadsList: MutableList<UploadedSong> = mutableListOf()

    fun updateUploads(newUploads: List<UploadedSong>) {
        uploadsList = newUploads as MutableList<UploadedSong>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        uploadsList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UploadsViewHolder(
        ListItemPreviousUploadsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: UploadsViewHolder, position: Int) {
        holder.bind(uploadsList[position])
    }

    override fun getItemCount() = uploadsList.size

    class UploadsViewHolder(val binding: ListItemPreviousUploadsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uploadedSong: UploadedSong) {

            binding.tvListItemPreviousUploadSongName.text = uploadedSong.name
            binding.tvStatusListItemPreviousUpload.text = "Status : ${uploadedSong.status}"

            when (uploadedSong.status) {
                "Finished" -> {

                    binding.imgListItemStatusPreviousUpload.setImageResource(R.drawable.semi_ring_green)
                }
                "Processing" -> {

                    binding.imgListItemStatusPreviousUpload.setImageResource(R.drawable.semi_ring_blue)
                }
            }
        }
    }
}
