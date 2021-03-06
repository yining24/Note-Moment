package com.angela.notemoment.data.source.local

import com.angela.notemoment.data.Result
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource
import com.google.firebase.auth.FirebaseUser

class NoteLocalDataSource (val context: Context) : NoteDataSource {

    override fun getUser(id: String): LiveData<User>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateUser(user: User): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun checkUser(id: String): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override suspend fun getBox(): Result<List<Box>>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNote(boxId:String): Result<List<Note>>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAllNote(): Result<List<Note>>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLiveNotes(boxId:String): LiveData<List<Note>>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateBox(box: Box, uri: Uri?): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateNote(note: Note, uri: Uri?): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun publishNote(note: Note, boxId:String, uri: Uri?): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}