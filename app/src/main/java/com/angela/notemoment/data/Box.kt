package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Box (
    val title: String,
    val date: String,
    val image: String
): Parcelable