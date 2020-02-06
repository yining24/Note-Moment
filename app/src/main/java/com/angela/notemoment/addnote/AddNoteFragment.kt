package com.angela.notemoment.addnote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddNoteBinding
import java.util.*

class AddNoteFragment  : Fragment() {
//    private val addNoteViewModel by viewModels<addNoteViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_note, container, false)
        binding.lifecycleOwner = this
//        binding.viewModel = addNoteViewModel


        //date picker
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.selectDate.text = sdf.format(cal.time)

        }

        binding.selectDate.setOnClickListener {
            DatePickerDialog(context!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        binding.selectTime.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(context!!, 3,{
                    _, hour, minute->
                binding.selectTime.text = String.format("%02d:%02d", hour, minute)
            }, hour, minute, true).show()
        }

        val timePicker = binding.timePicker as TimePicker
        timePicker.setIs24HourView(true)


        // spinner box need to get firebase data
        val spinnerBox = binding.selectBox
        val box = arrayListOf("1/20 日本行", "1/22 一個人的旅行")
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, box)
        spinnerBox.adapter = adapter




        return binding.root
    }
}