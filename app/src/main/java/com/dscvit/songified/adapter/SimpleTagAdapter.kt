package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.databinding.ListItemSimpleTagBinding

class SimpleTagAdapter : RecyclerView.Adapter<SimpleTagAdapter.SimpleTageViewHolder>() {

    var tagList: MutableList<String> = mutableListOf()

    fun updateTagsList(newTags: List<String>) {
        tagList = newTags as MutableList<String>
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        tagList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimpleTageViewHolder(
        ListItemSimpleTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SimpleTageViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount() = tagList.size

    class SimpleTageViewHolder(val binding: ListItemSimpleTagBinding) : RecyclerView.ViewHolder(binding.root) {

        // val bgArray= arrayOf(R.drawable.simple_tag_bg_3,R.drawable.simple_tag_bg_1,R.drawable.simple_tag_bg_2)

        fun bind(tag: String) {
            binding.textSimpleTag.text = tag
            // tvTagName.setBackgroundResource(bgArray.random())
        }
    }
}
