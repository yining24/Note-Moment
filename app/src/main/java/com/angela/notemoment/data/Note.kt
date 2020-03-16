package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Note(
    var title:      String          = "",
    var time:       Long            = 1,
    var content:    String          = "",
    var locateName: String          = "",
    var lat:        Double          = 0.0,
    var lng:        Double          = 0.0,
    var tags:       String          = "",
    var images:     String          = "",
    var boxId :     String          = "",
    var id :        String          = ""
): Parcelable
