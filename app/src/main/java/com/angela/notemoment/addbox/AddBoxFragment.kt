package com.angela.notemoment.addbox

import android.app.DatePickerDialog
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
import androidx.fragment.app.viewModels
import com.angela.notemoment.ext.getVmFactory
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger


class AddBoxFragment : Fragment() {

    lateinit var ccp: CountryCodePicker

    private val viewModel by viewModels<AddBoxViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAddBoxBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_box, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        ccp = binding.ccp

        viewModel.box.value?.country = ccp.selectedCountryName

        ccp.setOnCountryChangeListener {
            Logger.i(ccp.selectedCountryCode)
            Logger.i(ccp.selectedCountryName)
            viewModel.box.value?.country = ccp.selectedCountryName
        }


        //date picker
        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                viewModel.setStartDate(cal.time)
            }

        binding.selectStartDate.setOnClickListener {
            DatePickerDialog(
                context!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        val endDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                viewModel.setEndDate(cal.time)
            }

        binding.selectEndDate.setOnClickListener {
            DatePickerDialog(
                context!!, endDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        viewModel.box.observe(this, Observer {
            Logger.i("viewModel.box.observe, it=$it")
        })

        viewModel.navigateToList.observe(this, Observer {
            it?.let {
                findNavController().navigate(AddBoxFragmentDirections.actionGlobalListFragment())
                viewModel.onListNavigated()
            }
        })

        return binding.root
    }


}


