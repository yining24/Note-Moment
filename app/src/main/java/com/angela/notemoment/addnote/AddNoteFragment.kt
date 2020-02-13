package com.angela.notemoment.addnote

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.Logger
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class AddNoteFragment  : Fragment() , PlaceSelectionListener {

    private val viewModel by viewModels<AddNoteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_note, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        //date dialog picker
//        var cal = Calendar.getInstance()
//
//        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            cal.set(Calendar.YEAR, year)
//            cal.set(Calendar.MONTH, monthOfYear)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//            val myFormat = "MM/dd/yyyy" // mention the format you need
//            val sdf = SimpleDateFormat(myFormat, Locale.US)
//            binding.selectDate.text = sdf.format(cal.time)
//        }
//        binding.selectDate.setOnClickListener {
//            DatePickerDialog(context!!, dateSetListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)).show()
//        }
//        binding.selectTime.setOnClickListener {
//            val hour = cal.get(Calendar.HOUR_OF_DAY)
//            val minute = cal.get(Calendar.MINUTE)
//            TimePickerDialog(context!!, 3,{
//                    _, hour, minute->
//                binding.selectTime.text = String.format("%02d:%02d", hour, minute)
//            }, hour, minute, true).show()
//        }

        //get selected time
        val datePicker = binding.datePicker as DatePicker
        val timePicker = binding.timePicker as TimePicker
        timePicker.setIs24HourView(true)


        val calendar = Calendar.getInstance()

        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
            timePicker.hour, timePicker.minute, 0
        )
        viewModel.onChangeNoteTime(calendar.timeInMillis)


        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
                hourOfDay, minute, 0
            )
            Logger.i("change time is :$hourOfDay:$minute")
            Logger.i("calendar.timeInMillis=${calendar.timeInMillis}")

            viewModel.onChangeNoteTime(calendar.timeInMillis)
        }

        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth,
                timePicker.hour, timePicker.minute, 0
            )
            Logger.i("change date is :$year / $monthOfYear / $dayOfMonth")
            Logger.i("calendar.timeInMillis=${calendar.timeInMillis}")

            viewModel.onChangeNoteTime(calendar.timeInMillis)
        }


        // spinner listener
        val spinner = binding.selectBox

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val selectedBox = viewModel.selectedBox(pos)
                Logger.i("pos = $pos")

                //set limit with box date
                datePicker.maxDate = selectedBox.endDate
                datePicker.minDate = selectedBox.startDate


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        viewModel.navigateToList.observe(this, Observer {
            it?.let {
                findNavController().navigate(AddNoteFragmentDirections.actionGlobalListFragment())
                viewModel.onListNavigated()
            }
        })




        //place AutocompleteSupportFragment

        val apiKey = getString(R.string.google_map_api)

        if (!Places.isInitialized()) {
            Places.initialize(context!!, apiKey)
        }
        val placesClient = Places.createClient(context!!)

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment


        /*AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(filter);*/

        autocompleteFragment.setHint("請輸入景點")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS, Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(this)


        return binding.root

    }
    override fun onError(status: Status) {
        Logger.i("An error occurred:  $status")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaceSelected(p0: Place) {
        viewModel.selectedPlace(p0.name?:"" , p0.latLng?: LatLng(0.0,0.0))
        Logger.i("selected place :: ${p0.latLng}")
    }


}



