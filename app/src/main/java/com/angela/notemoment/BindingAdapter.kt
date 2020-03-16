package com.angela.notemoment

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.listbox.ListBoxAdapter
import com.angela.notemoment.listnote.ListNoteSortedAdapter
import com.angela.notemoment.map.MyMapNoteAdapter
import com.angela.notemoment.util.GlideApp
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


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

@BindingAdapter("markerNotes")
fun bindRecyclerViewMarkerNotes(recyclerView: RecyclerView, noteItems: List<Note>?) {
    noteItems?.let {
        if (it.size > 1) {
            recyclerView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                (NoteApplication.instance.resources.getDimensionPixelSize(R.dimen.map_note_height).toFloat() * 1.5).toInt()
            )
        } else {
            recyclerView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                NoteApplication.instance.resources.getDimensionPixelSize(R.dimen.map_note_height)
            )
        }
    }
    val adapter = recyclerView.adapter as MyMapNoteAdapter
    adapter.submitList(noteItems)
}


@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        LoadApiStatus.DONE, LoadApiStatus.ERROR -> view.visibility = View.GONE
    }
}


@BindingAdapter("requestFocus")
fun requestFocus(view: TextView, requestFocus: Boolean) {
    when (requestFocus) {
        true -> {
            view.isFocusableInTouchMode = true
            view.isFocusable = true
        }
        false -> {
            view.isFocusableInTouchMode = false
            view.isFocusable = false
        }
    }
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrlBox")
fun bindImageBox(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(20))
                    .placeholder(R.drawable.placeholder_logo_background2)
                    .error(R.drawable.placeholder_logo_background2)
                    ).into(imgView)
    }
}

@BindingAdapter("imageUrlNote")
fun bindImageNote(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().transform(CenterCrop(), RoundedCorners(10))
                    .placeholder(R.drawable.placeholder_logo_background2)
                    .error(R.drawable.placeholder_logo_background2))
            .into(imgView)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().centerCrop()
                    .placeholder(R.drawable.placeholder_logo_background2)
                    .error(R.drawable.placeholder_logo_background2))
            .into(imgView)
    }
}

@BindingAdapter("imageUrlPlaceholderWide")
fun bindImagePlaceholderWide(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().centerCrop()
                    .placeholder(R.drawable.placeholder_logo_wide)
                    .error(R.drawable.placeholder_logo_wide))
            .into(imgView)
    }
}


@BindingAdapter("imageUrlAddBox")
fun bindImageAddBox(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().centerCrop()
                    .placeholder(R.drawable.placeholder_logo_add_gray)
                    .error(R.drawable.placeholder_logo_add_gray))
            .into(imgView)
    }
}


@BindingAdapter("imageUrlUser")
fun bindImageUser(imgView: ImageView, imgUrl: Uri?) {
    imgUrl?.let {
        val imgUri = it.buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.icon_user)
                    .error(R.drawable.icon_user))
            .into(imgView)
    }
}



@BindingAdapter("year")
fun bindTimeYear(textView: TextView, time: Long?) {
    time?.let {
        val sdf = SimpleDateFormat("yyyy")
        textView.text = sdf.format(time)
    }
}
@BindingAdapter("month")
fun bindTimeMonth(textView: TextView, time: Long?) {
    time?.let {
        val sdf = SimpleDateFormat("MMM")
        textView.text = sdf.format(time)
    }
}
@BindingAdapter("day")
fun bindTimeDay(textView: TextView, time: Long?) {
    time?.let {
        val sdf = SimpleDateFormat("dd")
        textView.text = sdf.format(time)
    }
}
@BindingAdapter("time")
fun bindTime(textView: TextView, time: Long?) {
    time?.let {
        val sdf = SimpleDateFormat("HH:mm")
        textView.text = sdf.format(time)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("contentLength")
fun bindContent(textView: TextView, string: String?) {
    string?.let {
        textView.text = "${it.length}/500"
    }
}