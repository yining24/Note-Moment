package com.angela.notemoment.data.source

import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.local.NoteLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultNoteRepository (private val remoteDataSource: NoteDataSource,
                             private val localDataSource: NoteDataSource
) : NoteRepository {

    override suspend fun login(id: String): Result<User> {
        return localDataSource.login(id)
    }

    override suspend fun getBox(): Result<List<Box>> {
        return remoteDataSource.getBox()
    }

    override suspend fun publishBox(box: Box): Result<Boolean> {
        return remoteDataSource.publishBox(box)
    }

    override suspend fun delete(box: Box): Result<Boolean> {
        return remoteDataSource.delete(box)
    }
}