package com.angela.notemoment.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.angela.notemoment.MainViewModel
import com.angela.notemoment.addbox.AddBoxViewModel
import com.angela.notemoment.addnote.AddNoteViewModel
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.detailnote.DetailNoteViewModel
import com.angela.notemoment.listbox.ListBoxViewModel
import com.angela.notemoment.listnote.ListNoteViewModel
import com.angela.notemoment.map.MyMapViewModel
import com.angela.notemoment.profile.ProfileViewModel

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

                isAssignableFrom(ListBoxViewModel::class.java) ->
                    ListBoxViewModel(noteRepository)

                isAssignableFrom(AddBoxViewModel::class.java) ->
                    AddBoxViewModel(noteRepository)

                isAssignableFrom(AddNoteViewModel::class.java) ->
                    AddNoteViewModel(noteRepository)

                isAssignableFrom(MyMapViewModel::class.java) ->
                    MyMapViewModel(noteRepository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(noteRepository)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

/**
* Factory for all ViewModels which need [key].
*/

@Suppress("UNCHECKED_CAST")
class boxViewModelFactory(
    private val noteRepository: NoteRepository,
    private val box: Box
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ListNoteViewModel::class.java) ->
                    ListNoteViewModel(noteRepository, box)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

@Suppress("UNCHECKED_CAST")
class detailNoteViewModelFactory(
    private val noteRepository: NoteRepository,
    private val note: Note,
    private val box: Box
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailNoteViewModel::class.java) ->
                    DetailNoteViewModel(noteRepository, note, box)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}