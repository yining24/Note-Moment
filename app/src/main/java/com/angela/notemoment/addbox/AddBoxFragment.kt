package com.angela.notemoment.addbox

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentAddBoxBinding
import com.hbb20.CountryCodePicker
import androidx.fragment.app.viewModels
import com.angela.notemoment.ext.getVmFactory
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.util.Logger
import com.angela.notemoment.util.MyRequestCode
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_box.*
import java.io.File
import java.io.IOException
import java.util.*


class AddBoxFragment : Fragment() {

    companion object {
        private const val CAMERA_AUTHORITY = "com.angela.notemoment.fileprovider"
        private const val IMAGE_FILE_TIME_STAMP = "yyyyMMdd_HHmmss"
    }
    private lateinit var ccp: CountryCodePicker
    private lateinit var cameraPhotoPath: String
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
        binding.addBoxUploadImage.setOnClickListener { showSelectPhotoDialog() }

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


    private fun getStoragePermissions() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        when (ContextCompat.checkSelfPermission(
            NoteApplication.instance,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )) {
            PackageManager.PERMISSION_GRANTED -> {
                launchGallery()
            }
            else -> {
                requestPermissions(
                    permissions,
                    MyRequestCode.LAUNCH_GALLERY.value
                )

            }
        }
    }

    private fun getCameraPermissions() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        when (ContextCompat.checkSelfPermission(
            NoteApplication.instance,
            Manifest.permission.CAMERA
        )) {
            PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            else -> {
                requestPermissions(
                    permissions,
                    MyRequestCode.LAUNCH_CAMERA.value
                )

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MyRequestCode.LAUNCH_GALLERY.value ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        launchGallery()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            MyRequestCode.LAUNCH_CAMERA.value ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        dispatchTakePictureIntent()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    return
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MyRequestCode.LAUNCH_GALLERY.value -> {
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
                MyRequestCode.LAUNCH_CAMERA.value -> {
                    viewModel.photoUrl.value = filePath

                    try {
                        Glide.with(this).load(cameraPhotoPath).centerCrop()
                            .into(add_box_upload_image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
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


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        CAMERA_AUTHORITY,
                        it
                    )
                    this.filePath = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, MyRequestCode.LAUNCH_CAMERA.value)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat(IMAGE_FILE_TIME_STAMP).format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            cameraPhotoPath = absolutePath
        }
    }

    private fun showSelectPhotoDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle(getString(R.string.upload_photo))
        val pictureDialogItems = arrayOf(getString(R.string.album), getString(R.string.camera))
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> getStoragePermissions()
                1 -> getCameraPermissions()
            }
        }
        pictureDialog.show()
    }

}


