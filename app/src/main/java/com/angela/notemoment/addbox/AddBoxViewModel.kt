package com.angela.notemoment.addbox

import android.net.Uri
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
import com.angela.notemoment.ext.showToast

class AddBoxViewModel (private val repository: NoteRepository,
                       private val argument: Box?) : ViewModel() {

    private val _box = MutableLiveData<Box>()
        .apply {
            value = argument ?: Box()
    }

    val box: LiveData<Box>
        get() = _box

    var photoUrl = MutableLiveData<Uri>()


    private val _navigateToList = MutableLiveData<Boolean>()

    val navigateToList: LiveData<Boolean>
        get() = _navigateToList


    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

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


    fun save(box: Box, photoUrl: Uri? = null) {

        Logger.i("save, canAddBox=$canAddBox")

        if (canAddBox) {

            if (box.id.isEmpty()) {
                publishBox(box, photoUrl)
            } else {
                updateBox(box, photoUrl)
            }
        } else {
            showToast(NoteApplication.instance.getString(R.string.hint_no_box_name))
        }
    }

    private fun publishBox(box: Box, photoUrl: Uri? = null) {
        Logger.i("publishBox, box=$box")

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.publishBox(box, photoUrl)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    showToast(NoteApplication.instance.getString(R.string.add_success))
                    navigateToList()
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    showToast(NoteApplication.instance.getString(R.string.fail))
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

    private fun updateBox(box: Box, photoUrl: Uri? = null) {
        Logger.i("updateBox, box=$box")

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateBox(box, photoUrl)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    showToast(NoteApplication.instance.getString(R.string.edit_success))
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


    private val canAddBox
        get() = !box.value?.title.isNullOrEmpty()

    private fun navigateToList() {
        _navigateToList.value = true
    }

    fun onListNavigated() {
        _navigateToList.value = null
    }

}