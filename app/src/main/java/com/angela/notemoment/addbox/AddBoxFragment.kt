package com.angela.notemoment.addbox

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.util.Logger
import com.angela.notemoment.util.MyRequestCode
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_box.*
import java.io.IOException


class AddBoxFragment : Fragment() {

    private lateinit var ccp: CountryCodePicker
    private var filePath: Uri? = null

    private val viewModel by viewModels<AddBoxViewModel> {
        getVmFactory(
            AddBoxFragmentArgs.fromBundle(requireArguments()).BoxKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAddBoxBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_box, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //country code
        ccp = binding.ccp

        viewModel.box.value?.country = ccp.selectedCountryName

        ccp.setOnCountryChangeListener {
            Logger.i(ccp.selectedCountryCode)
            Logger.i(ccp.selectedCountryName)
            viewModel.box.value?.country = ccp.selectedCountryName
        }

        //upload photo
        binding.addBoxUploadImage.setOnClickListener { launchGallery() }

        viewModel.photoUrl.observe(viewLifecycleOwner, Observer {
            Logger.d("photo uri :: $it")
        })


        binding.addBoxButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.box.observe(viewLifecycleOwner, Observer {
            Logger.i("boxViewModel.box.observe, it=$it")
        })

        viewModel.navigateToList.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(AddBoxFragmentDirections.actionGlobalListFragment())
                viewModel.onListNavigated()
            }
        })


        return binding.root
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
                    .into(add_box_upload_image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


