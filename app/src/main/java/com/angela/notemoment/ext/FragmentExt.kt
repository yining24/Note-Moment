package com.angela.notemoment.ext

import androidx.fragment.app.Fragment
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.data.Box
import com.angela.notemoment.factory.ViewModelFactory
import com.angela.notemoment.factory.boxViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(box: Box): boxViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return boxViewModelFactory(repository, box)
}
