package com.angela.notemoment.util

import com.angela.notemoment.NoteApplication


object Util {


    fun getString(resourceId: Int): String {
        return NoteApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return NoteApplication.instance.getColor(resourceId)
    }





}
