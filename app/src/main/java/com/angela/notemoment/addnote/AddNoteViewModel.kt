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
import com.angela.notemoment.ext.checkAndUpdateDate
import com.angela.notemoment.ext.showToast
import com.angela.notemoment.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val argument: Box?
) : ViewModel() {

    private val _box = MutableLiveData<Box>().apply {
        value = argument
    }
    val box: LiveData<Box>
        get() = _box


    private val _note = MutableLiveData<Note>()
        .apply {
            value = Note()
        }
    val note: LiveData<Note>
        get() = _note


    private val _boxes = MutableLiveData<List<Box>>()

    private val boxes: LiveData<List<Box>>
        get() = _boxes


    var keyBoxPosition = MutableLiveData<Int>().apply {
        value = -1
    }

    var content = MutableLiveData<String>()

    var photoUrl = MutableLiveData<Uri>()

    var selectedBox = Box()

    private val _navigateToListNote = MutableLiveData<Boolean>()

    val navigateToListNote: LiveData<Boolean>
        get() = _navigateToListNote


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

        Logger.i("argument box:: ${box.value}")

        getBoxesResult()
    }

    fun publishNoteResult(note: Note, photoUrl: Uri? = null, selectedBox: Box) {

        note.content = content.value ?: ""

        if (canAddNote) {
            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                Toast.makeText(NoteApplication.instance, NoteApplication.instance.getString(R.string.uploading), Toast.LENGTH_SHORT).show()

                when (val result = repository.publishNote(note, note.boxId, photoUrl)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        selectedBox.checkAndUpdateDate(repository)
                        showToast(NoteApplication.instance.getString(R.string.add_success))
                        navigateToListNote()
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
        } else {
            showToast(NoteApplication.instance.getString(R.string.hint_note_required_fields))
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
                    findKeyBoxPosition(result.data)
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


    val boxList = Transformations.map(boxes) {
        val boxesTitle = mutableListOf<String>()
        for (box in it) {
            boxesTitle.add(box.title)
        }
        boxesTitle
    }


    private fun findKeyBoxPosition(boxes: List<Box>) {
        val argBoxId = box.value?.id

        for ((index, box) in boxes.withIndex()) {
            if (box.id == argBoxId) {
                keyBoxPosition.value = index
                break
            }
        }
        Logger.i("box position === ${keyBoxPosition.value}")
    }

    fun selectBoxPosition(position: Int) {
        boxes.value?.let {
            selectedBox = it[position]
            note.value?.boxId = selectedBox.id
            Logger.i("selectBox value = ${boxes.value}")
        }
        return
    }


    fun selectedPlace(name: String, lat: Double, lng: Double) {
        note.value?.locateName = name
        note.value?.lat = lat
        note.value?.lng = lng
    }

    private val canAddNote
        get() = !note.value?.title.isNullOrEmpty() && note.value?.time != 1L && !note.value?.locateName.isNullOrEmpty()


    private fun navigateToListNote() {
        _navigateToListNote.value = true
    }

    fun onListNoteNavigated() {
        _navigateToListNote.value = null
    }

    fun onChangeNoteTime(time: Long) {
        _note.value?.time = time
        Logger.i("chang note time = $time")
    }

}
