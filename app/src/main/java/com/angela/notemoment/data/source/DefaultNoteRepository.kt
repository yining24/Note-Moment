package com.angela.notemoment.data.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.local.NoteLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultNoteRepository (private val remoteDataSource: NoteDataSource,
                             private val localDataSource: NoteDataSource
) : NoteRepository {

    override fun getUser(id: String): LiveData<User> {
        return remoteDataSource.getUser(id)
    }

    override suspend fun getBox(): Result<List<Box>> {
        return remoteDataSource.getBox()
    }

    override suspend fun getNote(boxId:String): Result<List<Note>>{
        return remoteDataSource.getNote(boxId)
    }

    override suspend fun getAllNote(): Result<List<Note>>{
        return remoteDataSource.getAllNote()
    }

    override suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean>{
        return remoteDataSource.publishBox(box, uri)
    }

    override suspend fun updateBox(box: Box, uri: Uri?): Result<Boolean>{
        return remoteDataSource.updateBox(box, uri)
    }

    override suspend fun publishNote(note: Note, boxId:String, uri: Uri?): Result<Boolean> {
        return remoteDataSource.publishNote(note, boxId, uri)
    }

    override suspend fun delete(box: Box): Result<Boolean> {
        return remoteDataSource.delete(box)
    }
}