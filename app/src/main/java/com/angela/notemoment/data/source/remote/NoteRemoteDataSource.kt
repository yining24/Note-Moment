package com.angela.notemoment.data.source.remote

import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NoteRemoteDataSource : NoteDataSource {

    override suspend fun login(id: String): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getBox(): Result<List<Box>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection("users")
            .orderBy("startTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Box>()
                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val article = document.toObject(Box::class.java)
                        list.add(article)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.app_name)))
                }
            }
    }

    override suspend fun publishBox(box: Box): Result<Boolean> =
        suspendCoroutine { continuation ->
            val boxes = FirebaseFirestore.getInstance().collection("users")
            val document = boxes.document()

            box.id = document.id

            document
                .set(box)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Publish: $box")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.app_name)))
                    }
                }
        }

    override suspend fun delete(box: Box): Result<Boolean> =
        suspendCoroutine { continuation ->

            when {
                box.title == "waynechen323"
                        && box.title.toLowerCase(Locale.TAIWAN) != "test"
                        && box.title.trim().isNotEmpty() -> {

                    continuation.resume(Result.Fail("You know nothing!! ${box.title}"))
                }
                else -> {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(box.id)
                        .delete()
                        .addOnSuccessListener {
                            Logger.i("Delete: $box")

                            continuation.resume(Result.Success(true))
                        }.addOnFailureListener {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                }
            }

        }


}