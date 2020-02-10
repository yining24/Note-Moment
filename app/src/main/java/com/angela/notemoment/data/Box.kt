package com.angela.notemoment.data

import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Box (
    var id: String = "",
    var title: String = "",
    var startDate: Long = 1,
    var endDate: Long = 1,
    var country: String = "臺灣",
    var image: String = ""
): Parcelable