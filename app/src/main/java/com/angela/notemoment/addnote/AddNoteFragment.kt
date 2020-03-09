package com.angela.notemoment.addnote

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.util.MyRequestCode
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class AddNoteFragment : Fragment(), PlaceSelectionListener {

    companion object {
        private const val TIME_PICKER_THEME = 3
        private const val HINT_TEXT_SIZE = 14.0f

    }

    private var filePath: Uri? = null

    private val viewModel by viewModels<AddNoteViewModel> {
        getVmFactory(
            AddNoteFragmentArgs.fromBundle(
                requireArguments()
            ).BoxKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_note, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        binding.addNoteButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }


        //date picker
        val cal = Calendar.getInstance()
        cal.set(
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0
        )
        Logger.i("set date is :${cal.time}")

        val myFormat = getString(R.string.format_date) // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.selectDate.text = sdf.format(cal.time)

        binding.selectTime.text = SimpleDateFormat(getString(R.string.format_time)).format(cal.time)

        viewModel.onChangeNoteTime(cal.timeInMillis)


        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.onChangeNoteTime(cal.timeInMillis)

                binding.selectDate.text = sdf.format(cal.time)
            }

        binding.selectDate.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            viewModel.onChangeNoteTime(cal.timeInMillis)
            binding.selectTime.text = String.format("%02d:%02d", hourOfDay, minute)
        }

        binding.selectTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                TIME_PICKER_THEME,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
                ,
                true
            ).show()
        }

        viewModel.navigateToListNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(AddNoteFragmentDirections.actionGlobalListFragment())
                viewModel.onListNoteNavigated()
            }
        })


        // spinner listener
        val spinner = binding.selectBox

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                viewModel.selectBoxPosition(pos)
                Logger.i("pos = $pos")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewModel.boxList.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.selectBox.adapter = SelectBoxSpinnerAdapter(it)
            }
        })

        viewModel.keyBoxPosition.observe(viewLifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Main).launch {
                spinner.setSelection(it, true)
                Logger.d("observe setpos :: $it")
            }
        })


        //place AutocompleteSupportFragment
        val apiKey = getString(R.string.google_map_api)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment
            .setHint(getString(R.string.hint_autocomplete_location))
            .setPlaceFields(
                listOf(
                    Place.Field.ADDRESS,
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
                )
            )
            .setOnPlaceSelectedListener(this)

        val autocompleteText =
            (autocompleteFragment.view?.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText)
        autocompleteText.textSize = HINT_TEXT_SIZE
        autocompleteText.typeface = ResourcesCompat.getFont(requireContext(), R.font.noto_sans)
        autocompleteText.setTextColor(NoteApplication.instance.getColor(R.color.black_3f3a3a))
        autocompleteText.setHintTextColor(NoteApplication.instance.getColor(R.color.hint_text_color))



        val autocompleteIcon =
            autocompleteFragment.view?.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button) as ImageView
        autocompleteIcon.visibility = View.GONE


        //upload photo
        binding.addNoteUploadImage.setOnClickListener { launchGallery() }

        return binding.root

    }

    override fun onError(status: Status) {
        Logger.i("autocompleteFragment close:  $status")
    }

    override fun onPlaceSelected(p0: Place) {
        viewModel.selectedPlace(
            p0.name ?: "",
            p0.latLng?.latitude ?: 0.0,
            p0.latLng?.longitude ?: 0.0
        )
        Logger.i("selected place :: ${p0.latLng}")
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = getString(R.string.launch_gallery_intent)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.launch_gallery_title)
            ), MyRequestCode.LAUNCH_GALLERY.value
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyRequestCode.LAUNCH_GALLERY.value && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            viewModel.photoUrl.value = data.data

            try {
                filePath = data.data
                Glide.with(this).load(filePath).centerCrop()
                    .into(add_note_upload_image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}



