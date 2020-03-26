package com.angela.notemoment.data.source.remote

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteDataSource
import com.angela.notemoment.login.UserManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NoteRemoteDataSource : NoteDataSource {


    private const val PATH_USER = "users"
    private const val PATH_BOX = "boxes"
    private const val PATH_NOTE = "notes"
    private const val APP_TITLE = "Spot Moment"
    private const val UPLOADS = "uploads/"

    private val firebaseStore = FirebaseFirestore.getInstance().collection(PATH_USER)


    override fun getUser(id: String): LiveData<User> {
        val user = MutableLiveData<User>()
        firebaseStore
            .document(id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    user.value = snapshot.toObject(User::class.java)
                    Logger.i("getUser:::${user.value}")
                } else {
                    Logger.d("Current data: null")
                }
            }
        return user
    }


    override suspend fun updateUser(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->

            firebaseStore
                .document(user.id)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("task is successful")
                        Logger.i("update user = $user")

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


    override suspend fun checkUser(id: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            FirebaseAuth.getInstance().currentUser?.let { user ->
                firebaseStore
                    .document(id)
                    .get()
                    .addOnSuccessListener {
                        Logger.w("check snap shot :: $it")
                        if (!it.exists()) {
                            firebaseStore
                                .document(id)
                                .set(
                                    User(
                                        user.uid,
                                        user.displayName ?: "",
                                        APP_TITLE,
                                        user.email ?: ""
                                    )
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Logger.w("First login is Successful set : ${user.displayName}")
                                        continuation.resume(Result.Success(true))
                                    } else {
                                        task.exception?.let { e ->
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                            continuation.resume(Result.Error(e))
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

                        } else {
                            continuation.resume(Result.Success(true))
                            Logger.w("[${this::class.simpleName}]Not first login : ${user.displayName}")
                        }
                    }
            }
        }


    override suspend fun getBox(): Result<List<Box>> =
        suspendCoroutine { continuation ->
            if (UserManager.isLogin) {
                firebaseStore
                    .document(UserManager.userId!!)
                    .collection(PATH_BOX)
                    .orderBy("endDate", Query.Direction.DESCENDING)
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
            } else {
                continuation.resume(Result.Fail("User not logged in"))
            }

        }


    override suspend fun getNote(boxId: String): Result<List<Note>> =
        suspendCoroutine { continuation ->
            firebaseStore
                .document(UserManager.userId?: "")
                .collection(PATH_NOTE)
                .whereEqualTo("boxId", boxId)
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
            firebaseStore
                .document(UserManager.userId?: "")
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


    override fun getLiveNotes(boxId: String): LiveData<List<Note>> {
        val notes = MutableLiveData<List<Note>>()
        firebaseStore
            .document(UserManager.userId?: "")
            .collection(PATH_NOTE)
            .whereEqualTo("boxId", boxId)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val list = mutableListOf<Note>()
                    for (document in it) {
                        Logger.d(document.id + " => " + document.data)
                        val note = document.toObject(Note::class.java)
                        list.add(note)
                    }
                    notes.value = list
                }
            }
        return notes
    }


    override suspend fun publishBox(box: Box, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference

            val boxDocument = firebaseStore
                .document(UserManager.userId ?: "")
                .collection(PATH_BOX)
                .document()

            box.id = boxDocument.id

            if (uri != null) {
                val ref = storageReference.child(UPLOADS + UUID.randomUUID().toString())
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
                    }.addOnFailureListener {
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


    override suspend fun updateBox(box: Box, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference
            Logger.w("update box uid::::${box.id}")

            val boxDocument = firebaseStore.document(UserManager.userId ?:"")
                .collection(PATH_BOX)
                .document(box.id)

            if (uri != null) {
                val ref = storageReference.child(UPLOADS + UUID.randomUUID().toString())
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

                                        Logger.i("update box success : $box")
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
                    }.addOnFailureListener {
                        Logger.i("task is Failure")
                    }
            } else {
                boxDocument.set(box)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Logger.i("update box success : $box")
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


    override suspend fun updateNote(note: Note, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference

            val noteDocument = firebaseStore
                .document(UserManager.userId ?: "")
                .collection(PATH_NOTE)
                .document(note.id)

            if (uri != null) {
                val ref = storageReference.child(UPLOADS + UUID.randomUUID().toString())
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

                            noteDocument.set(note)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Logger.i("Update note success : $note")
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
                    }.addOnFailureListener {
                        Logger.i("task is Failure")
                    }
            } else {
                noteDocument.set(note)
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


    override suspend fun publishNote(note: Note, boxId: String, uri: Uri?): Result<Boolean> =
        suspendCoroutine { continuation ->

            val storageReference = FirebaseStorage.getInstance().reference

            val noteDocument = firebaseStore
                .document(UserManager.userId ?: "")
                .collection(PATH_NOTE)
                .document()

            note.id = noteDocument.id

            if (uri != null) {
                val ref = storageReference.child(UPLOADS + UUID.randomUUID().toString())
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

                            noteDocument.set(note)
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
                    }.addOnFailureListener {
                        Logger.i("task is Failure")
                    }
            } else {
                noteDocument.set(note)
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
}