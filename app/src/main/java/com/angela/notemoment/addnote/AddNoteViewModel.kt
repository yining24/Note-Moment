package com.angela.notemoment.addnote

import android.net.Uri
import android.widget.Toast
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

    var content = MutableLiveData<String>()


    private val _boxes = MutableLiveData<List<Box>>()

    val boxes: LiveData<List<Box>>
        get() = _boxes


    private val _navigateToList = MutableLiveData<Boolean>()

    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    var photoUrl = MutableLiveData<Uri>()

    var selectedBox = Box()

    var isUpdateBoxDate = false


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

    fun publishNoteResult(note: Note, photoUrl: Uri? = null, selectedBox: Box) {

        note.content = content.value?: ""

        if (canAddNote) {
            updateBoxDate(selectedBox)
            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                Toast.makeText(NoteApplication.instance, "Uploading", Toast.LENGTH_SHORT).show()

                when (val result = repository.publishNote(note, note.boxId, photoUrl)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        Toast.makeText(NoteApplication.instance, "Success", Toast.LENGTH_SHORT).show()
                        navigateToList()

                        if (isUpdateBoxDate) {
                            repository.updateBox(selectedBox ,null)
                        }
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

        } else {
            Toast.makeText(NoteApplication.instance, "-- Fields marked with * are required --", Toast.LENGTH_SHORT).show()
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
//        if (_boxes.value?.size == 0) {
//            val defaultBox = Box("", "default",0L,0L,"臺灣","")
//            _boxes.value = listOf(defaultBox)
//            Logger.i("default = $defaultBox")
//        }
        for (box in it){
            boxTitleList.add(box.title)
        }
        boxTitleList
    }


    fun selectBoxPosition(position : Int){
        boxes.value?.let {
            selectedBox = it[position]
            note.value?.boxId = selectedBox.id
            Logger.i("selectBox value = ${boxes.value}")

        }
        return
    }


    fun updateBoxDate (box: Box) {

        note.value?.apply {
            when {
                box.startDate == 0L && box.endDate == 0L -> {
                    isUpdateBoxDate = true
                    box.startDate = this.time
                    box.endDate = this.time
                    Logger.i("box start and end time to ::${this.time}")
                }
                this.time < box.startDate -> {
                    isUpdateBoxDate = true
                    box.startDate = this.time
                    Logger.i("box start time to ::${this.time}")
                }
                this.time > box.endDate -> {
                    isUpdateBoxDate = true
                    box.endDate = this.time
                    Logger.i("box end time to ::${this.time}")
                }
            }
        }
    }


    fun selectedPlace(name : String, lat: Double, lng: Double) {
        note.value?.locateName = name
        note.value?.lat = lat
        note.value?.lng = lng
    }



    val canAddNote
        get() = !note.value?.title.isNullOrEmpty() && note.value?.time != 1L && !note.value?.locateName.isNullOrEmpty()

    fun navigateToList() {
        _navigateToList.value = true
    }

    fun onListNavigated() {
        _navigateToList.value = null
    }

    fun onChangeNoteTime(time: Long) {
        _note.value?.time = time
        Logger.i("chang note time = $time")
    }

}