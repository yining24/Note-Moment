package com.angela.notemoment.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User (
    val id: String =  "",
    val name: String = "",
    val title: String = "Travel around the world"
) : Parcelable