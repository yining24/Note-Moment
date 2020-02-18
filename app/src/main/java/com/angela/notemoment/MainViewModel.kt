package com.angela.notemoment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.util.CurrentFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel (private val noteRepository: NoteRepository) : ViewModel() {



    private val _navigateToAddBox = MutableLiveData<Boolean>()

    val navigateToAddBox: LiveData<Boolean>
        get() = _navigateToAddBox

    private val _navigateToAddNote = MutableLiveData<Boolean>()

    val navigateToAddNote: LiveData<Boolean>
        get() = _navigateToAddNote

    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    private val _showToolbarSave = MutableLiveData<Boolean>().apply {
        value = false
    }

    val showToolbarSave: LiveData<Boolean>
        get() = _showToolbarSave

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


    fun navigateToAddBox() {
        _navigateToAddBox.value = true
    }

    fun onAddBoxNavigated() {
        _navigateToAddBox.value = null
    }

    fun navigateToAddNote() {
        _navigateToAddNote.value = true
    }

    fun onAddNoteNavigated() {
        _navigateToAddNote.value = null
    }

    fun showToolbarSave() {
        _showToolbarSave.value = true
    }

    fun hideToolbarSave() {
        _showToolbarSave.value = false
    }
}