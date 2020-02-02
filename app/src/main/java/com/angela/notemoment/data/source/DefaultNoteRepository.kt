package com.angela.notemoment.data.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultNoteRepository (private val NoteRemoteDataSource: NoteDataSource,
                             private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteRepository {

    override suspend fun getMarketingHots(){
        return NoteRemoteDataSource.getMarketingHots()
    }
}