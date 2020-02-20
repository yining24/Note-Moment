package com.angela.notemoment.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angela.notemoment.*
import com.angela.notemoment.ServiceLocator.repository
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.data.source.remote.NoteRemoteDataSource.publishBox
import com.angela.notemoment.databinding.FragmentListBinding
import com.angela.notemoment.ext.getVmFactory
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ListFragment : Fragment() {
    private val listViewModel by viewModels<ListViewModel> { getVmFactory() }
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.viewModel = listViewModel

        val adapter = ListBoxAdapter(listViewModel)
        binding.recyclerList.adapter = adapter

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerList.layoutManager = layoutManager

        binding.lifecycleOwner = this


        listViewModel.navigateToListNote.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalListNoteFragment(it))
                listViewModel.notSelectBox()
            }
        })

        listViewModel.navigateToAddBox.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddboxFragment())
                listViewModel.onAddBoxNavigated()
            }
        })


        binding.viewClickToAdd



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

}