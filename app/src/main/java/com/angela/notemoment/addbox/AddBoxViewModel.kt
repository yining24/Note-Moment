package com.angela.notemoment.addbox

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.*
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.angela.notemoment.data.Result
import java.util.*

class AddBoxViewModel (private val repository: NoteRepository) : ViewModel() {

    private val _box = MutableLiveData<Box>()
        .apply {
        value = Box()
    }

    val box: LiveData<Box>
        get() = _box

    var boxStartDate = MutableLiveData<String>()


    var boxEndDate = MutableLiveData<String>()
//        .apply {
//            value = "請點選結束日期"
//        }

    var photoUrl = MutableLiveData<Uri>()


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


        publishBoxResult(Box())
    }

    fun setStartDate(date: Date) {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        sdf.format(date)
        _box.value?.startDate = date.time
        _box.value = _box.value
        boxStartDate.value = sdf.format(date)
    }

    fun setEndDate(date: Date) {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat)
        _box.value?.endDate = date.time
        _box.value = _box.value
        boxEndDate.value = sdf.format(date)
    }




    fun publishBoxResult(box: Box, photoUrl: Uri? = null) {

        Logger.i("publishBoxResult, canAddbox=$canAddbox")
        if (canAddbox) {
            Logger.i("publishBoxResult, box=$box")
            coroutineScope.launch {

                _status.value = LoadApiStatus.LOADING

                when (val result = repository.publishBox(box, photoUrl)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        Toast.makeText(NoteApplication.instance, "Success", Toast.LENGTH_SHORT).show()
                        navigateToList()
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
        }

    }


    val canAddbox
        get() = !box.value?.title.isNullOrEmpty()


    fun navigateToList() {
        _navigateToList.value = true
    }

    fun onListNavigated() {
        _navigateToList.value = null
    }

}