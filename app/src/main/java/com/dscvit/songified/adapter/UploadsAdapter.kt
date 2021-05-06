package com.dscvit.songified.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.R
import com.dscvit.songified.model.Songbook
import com.dscvit.songified.model.UploadedSong

class UploadsAdapter: RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder>() {

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
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_previous_uploads,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: UploadsViewHolder, position: Int) {
        holder.bind(uploadsList[position])
    }

    override fun getItemCount() = uploadsList.size

    class UploadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSongName = view.findViewById<TextView>(R.id.tv_list_item_previous_upload_song_name);
        private val tvStatus=view.findViewById(R.id.tv_status_list_item_previous_upload) as TextView
        private val imgStatus=view.findViewById(R.id.img_list_item_status_previous_upload) as ImageView
        private val divider=view.findViewById(R.id.divider_previous_uploads) as View
        fun bind(uploadedSong: UploadedSong) {

            tvSongName.text = uploadedSong.name
            tvStatus.text="Status : ${uploadedSong.status}"

            when(uploadedSong.status){
                "Finished"->{

                    imgStatus.setImageResource(R.drawable.semi_ring_green)
                }
                "Processing"->{

                    imgStatus.setImageResource(R.drawable.semi_ring_blue)
                }

            }



        }
    }

}