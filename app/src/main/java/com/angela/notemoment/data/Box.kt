package com.angela.notemoment.data


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Box (
    var id: String = "",
    var title: String = "",
    var startDate: Long = 0,
    var endDate: Long = 0,
    var country: String = "",
    var image: String = ""
): Parcelable