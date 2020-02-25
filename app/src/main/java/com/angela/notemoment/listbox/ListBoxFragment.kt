package com.angela.notemoment.listbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angela.notemoment.*
import com.angela.notemoment.databinding.FragmentListBoxBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class ListBoxFragment : Fragment() {

    private val viewModel by viewModels<ListBoxViewModel> { getVmFactory() }
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var isFabOpen = false
    private lateinit var fab: FloatingActionButton
    private lateinit var fabBox: FloatingActionButton
    private lateinit var fabNote: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentListBoxBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_box, container, false)
        binding.viewModel = viewModel

        val adapter = ListBoxAdapter(viewModel)
        binding.recyclerList.adapter = adapter

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerList.layoutManager = layoutManager

        binding.lifecycleOwner = this


        viewModel.navigateToListNote.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalListNoteFragment(it))
                viewModel.notSelectBox()
            }
        })


        viewModel.navigateToAddBox.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddboxFragment())
                viewModel.onAddBoxNavigated()
                closeFABMenu()
            }
        })

        viewModel.navigateToAddNote.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddNoteFragment())
                viewModel.onAddNoteNavigated()
                closeFABMenu()
            }
        })


        //fab setting
        fab = binding.fab
        fabBox = binding.fabBox
        fabNote = binding.fabNote
        fab.bringToFront()
        fab.setOnClickListener {
            if (!isFabOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }


//        val testBox = Box(
//            "test 1",
//            "test 1",
//            "",
//            "",
//            "")
//
//        repository.publishBox(testBox)
//
//        val testNote = Note(
//            "",
//            "",
//            "",
//            "",
//            "",
//            listOf("tags"),
//            listOf("images"),
//            "MhsmJyiTnh9KTwVtZbmj"
//        )
//
//        publishNote(testNote, testNote.boxId)


//        val box = listOf(
//            Box(
//                "123",
//                "哈哈哈",
//                "",
//                "",
//                ""
//            ), Box(
//                "124",
//                "哈哈哈",
//                "",
//                "",
//                ""
//            ), Box(
//                "123",
//                "哈哈哈",
//                "",
//                "",
//                ""
//            ), Box(
//                "125",
//                "哈哈哈",
//                "",
//                "",
//                ""
//            ), Box(
//                "126",
//                "哈哈哈",
//                "",
//                "",
//                ""
//            )
//
//        )
//        adapter.submitList(box)


        return binding.root
    }


    @SuppressLint("RestrictedApi")
    private fun showFABMenu() {
        isFabOpen = true
        fabBox.visibility = View.VISIBLE
        fabNote.visibility = View.VISIBLE
        fabBox.animate().translationY(-getResources().getDimension(R.dimen.standard_105))
        fabNote.animate().translationY(-getResources().getDimension(R.dimen.standard_55))
        fab.animate().setDuration(200).rotation(135f)
    }

    @SuppressLint("RestrictedApi")
    private fun closeFABMenu() {
        isFabOpen = false
        fabBox.animate().translationY(0F).withEndAction {
            fabBox.visibility = View.GONE
        }
        fabNote.animate().translationY(0F).withEndAction {
            fabNote.visibility = View.GONE
        }
        fab.animate().setDuration(200).rotation(0f)
    }

}