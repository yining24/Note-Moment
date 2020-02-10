package com.angela.notemoment.listnote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.ItemListNoteBinding

class ListNoteItemAdapter (val viewModel: ListNoteViewModel) : ListAdapter<Note, ListNoteItemAdapter.ListNoteItemViewHolder>(DiffCallback) {



    class ListNoteItemViewHolder(private var binding: ItemListNoteBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note, viewModel: ListNoteViewModel) {
            binding.note = note
            binding.viewModel = viewModel
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNoteItemViewHolder {
        return ListNoteItemViewHolder(ItemListNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ListNoteItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }

}