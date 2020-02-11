package com.angela.notemoment

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.list.ListBoxAdapter
import com.angela.notemoment.listnote.ListNoteItemAdapter
import com.angela.notemoment.listnote.ListNoteSortedAdapter

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
    val adapter = recyclerView.adapter as ListNoteSortedAdapter
    adapter.submitList(noteItems)
}


//@BindingAdapter("showNoteDate")
//fun bindNoteDate(textView: TextView, time: Long?) {
//    time?.let { textView.text = "$it" }
//}