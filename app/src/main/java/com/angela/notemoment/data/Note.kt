package com.angela.notemoment.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Note(
    var title:      String          = "",
    var time:       Long            = 1,
    var content:    String          = "",
    var locateName:   String        = "",
    var locateLatLng: LatLng = LatLng(0.0, 0.0),
    var tags:       String          = "",
    var images:     List<String>    = listOf(),
    var boxId :     String          = ""
): Parcelable


//@Parcelize
//data class Note(
//    var title:      String          = "",
//    var time:       Long            = 1,
//    var content:    String          = "",
//    var locateName:   String        = "",
//    var locateLatLng: LatLng = LatLng(0.0, 0.0),
//    var tags:       String          = "",
//    var images:     List<String>    = listOf(),
//    var boxId :     String          = ""
//): Parcelable