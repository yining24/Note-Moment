package com.angela.notemoment.ext

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.factory.ViewModelFactory
import com.angela.notemoment.factory.BoxViewModelFactory
import com.angela.notemoment.factory.DetailNoteViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(box: Box?): BoxViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return BoxViewModelFactory(repository, box)
}

fun Fragment.getVmFactory(note: Note, box: Box?): DetailNoteViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return DetailNoteViewModelFactory(repository, note, box)
}


fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun showToast(message: String) {

    val layoutInflater = LayoutInflater.from(NoteApplication.instance)
    val layout = layoutInflater.inflate(R.layout.item_list_note, null, false)

    Toast.makeText(NoteApplication.instance, message, Toast.LENGTH_SHORT).apply {
        this.setGravity(Gravity.CENTER, 0, 0)
//        this.view = layout
        show()
    }

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
            repository.updateBox(this, null)
            Logger.i("update box start time to ::${it.first().time}")
            Logger.i("update box end time to ::${it.last().time}")
        }
    }

}
