package com.angela.notemoment.data.source.local

import com.angela.notemoment.data.Result
import android.content.Context
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource

class NoteLocalDataSource (val context: Context) : NoteDataSource {

    override suspend fun login(id: String): Result<User>{
        return when (id) {
            "waynechen323" -> Result.Success((User(
                id,
                "AKA小安老師",
                "wayne@school.appworks.tw"
            )))
            "dlwlrma" -> Result.Success((User(
                id,
                "IU",
                "dlwlrma@school.appworks.tw"
            )))
            //TODO add your profile here
            else -> Result.Fail("You have to add $id info in local data source")
        }
    }

    override suspend fun getBox(): Result<List<Box>>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override suspend fun publishBox(box: Box): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun delete(box: Box): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}