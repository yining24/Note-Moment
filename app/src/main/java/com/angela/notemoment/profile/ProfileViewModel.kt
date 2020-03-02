package com.angela.notemoment.profile

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ProfileViewModel (private val repository: NoteRepository) : ViewModel() {

    private var _user = MutableLiveData<User>()

    val user : LiveData<User>
        get() = _user

    var userPhoto = MutableLiveData<Uri>()


    var isEditable = MutableLiveData<Boolean>().apply {
        value = false
    }


    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error



    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        userPhoto.value = FirebaseAuth.getInstance().currentUser?.photoUrl
    }


    fun getUser(id:String) {
        _user = repository.getUser(id) as MutableLiveData<User>

    }

    fun updateUserResult(user: User) {

            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING
                Toast.makeText(NoteApplication.instance, "Uploading", Toast.LENGTH_SHORT).show()

                when (val result = repository.updateUser(user)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        Toast.makeText(NoteApplication.instance, "Success", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                        Toast.makeText(NoteApplication.instance, "Fail", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = NoteApplication.instance.getString(R.string.fail)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }

        }




    fun editProfile() {
        if (isEditable.value == false){
            isEditable.value = true
        } else {
            isEditable.value = false
            updateUserResult(user.value!!)
        }

    }

}