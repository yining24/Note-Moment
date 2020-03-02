package com.angela.notemoment.addnote

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.Logger
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_add_note.*
import java.io.IOException
import java.util.*



class AddNoteFragment  : Fragment() , PlaceSelectionListener {

    private var filePath: Uri? = null

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


        binding.noteButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }



//        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
//        mainViewModel.showToolbarSave()
//
//        val save = (activity as MainActivity).binding.toolbar.findViewById<View>(R.id.toolbar_save)
//        save.setOnClickListener {
//            if (boxViewModel.note.value != null) {
//                boxViewModel.publishNoteResult(boxViewModel.note.value!!, boxViewModel.photoUrl.value, boxViewModel.selectedBox)
//            }
//        }


        //date dialog picker
        val cal = Calendar.getInstance()
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0)
        Logger.i("set date is :${cal.time}")

        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.selectDate.text = sdf.format(cal.time)

        binding.selectTime.text = SimpleDateFormat("HH:mm").format(cal.time)

        viewModel.onChangeNoteTime(cal.timeInMillis)


        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            viewModel.onChangeNoteTime(cal.timeInMillis)

            binding.selectDate.text = sdf.format(cal.time)
        }

        binding.selectDate.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

//        binding.selectTime.setOnClickListener {
//            val hour = cal.get(Calendar.HOUR_OF_DAY)
//            val minute = cal.get(Calendar.MINUTE)
//            TimePickerDialog(context!!, 3,{
//                    _, hour, minute->
//                binding.selectTime.text = String.format("%02d:%02d", hour, minute)
//            }, hour, minute, true).show()
//        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            viewModel.onChangeNoteTime(cal.timeInMillis)
            binding.selectTime.text = String.format("%02d:%02d", hourOfDay, minute)
        }

        binding.selectTime.setOnClickListener {
            TimePickerDialog(requireContext(), 3, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)
            , true).show()
        }






        // spinner listener
        val spinner = binding.selectBox

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                viewModel.selectBoxPosition(pos)
                Logger.i("pos = $pos")
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }



        viewModel.navigateToList.observe(viewLifecycleOwner, Observer {
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

        autocompleteFragment.setHint("* Location")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS, Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(this)
        (autocompleteFragment.view?.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).textSize = 14.0f
        (autocompleteFragment.view?.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText)
            .setHintTextColor(resources.getColor(R.color.hint_text_color))




        //upload photo
        binding.uploadImage.setOnClickListener { launchGallery() }


        return binding.root

    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
//        mainViewModel.hideToolbarSave()
//    }

    override fun onError(status: Status) {
        Logger.i("An error occurred:  $status")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaceSelected(p0: Place) {
        viewModel.selectedPlace(p0.name?:"" , p0.latLng?.latitude?: 0.0,  p0.latLng?.longitude?: 0.0)
        Logger.i("selected place :: ${p0.latLng}")
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            viewModel.photoUrl.value = data.data

            try {
                filePath = data.data
                Glide.with(this).load(filePath).centerCrop()
                    .into(upload_image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}



