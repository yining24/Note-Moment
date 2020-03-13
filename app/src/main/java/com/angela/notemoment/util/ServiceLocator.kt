package com.angela.notemoment.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.angela.notemoment.data.source.DefaultNoteRepository
import com.angela.notemoment.data.source.NoteDataSource
import com.angela.notemoment.data.source.remote.NoteRemoteDataSource
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.data.source.local.NoteLocalDataSource

object ServiceLocator {

    @Volatile
    var repository: NoteRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): NoteRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createNoteRepository(context)
        }
    }

    private fun createNoteRepository(context: Context): NoteRepository {
        return DefaultNoteRepository(
            NoteRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): NoteDataSource {
        return NoteLocalDataSource(context)
    }
}