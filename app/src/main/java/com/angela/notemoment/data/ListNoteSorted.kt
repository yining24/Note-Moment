package com.angela.notemoment.data

data class ListNoteSorted (
    var date : String = "",
    var notes: MutableList<Note> = mutableListOf()
)
//{
//
//
//    fun toListNoteSorted(notes: List<Note>): List<ListNoteSorted> {
//
//        val sortedNotes = mutableListOf<ListNoteSorted>()
//
//        var tempObj: ListNoteSorted? = null
//
//        for (note in notes) {
//
//            fun getDateByTime(time: Long): String {
//
//                return ""
//            }
//
//            if (getDateByTime(note.time) != tempObj?.date ?: "") {
//                tempObj = ListNoteSorted
//                tempObj.date = getDateByTime(note.time)
//
//            } else {
//
//            }
//
//            (notes as MutableList<Note>).add(note)
//
//        }
//
//        return sortedNotes
//    }
//
//
//}