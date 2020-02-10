package com.angela.notemoment.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.angela.notemoment.MainViewModel
import com.angela.notemoment.addbox.AddBoxViewModel
import com.angela.notemoment.addnote.AddNoteViewModel
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.list.ListViewModel
import com.angela.notemoment.listnote.ListNoteViewModel

/**
 * Factory for all ViewModels
 */

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val noteRepository: NoteRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(noteRepository)

                isAssignableFrom(ListViewModel::class.java) ->
                    ListViewModel(noteRepository)

                isAssignableFrom(AddBoxViewModel::class.java) ->
                    AddBoxViewModel(noteRepository)

                isAssignableFrom(AddNoteViewModel::class.java) ->
                    AddNoteViewModel(noteRepository)

                isAssignableFrom(ListNoteViewModel::class.java) ->
                    ListNoteViewModel(noteRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}