package com.angela.notemoment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _navigateToListBox = MutableLiveData<Boolean>()

    val navigateToListBox: LiveData<Boolean>
        get() = _navigateToListBox


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


    fun checkUserResult(id: String) {

        coroutineScope.launch {
            Logger.w("checkUserResult user id :: $id")

            _status.value = LoadApiStatus.LOADING
            when (val result = repository.checkUser(id)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Logger.w("checkUserResult Success :: $id")
                    navigateToListBox()
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Logger.w("checkUserResult fail :: $id")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Logger.w("checkUserResult error :: $id")
                }
                else -> {
                    _error.value = NoteApplication.instance.getString(R.string.fail)
                    _status.value = LoadApiStatus.ERROR
                    Logger.w("checkUserResult else :: $id")
                }
            }
        }
    }

    private fun navigateToListBox() {
        _navigateToListBox.value = true
    }

    fun onListBoxNavigated() {
        _navigateToListBox.value = null
    }
}