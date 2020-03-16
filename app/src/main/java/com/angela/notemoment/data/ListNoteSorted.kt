package com.angela.notemoment.data

data class ListNoteSorted (
    var date : String = "",
    var notes: MutableList<Note> = mutableListOf()
)
