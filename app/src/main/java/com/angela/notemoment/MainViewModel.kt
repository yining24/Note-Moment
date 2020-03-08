package com.angela.notemoment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.data.User
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.util.CurrentFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel (private val repository: NoteRepository) : ViewModel() {

    private var _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user


    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

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


    fun getMainUser(id:String) {
        _user =  repository.getUser(id) as MutableLiveData<User>
    }


}