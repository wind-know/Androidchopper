package com.example.androidchopper.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchopper.databinding.ItemChapterBinding

class ChapterAdapter(
    private val onChapterClick: (String) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private var chapterList: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapterList[position]
        holder.binding.chapterText.text = chapter
        holder.itemView.setOnClickListener {
            onChapterClick(chapter)
        }
    }

    override fun getItemCount(): Int = chapterList.size

    fun submitList(newList: List<String>) {
        chapterList = newList
        notifyDataSetChanged()
    }

    class ChapterViewHolder(val binding: ItemChapterBinding) : RecyclerView.ViewHolder(binding.root)
}