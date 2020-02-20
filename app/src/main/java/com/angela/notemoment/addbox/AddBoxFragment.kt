package com.angela.notemoment.addbox

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_box.*
import java.io.IOException


class AddBoxFragment : Fragment() {

    lateinit var ccp: CountryCodePicker
    private var filePath: Uri? = null

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

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }


        //date picker
//        val cal = Calendar.getInstance()
//
//        val dateSetListener =
//            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                viewModel.setStartDate(cal.time)
//            }
//
//        binding.selectStartDate.setOnClickListener {
//            DatePickerDialog(
//                context!!, dateSetListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }
//
//
//        val endDateSetListener =
//            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                viewModel.setEndDate(cal.time)
//            }
//
//        binding.selectEndDate.setOnClickListener {
//            DatePickerDialog(
//                context!!, endDateSetListener,
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }


        viewModel.box.observe(this, Observer {
            Logger.i("viewModel.box.observe, it=$it")
        })

        viewModel.navigateToList.observe(this, Observer {
            it?.let {
                findNavController().navigate(AddBoxFragmentDirections.actionGlobalListFragment())
                viewModel.onListNavigated()
            }
        })

        viewModel.photoUrl.observe(this, Observer {
            Logger.d("photo uri :: $it")
        })




        //upload photo

        binding.uploadImage.setOnClickListener { launchGallery() }


        return binding.root
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

//    private fun uploadImage(){
//        if(filePath != null){
//            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
//            val uploadTask = ref?.putFile(filePath!!)
//            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        Logger.i("task is not successful")
//                        throw it
//                    }
//                }
//                return@Continuation ref.downloadUrl
//            })?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Logger.i("task is successful")
//
//                    //toolbar_save to firestore
//                    Logger.i(" viewModel.photoUrl.value = ${task.result}")
//                    viewModel.addUploadRecordToDb(task.result.toString())
//
//                } else {
//// Handle failures
//                }
//            }?.addOnFailureListener{
//            }
//        }else{
//            Logger.i("Please Upload an Image")
//            Toast.makeText(context, "Please Upload an Image", Toast.LENGTH_SHORT).show()
//        }
//    }


//    private fun addUploadRecordToDb(uri: String){
//        val db = FirebaseFirestore.getInstance()
//        val data = HashMap<String, Any>()
//        data["imageUrl"] = uri
//        db.collection("posts")
//            .add(data)
//            .addOnSuccessListener { documentReference ->
//                Toast.makeText(context, "Saved to DB", Toast.LENGTH_LONG).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context, "Error saving to DB", Toast.LENGTH_LONG).show()
//            }
//    }


}


