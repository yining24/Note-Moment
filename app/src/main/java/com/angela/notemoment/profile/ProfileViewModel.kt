package com.angela.notemoment.profile

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ProfileViewModel (private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()

    val notes: LiveData<List<Note>>
        get() = _notes

    private val _markerNotes = MutableLiveData<ArrayList<Note>>()

    val markerNotes: LiveData<ArrayList<Note>>
        get() = _markerNotes

    var markerTitle = MutableLiveData<String>()

//    var markerLatlng = MutableLiveData<LatLng>()

    private val _showNotesWindow = MutableLiveData<Boolean>()

    val showNotesWindow: LiveData<Boolean>
        get() = _showNotesWindow


    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
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

        getAllNoteResult()
    }


    private fun getAllNoteResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getAllNote()

            _notes.value = when (result) {
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
    fun getMarkerNote(latLng: LatLng) {
        _markerNotes.value = arrayListOf()
        notes.value.let { listNote ->
            listNote!!.forEach {
                val noteLatLng = LatLng(it.lat, it.lng)
                if (noteLatLng == latLng) {
                    _markerNotes.value?.add(it)
                }
                Logger.i("marker listNote = $listNote")
            }
        }
    }

    fun showNotesWindow() {
        _showNotesWindow.value = true
    }

    fun closeNotesWindow() {
        _showNotesWindow.value = null
    }


}