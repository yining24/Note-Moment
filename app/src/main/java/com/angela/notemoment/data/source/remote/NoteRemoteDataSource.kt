package com.angela.notemoment.data.source.remote

import android.net.Uri
import android.widget.Toast
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.net.URI
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NoteRemoteDataSource : NoteDataSource {


    private const val PATH_USER = "users"
    private const val PATH_BOX = "boxes"
    private const val PATH_NOTE = "notes"

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


    // modify note date structure
    override suspend fun getNote(boxId:String): Result<List<Note>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .collection(PATH_NOTE)
                .whereEqualTo("boxId",boxId)
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


    override suspend fun getAllNote(): Result<List<Note>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_USER)
                .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .collection(PATH_NOTE)
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



    override suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference
            val firebaseStore = FirebaseFirestore.getInstance().collection(PATH_USER)
            val document = firebaseStore.document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            Logger.w("publish box uid::::${FirebaseAuth.getInstance().currentUser!!.uid}")

            val boxDocument = document
                .collection(PATH_BOX)
                .document()

            box.id = boxDocument.id

            if (uri != null) {
                val ref = storageReference.child("uploads/" + UUID.randomUUID().toString())
                val uploadTask = ref.putFile(uri)
                uploadTask.continueWithTask(
                    Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                        if (!taskImage.isSuccessful) {
                            taskImage.exception?.let {
                                Logger.i("task is not successful")
                                throw it
                            }
                        }
                        return@Continuation ref.downloadUrl
                    })
                    .addOnCompleteListener { taskImage ->
                        if (taskImage.isSuccessful) {
                            Logger.i("task is successful")
                            Logger.i(" box taskImage = ${taskImage.result}")

                            box.image = taskImage.result.toString()

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
                    }.addOnFailureListener{
                        Logger.i("task is Failure")
                    }
            } else {
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
        }



    override suspend fun publishNote(note: Note, boxId:String, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference
            val firebaseStore = FirebaseFirestore.getInstance().collection(PATH_USER)

        val document = firebaseStore.document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            val noteDocument =
                document
                .collection(PATH_NOTE)

            if (uri != null) {
                val ref = storageReference.child("uploads/" + UUID.randomUUID().toString())
                val uploadTask = ref.putFile(uri)
                uploadTask.continueWithTask(
                    Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                        if (!taskImage.isSuccessful) {
                            taskImage.exception?.let {
                                Logger.i("task is not successful")
                                throw it
                            }
                        }
                        return@Continuation ref.downloadUrl
                    })
                    .addOnCompleteListener { taskImage ->
                        if (taskImage.isSuccessful) {
                            Logger.i("task is successful")
                            Logger.i(" note taskImage = ${taskImage.result}")

                            note.images = taskImage.result.toString()

                            noteDocument.add(note)
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
                                        continuation.resume(
                                            Result.Fail(
                                                NoteApplication.instance.getString(
                                                    R.string.fail
                                                )
                                            )
                                        )
                                    }
                                }
                        }
                    }.addOnFailureListener{
                        Logger.i("task is Failure")
                    }
            } else {
                noteDocument.add(note)
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