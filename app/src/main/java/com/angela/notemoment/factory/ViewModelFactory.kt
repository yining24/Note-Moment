package com.angela.notemoment.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.list.ListViewModel

//Factory for all ViewModels

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val noteRepository: NoteRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
//                isAssignableFrom(MainViewModel::class.java) ->
//                    MainViewModel(noteRepository)
//
                isAssignableFrom(ListViewModel::class.java) ->
                    ListViewModel(noteRepository)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}