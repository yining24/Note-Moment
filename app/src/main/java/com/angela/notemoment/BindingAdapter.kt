package com.angela.notemoment

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.list.ListBoxAdapter
import com.angela.notemoment.listnote.ListNoteSortedAdapter
import com.angela.notemoment.util.GlideApp
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


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


/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrlBox")
fun bindImageBox(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
//            .apply(
//                RequestOptions().transform(CenterCrop(), RoundedCorners(20))
//                    .placeholder(R.drawable.bg_list_box_frame)
//                    .error(R.drawable.bg_list_box_frame)
//                    )

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
                RequestOptions().transform(CenterCrop(), RoundedCorners(10))
                    .placeholder(R.drawable.icon_photo_gray)
                    .error(R.drawable.icon_photo_gray))
            .into(imgView)
    }
}

