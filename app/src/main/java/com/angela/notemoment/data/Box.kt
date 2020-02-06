package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Box (
    var id: String = "",
    val title: String,
    val startDate: String,
    val endDate: String,
    val image: String
): Parcelable