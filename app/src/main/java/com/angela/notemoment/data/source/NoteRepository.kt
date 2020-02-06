package com.angela.notemoment.data.source

import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User

interface NoteRepository {

    suspend fun login(id: String): Result<User>

    suspend fun getBox(): Result<List<Box>>

    suspend fun publishBox(box: Box): Result<Boolean>

    suspend fun delete(box: Box): Result<Boolean>

}