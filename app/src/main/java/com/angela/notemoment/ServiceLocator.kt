package com.angela.notemoment

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.angela.notemoment.data.source.DefaultNoteRepository
import com.angela.notemoment.data.source.NoteDataSource
import com.angela.notemoment.data.source.NoteRemoteDataSource
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.data.source.local.NoteLocalDataSource

object ServiceLocator {

    @Volatile
    var noteRepository: NoteRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): NoteRepository {
        synchronized(this) {
            return noteRepository
                ?: noteRepository
                ?: createNoteRepository(context)
        }
    }

    private fun createNoteRepository(context: Context): NoteRepository {
        return DefaultNoteRepository(
            NoteRemoteDataSource
//            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): NoteDataSource {
        return NoteLocalDataSource(context)
    }
}