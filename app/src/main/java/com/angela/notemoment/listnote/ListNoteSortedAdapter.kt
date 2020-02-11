package com.angela.notemoment.listnote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.ItemListNoteSortedBinding

class ListNoteSortedAdapter (val viewModel: ListNoteViewModel) : ListAdapter<ListNoteSorted, ListNoteSortedAdapter.ListNoteViewHolder>(DiffCallback) {



    class ListNoteViewHolder(private var binding: ItemListNoteSortedBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(listNoteSorted: ListNoteSorted, viewModel: ListNoteViewModel) {
            binding.listNoteSorted = listNoteSorted
            binding.viewModel = viewModel

            val adapter= ListNoteItemAdapter(viewModel)
            binding.recyclerListNote.adapter = adapter

            adapter.submitList(listNoteSorted.notes)

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ListNoteSorted>() {
        override fun areItemsTheSame(oldItem: ListNoteSorted, newItem: ListNoteSorted): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ListNoteSorted, newItem: ListNoteSorted): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNoteViewHolder {
        return ListNoteViewHolder(ItemListNoteSortedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ListNoteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }

}