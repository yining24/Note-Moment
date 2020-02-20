package com.angela.notemoment

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.util.CurrentFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel (private val repository: NoteRepository) : ViewModel() {

    private val _boxes = MutableLiveData<List<Box>>()

    val boxes: LiveData<List<Box>>
        get() = _boxes

    private val _navigateToAddBox = MutableLiveData<Boolean>()

    val navigateToAddBox: LiveData<Boolean>
        get() = _navigateToAddBox

    private val _navigateToAddNote = MutableLiveData<Boolean>()

    val navigateToAddNote: LiveData<Boolean>
        get() = _navigateToAddNote

    val currentFragmentType = MutableLiveData<CurrentFragmentType>()



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

        getBoxesResult()
    }


    private fun getBoxesResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getBox()

            _boxes.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = NoteApplication.instance.getString(R.string.fail)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }



    fun navigateToAddBox() {
        _navigateToAddBox.value = true
    }

    fun onAddBoxNavigated() {
        _navigateToAddBox.value = null
    }

    fun navigateToAddNote() {
        if (_boxes.value?.size == 0) {
            Toast.makeText(NoteApplication.instance, "請先新增抽屜～", Toast.LENGTH_SHORT).show()
        } else {
            _navigateToAddNote.value = true
        }
    }

    fun onAddNoteNavigated() {
        _navigateToAddNote.value = null
    }

}