package com.angela.notemoment

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.list.ListBoxAdapter
import com.angela.notemoment.listnote.ListNoteAdapter

@BindingAdapter("boxes")
fun bindRecyclerView(recyclerView: RecyclerView, boxItems: List<Box>?) {
    boxItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ListBoxAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("noteSorted")
fun bindRecyclerViewNoteSorted(recyclerView: RecyclerView, noteItems: List<ListNoteSorted>?) {
    val adapter = recyclerView.adapter as ListNoteAdapter
    adapter.submitList(noteItems)

}