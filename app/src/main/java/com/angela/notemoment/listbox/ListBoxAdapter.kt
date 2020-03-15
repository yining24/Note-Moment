package com.angela.notemoment.listbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.databinding.ItemListDrawerBinding
import kotlin.random.Random


class ListBoxAdapter(val boxViewModel: ListBoxViewModel) : ListAdapter<Box, ListBoxAdapter.ListBoxViewHolder>(DiffCallback) {


    class ListBoxViewHolder(private var binding: ItemListDrawerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(box: Box, viewModel: ListBoxViewModel) {
            binding.box = box
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Box>() {
        override fun areItemsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBoxViewHolder {
        return ListBoxViewHolder(
                ItemListDrawerBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }

    override fun onBindViewHolder(holder: ListBoxViewHolder, position: Int) {
                val item = getItem(position)
                holder.itemView.layoutParams.height = getRandomIntInRange(550, 500)
                holder.bind(item, boxViewModel)
                holder.itemView.setOnClickListener {
                    boxViewModel.selectBox(item)
                }
            }

    private fun getRandomIntInRange(max: Int, min: Int): Int {
        return Random.nextInt(max - min + 30) + min
    }
}