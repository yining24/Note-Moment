package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Note(
    var title:      String          = "",
    var time:       Long            = 1,
    var content:    String          = "",
    var location:   String          = "",
    var tags:       String          = "",
    var images:     List<String>    = listOf(),
    var boxId :     String          = ""
): Parcelable