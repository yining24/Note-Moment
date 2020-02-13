package com.angela.notemoment.data.source.remote

import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NoteRemoteDataSource : NoteDataSource {


    private const val PATH_USER = "users"
    private const val PATH_BOX = "boxes"
    private const val PATH_NOTE = "notes"
    private const val KEY_CREATED_TIME = "createdTime"


    override suspend fun login(id: String): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getBox(): Result<List<Box>> =
        suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .collection(PATH_BOX)
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Box>()
                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val box = document.toObject(Box::class.java)
                        list.add(box)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.app_name)))
                }
            }
    }

//    override suspend fun getNote(boxId:String): Result<List<Note>> =
//        suspendCoroutine { continuation ->
//            FirebaseFirestore.getInstance()
//                .collection("users")
//                .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
//                .collection(PATH_BOX)
//                .document(boxId)
//                .collection(PATH_NOTE)
//                .orderBy("time", Query.Direction.ASCENDING)
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val list = mutableListOf<Note>()
//                        for (document in task.result!!) {
//                            Logger.d(document.id + " => " + document.data)
//                            val note = document.toObject(Note::class.java)
//                            list.add(note)
//                        }
//                        continuation.resume(Result.Success(list))
//                    } else {
//                        task.exception?.let {
//                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                            continuation.resume(Result.Error(it))
//                            return@addOnCompleteListener
//                        }
//                        continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.app_name)))
//                    }
//                }
//        }


    // modify note date structure
    override suspend fun getNote(boxId:String): Result<List<Note>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
//                .collection(PATH_BOX)
//                .document(boxId)
                .collection(PATH_NOTE)
                .orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Note>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)
                            val note = document.toObject(Note::class.java)
                            list.add(note)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.app_name)))
                    }
                }
        }




    override suspend fun publishBox(box: Box): Result<Boolean> =
        suspendCoroutine { continuation ->
            val boxes = FirebaseFirestore.getInstance().collection(PATH_USER)
            val document = boxes.document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            Logger.w("publish box uid::::${FirebaseAuth.getInstance().currentUser!!.uid}")
            val boxDocument = document
                .collection(PATH_BOX)
                .document()

            box.id = boxDocument.id

            boxDocument.set(box)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Logger.i("Publish box success : $box")
                        continuation.resume(Result.Success(true))

                    } else {
                        task.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.fail)))
                    }
                }
        }



    // modify note date structure
    override suspend fun publishNote(note: Note, boxId:String): Result<Boolean> =
        suspendCoroutine { continuation ->
        val boxes = FirebaseFirestore.getInstance().collection(PATH_USER)
        val document = boxes.document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
        document
//            .collection(PATH_BOX)
//            .document(boxId)
            .collection(PATH_NOTE)
            .add(note)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("Publish note success : $note")
                    continuation.resume(Result.Success(true))

                } else {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(NoteApplication.instance.getString(R.string.fail)))
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