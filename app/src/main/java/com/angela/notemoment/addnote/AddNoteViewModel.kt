package com.angela.notemoment.addnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.angela.notemoment.*
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddNoteViewModel (private val repository: NoteRepository) : ViewModel() {

    private val _note = MutableLiveData<Note>()
        .apply {
            value = Note()
        }

    val note: LiveData<Note>
        get() = _note

    private val _boxes = MutableLiveData<List<Box>>()

    val boxes: LiveData<List<Box>>
        get() = _boxes


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

        getBoxesResult()
    }

    fun publishNoteResult(note: Note) {

        if (canAddNote) {
            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                when (val result = repository.publishNote(note, note.boxId)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
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
            navigateToList()

        }

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

    val boxList = Transformations.map(boxes){
        val boxTitleList = mutableListOf<String>()
        for (box in it){
            boxTitleList.add(box.title)
        }
        boxTitleList
    }

    fun selectedBox(postion : Int){
        note.value?.boxId = boxes.value!![postion].id
        Logger.i("box id value = ${boxes.value}")
    }


    val canAddNote
        get() = !note.value?.title.isNullOrEmpty() && note.value?.time != 1L && !note.value?.content.isNullOrEmpty()

    fun navigateToList() {
        _navigateToList.value = true
    }

    fun onListNavigated() {
        _navigateToList.value = null
    }

    fun onChangeNoteTime(time: Long) {
        note.value?.time = time
    }
}