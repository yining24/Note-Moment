package com.angela.notemoment.listnote

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.util.Logger
import com.angela.notemoment.data.*
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*

class ListNoteViewModel (private val repository: NoteRepository,
                         private val argument: Box
) : ViewModel() {

    private val _box = MutableLiveData<Box>().apply {
        value = argument
    }
    val box: LiveData<Box>
        get() = _box

    val noteSorted = MutableLiveData<List<ListNoteSorted>>()


    private val _note = repository.getLiveNotes(argument.id)

    val note: LiveData<List<Note>>
        get() = _note


    private val _navigateToAddNote = MutableLiveData<Box>()

    val navigateToAddNote: LiveData<Box>
        get() = _navigateToAddNote


    private val _navigateToAddBox = MutableLiveData<Box>()

    val navigateToAddBox: LiveData<Box>
        get() = _navigateToAddBox


    private val _navigateToDetailNote = MutableLiveData<Note>()

    val navigateToDetailNote: LiveData<Note>
        get() = _navigateToDetailNote


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

//        _noteSorted.value = toListNoteSorted(note.value)
    }


//    private fun getLiveNotesResult() {
//
//        coroutineScope.launch {
//
//            _status.value = LoadApiStatus.LOADING
//
//            val result = repository.getNote(_box.value!!.id)
//
//            _note.value = when (result) {
//                is Result.Success -> {
//                    _error.value = null
//                    _status.value = LoadApiStatus.DONE
//                    _noteSorted.value = result.data.toListNoteSorted()
//                    result.data
//                }
//                is Result.Fail -> {
//                    _error.value = result.error
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                is Result.Error -> {
//                    _error.value = result.exception.toString()
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//                else -> {
//                    _error.value = NoteApplication.instance.getString(R.string.fail)
//                    _status.value = LoadApiStatus.ERROR
//                    null
//                }
//            }
//        }
//    }


    val notesSize = Transformations.map(_note){
        when (it.size) {
            0 -> "0 note"
            1 -> "1 note"
            else -> "${it.size} notes"
        }
    }


    fun displayBoxDate(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val start = sdf.format(_box.value?.startDate)
        val end = sdf.format(_box.value?.endDate)
        return if (_box.value?.startDate == 0L || _box.value?.endDate == 0L) {
            ""
        } else{
            "$start-$end"
        }
    }



    // sort notes by date
    fun toListNoteSorted(notes : List<Note>): List<ListNoteSorted> {

        val sortedNotes = mutableListOf<ListNoteSorted>()
        var tempObj = ListNoteSorted()

        notes.let {

            for (note in it) {

                fun getDateByTime(time: Long): String {
                    val myFormat = "yyyy/MM/dd"
                    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

                    return sdf.format(time)
                }

                val date = getDateByTime(note.time)
                Logger.i("date=$date")

                if (date != tempObj.date) {
                    Logger.i("create ListNoteSorted")
                    tempObj = ListNoteSorted()
                    tempObj.date = date
                    sortedNotes.add(tempObj)
                }
                tempObj.notes.add(note)
                Logger.i("sortedNotes=$sortedNotes")
            }
        }
        return sortedNotes
    }


    fun navigateToAddNote(box: Box) {
        _navigateToAddNote.value = box
    }

    fun onAddNoteNavigated() {
        _navigateToAddNote.value = null
    }

    fun selectNote(note: Note) {
        _navigateToDetailNote.value = note
    }

    fun onSelectNote() {
        _navigateToDetailNote.value = null
    }

    fun navigateToEditBox(box: Box) {
        _navigateToAddBox.value = box
    }

    fun onEditBoxNavigated() {
        _navigateToAddBox.value = null
    }



}