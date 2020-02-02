package com.angela.notemoment.ext

import androidx.fragment.app.Fragment
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.factory.ViewModelFactory


fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as NoteApplication).noteRepository
    return ViewModelFactory(repository)
}
