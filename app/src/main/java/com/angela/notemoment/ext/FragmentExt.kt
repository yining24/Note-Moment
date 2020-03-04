package com.angela.notemoment.ext

import androidx.fragment.app.Fragment
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.factory.ViewModelFactory
import com.angela.notemoment.factory.BoxViewModelFactory
import com.angela.notemoment.factory.detailNoteViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(box: Box?): BoxViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return BoxViewModelFactory(repository, box)
}

fun Fragment.getVmFactory(note: Note, box: Box): detailNoteViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return detailNoteViewModelFactory(repository, note, box)
}



suspend fun Box.checkAndUpdateDate(repository: NoteRepository) {

    val list = when (val result = repository.getNote(this.id)) {
        is Result.Success -> {
            result.data
        }
        else -> null
    }

    list?.let {
        if (it.first().time != this.startDate || it.last().time != this.endDate) {
            this.startDate = it.first().time
            this.endDate = it.last().time
            repository.updateBox(this ,null)
            Logger.i("update box start time to ::${it.first().time}")
            Logger.i("update box end time to ::${it.last().time}")
        }
    }

}
