package com.angela.notemoment.addbox

import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddBoxBinding
import com.hbb20.CountryCodePicker
import java.util.*
import com.savvi.rangedatepicker.CalendarPickerView
import android.net.ParseException
import com.savvi.rangedatepicker.SubTitle


class AddBoxFragment : Fragment() {

    lateinit var ccp: CountryCodePicker

//    private val listViewModel by viewModels<ListViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAddBoxBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_box, container, false)


//        val listAdapter = MyorderListAdapter(myorderViewModel)
//        val detailAdapter = MyorderDetailAdapter(myorderViewModel)
//        binding.recyclerMyorderList.adapter = listAdapter

        binding.lifecycleOwner = this

        ccp = binding.ccp

//        Logger.i(ccp.selectedCountryCode)


        //date picker
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "MM.dd.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.selectStartDate.text = sdf.format(cal.time)

        }

        binding.selectStartDate.setOnClickListener {
            DatePickerDialog(context!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }



        return binding.root
    }




}


