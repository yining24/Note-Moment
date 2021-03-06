package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User (
    val id: String =  "",
    var name: String = "",
    var title: String = "Spot Moment",
    val email: String = ""
) : Parcelable


