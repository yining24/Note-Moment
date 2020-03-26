package com.angela.notemoment.detailnote

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.addnote.SelectBoxSpinnerAdapter
import com.angela.notemoment.databinding.FragmentDetailNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.util.MyRequestCode
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_detail_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class DetailNoteFragment : Fragment() {
    private val viewModel by viewModels<DetailNoteViewModel> {
        getVmFactory (DetailNoteFragmentArgs.fromBundle(requireArguments()).NoteKey,
            DetailNoteFragmentArgs.fromBundle(requireArguments()).BoxKey) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentDetailNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_note, container, false)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.noteButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.navigateToListNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onListNoteNavigated()
            }
        })


        //set date picker
        val cal = Calendar.getInstance()

        val sdfDate = SimpleDateFormat(getString(R.string.format_date) , Locale.getDefault())
        val sdfTime = SimpleDateFormat(getString(R.string.format_time) , Locale.getDefault())

        binding.selectDate.text = sdfDate.format(viewModel.note.value?.time)
        binding.selectTime.text = sdfTime.format(viewModel.note.value?.time)


        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            viewModel.onChangeNoteTime(cal.timeInMillis)

            binding.selectDate.text = sdfDate.format(cal.time)
        }

        binding.selectDate.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            viewModel.onChangeNoteTime(cal.timeInMillis)
            binding.selectTime.text = String.format("%02d:%02d", hourOfDay, minute)
        }

        binding.selectTime.setOnClickListener {
            TimePickerDialog(requireContext(), 3, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                Calendar.MINUTE)
                , true).show()
        }



        // spinner listener
        val boxSpinner = binding.changeBox

        boxSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                viewModel.changeBoxPosition(pos)
                Logger.i("pos = $pos")
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewModel.allBoxList.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.changeBox.adapter = SelectBoxSpinnerAdapter(it)
            }
        })

        viewModel.keyBoxPosition.observe(viewLifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Main).launch {
                boxSpinner.setSelection(it, true)
                Logger.d("observe setpos :: $it")
            }
        })

        //upload photo
        binding.detailNoteImage.setOnClickListener { launchGallery() }


        return binding.root
    }


    private fun launchGallery() {
        val intent = Intent()
        intent.type = getString(R.string.launch_gallery_intent)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.launch_gallery_title)), MyRequestCode.LAUNCH_GALLERY.value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyRequestCode.LAUNCH_GALLERY.value && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            viewModel.newPhotoUrl.value = data.data

            try {
                var filePath = data.data
                Glide.with(this).load(filePath).centerCrop()
                    .into(detail_note_image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}