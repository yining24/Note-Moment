package com.angela.notemoment.data.source

import android.net.Uri
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User

interface NoteDataSource {

    suspend fun login(id: String): Result<User>

    suspend fun getBox(): Result<List<Box>>

    suspend fun getNote(boxId:String): Result<List<Note>>

    suspend fun getAllNote(): Result<List<Note>>

    suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean>

    suspend fun publishNote(note: Note, boxId:String, uri: Uri?): Result<Boolean>

    suspend fun delete(box: Box): Result<Boolean>
}