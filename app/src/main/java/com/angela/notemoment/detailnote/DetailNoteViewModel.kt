package com.angela.notemoment.detailnote

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.ext.checkAndUpdateDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailNoteViewModel(
    private val repository: NoteRepository,
    private val arguments: Note,
    private val argumentsBox: Box?
) : ViewModel() {

    private val _note = MutableLiveData<Note>().apply {
        value = arguments
    }
    val note: LiveData<Note>
        get() = _note

    private val _box = MutableLiveData<Box>().apply {
        value = argumentsBox
    }
    val box: LiveData<Box>
        get() = _box


    private val _selectedBox = MutableLiveData<Box>().apply {
        value = argumentsBox
    }
    val selectedBox: LiveData<Box>
        get() = _selectedBox


    private val _allBoxes = MutableLiveData<List<Box>>()

    val allBoxes: LiveData<List<Box>>
        get() = _allBoxes


    var newPhotoUrl = MutableLiveData<Uri>().apply {
        value = null
    }


    var isEditable = MutableLiveData<Boolean>().apply {
        value = false
    }

    var keyBoxPosition = MutableLiveData<Int>().apply {
        value = -1
    }


    private val _navigateToListNote = MutableLiveData<Boolean>()

    val navigateToListNote: LiveData<Boolean>
        get() = _navigateToListNote


    var content = MutableLiveData<String>()

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

        getBoxesResult()

    }


    private fun updateNoteResult(note: Note, photoUrl: Uri?) {

        if (canUpdateNote) {
            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                Toast.makeText(
                    NoteApplication.instance,
                    NoteApplication.instance.getString(R.string.uploading),
                    Toast.LENGTH_SHORT
                ).show()

                when (val result = repository.updateNote(note, photoUrl)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        Toast.makeText(
                            NoteApplication.instance,
                            NoteApplication.instance.getString(R.string.edit_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        _box.value?.let { box ->
                            box.checkAndUpdateDate(repository)
                            _selectedBox.value?.let { selectedBox ->
                                if (selectedBox != box) {
                                    selectedBox.checkAndUpdateDate(repository)
                                }
                            }
                        }
                        navigateToListNote()
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                        Toast.makeText(
                            NoteApplication.instance,
                            NoteApplication.instance.getString(R.string.fail),
                            Toast.LENGTH_SHORT
                        ).show()
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
            Toast.makeText(
                NoteApplication.instance,
                NoteApplication.instance.getString(R.string.hint_note_required_fields),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun getBoxesResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getBox()

            _allBoxes.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    checkBoxValue(result.data)
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

    private fun checkBoxValue(boxes: List<Box>) {
        if (argumentsBox == null) {
            for (boxOfNote in boxes) {
                if (boxOfNote.id == note.value?.boxId) {
                    _box.value = boxOfNote
                    _selectedBox.value = boxOfNote
                    break
                }
            }
        }
        findKeyBoxPosition(boxes)
    }


    private val canUpdateNote
        get() = !note.value?.title.isNullOrEmpty() && note.value?.time != 1L && !note.value?.locateName.isNullOrEmpty()


    fun navigateToListNote() {
        _navigateToListNote.value = true
    }

    fun onListNoteNavigated() {
        _navigateToListNote.value = null
    }

    fun onChangeNoteTime(time: Long) {
        _note.value?.time = time
        Logger.i("chang note time = $time")
    }

    fun editNote() {
        if (isEditable.value == false) {
            isEditable.value = true
        } else {
            isEditable.value = false
            updateNoteResult(note.value!!, newPhotoUrl.value)
        }
    }


    val allBoxList = Transformations.map(allBoxes) {
        val boxTitleList = mutableListOf<String>()
        for (box in it) {
            boxTitleList.add(box.title)
        }
        boxTitleList
    }


    fun changeBoxPosition(position: Int) {
        allBoxes.value?.let {
            note.value?.boxId = it[position].id
            _selectedBox.value = it[position]
            Logger.i("selectBox value = ${allBoxes.value}")
        }
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


}