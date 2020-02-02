package com.angela.notemoment

import android.app.Application
import com.angela.notemoment.data.source.NoteRepository
import kotlin.properties.Delegates

class NoteApplication : Application() {

    val noteRepository: NoteRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: NoteApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
