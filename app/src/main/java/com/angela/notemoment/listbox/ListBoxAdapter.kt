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
                holder.itemView.layoutParams.height = getRandomIntInRange(550, 450)
                holder.bind(item, boxViewModel)
                holder.itemView.setOnClickListener {
                    boxViewModel.selectBox(item)
                }
            }

    private fun getRandomIntInRange(max: Int, min: Int): Int {
        return Random.nextInt(max - min + 30) + min
    }
}