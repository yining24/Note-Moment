package com.angela.notemoment.data.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.google.firebase.auth.FirebaseUser

interface NoteDataSource {

    fun getUser(id: String): LiveData<User>

    suspend fun updateUser(user: User): Result<Boolean>

    suspend fun checkUser(id: String): Result<Boolean>

    suspend fun getBox(): Result<List<Box>>

    suspend fun getNote(boxId:String): Result<List<Note>>

    suspend fun getAllNote(): Result<List<Note>>

    suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean>

    suspend fun updateBox(box: Box, uri: Uri?): Result<Boolean>

    suspend fun updateNote(note: Note, uri: Uri?): Result<Boolean>

    suspend fun publishNote(note: Note, boxId:String, uri: Uri?): Result<Boolean>

    suspend fun delete(box: Box): Result<Boolean>
}