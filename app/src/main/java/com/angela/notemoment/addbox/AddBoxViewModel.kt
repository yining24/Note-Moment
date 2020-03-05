package com.angela.notemoment.addbox

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.*
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.angela.notemoment.data.Result
import java.util.*

class AddBoxViewModel (private val repository: NoteRepository,
                       private val argument: Box?) : ViewModel() {

    private val _box = MutableLiveData<Box>()
        .apply {
        value = argument
    }

    val box: LiveData<Box>
        get() = _box

    var photoUrl = MutableLiveData<Uri>()


    private val _navigateToList = MutableLiveData<Boolean>()

    val navigateToList: LiveData<Boolean>
        get() = _navigateToList



    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error


    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")


    }

    fun publishBoxResult(box: Box, photoUrl: Uri? = null) {

        Logger.i("publishBoxResult, canAddbox=$canAddbox")
        if (canAddbox) {
            Logger.i("publishBoxResult, box=$box")
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING
                Toast.makeText(NoteApplication.instance, "Uploading", Toast.LENGTH_SHORT).show()

                if (box.id.isEmpty()) {
                    when (val result = repository.publishBox(box, photoUrl)) {
                        is Result.Success -> {
                            _error.value = null
                            _status.value = LoadApiStatus.DONE
                            Toast.makeText(NoteApplication.instance, "Add Success", Toast.LENGTH_SHORT)
                                .show()
                            navigateToList()
                        }
                        is Result.Fail -> {
                            _error.value = result.error
                            _status.value = LoadApiStatus.ERROR
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
                } else {
                    when (val result = repository.updateBox(box, photoUrl)) {
                        is Result.Success -> {
                            _error.value = null
                            _status.value = LoadApiStatus.DONE
                            Toast.makeText(NoteApplication.instance, "Edit Success", Toast.LENGTH_SHORT)
                                .show()
                            navigateToList()
                        }
                        is Result.Fail -> {
                            _error.value = result.error
                            _status.value = LoadApiStatus.ERROR
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
        } else {
            Toast.makeText(NoteApplication.instance, "-- No title --", Toast.LENGTH_SHORT).show()
        }

    }


    val canAddbox
        get() = !box.value?.title.isNullOrEmpty()


    fun navigateToList() {
        _navigateToList.value = true
    }

    fun onListNavigated() {
        _navigateToList.value = null
    }

}