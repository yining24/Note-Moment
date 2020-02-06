package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Note(
    val title: String,
    val date: String,
    val time: String,
    val content: String,
    val location: String,
    val tags: List<String>,
    val images: List<String>,
    val boxId : String
): Parcelable