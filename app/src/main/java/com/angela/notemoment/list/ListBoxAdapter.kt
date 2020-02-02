package com.angela.notemoment.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.databinding.ItemListBoxBinding
import kotlin.random.Random

class ListBoxAdapter(val viewModel: ListViewModel) : ListAdapter<Box, ListBoxAdapter.ListBoxViewHolder>(DiffCallback) {



    class ListBoxViewHolder(private var binding: ItemListBoxBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(box: Box, viewModel: ListViewModel) {
            binding.box = box
            binding.viewModel = viewModel
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Box>() {
        override fun areItemsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBoxViewHolder {
        return ListBoxViewHolder(ItemListBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ListBoxViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.layoutParams.height = getRandomIntInRange(550, 400)
        holder.bind(item, viewModel)
    }

    private fun getRandomIntInRange(max: Int, min: Int): Int {
        return Random.nextInt(max - min + min) + min
    }
}