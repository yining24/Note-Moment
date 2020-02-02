package com.angela.notemoment.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.factory.ViewModelFactory


//fun Activity.getVmFactory(): ViewModelFactory {
//    val repository = (applicationContext as NoteApplication).noteRepository
//    return ViewModelFactory(repository)
//}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}