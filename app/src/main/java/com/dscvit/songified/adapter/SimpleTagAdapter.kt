package com.dscvit.songified.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.songified.R

class SimpleTagAdapter: RecyclerView.Adapter<SimpleTagAdapter.SimpleTageViewHolder>() {

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
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_simple_tag,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SimpleTageViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount() = tagList.size

    class SimpleTageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTagName = view.findViewById<TextView>(R.id.text_simple_tag);
        //val bgArray= arrayOf(R.drawable.simple_tag_bg_3,R.drawable.simple_tag_bg_1,R.drawable.simple_tag_bg_2)

        fun bind(tag: String) {
            tvTagName.text = tag
           // tvTagName.setBackgroundResource(bgArray.random())

        }
    }

}