package com.angela.notemoment.listnote

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
import com.angela.notemoment.Logger
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.FragmentListNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListNoteFragment : Fragment() {
    private val viewModel by viewModels<ListNoteViewModel> { getVmFactory (ListNoteFragmentArgs.fromBundle(arguments!!).BoxKey) }

    private var isFabOpen = false
    private lateinit var fab: FloatingActionButton
    private lateinit var fabBox: FloatingActionButton
    private lateinit var fabNote: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentListNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_note, container, false)
        binding.viewModel = viewModel

        val adapter = ListNoteSortedAdapter(viewModel)
        binding.recyclerListNoteDetail.adapter = adapter

        binding.lifecycleOwner = this



        viewModel.note.observe(this, Observer {
            it?.let {
                Logger.i("note size = ${it.size}")
            }
        })

        viewModel.navigateToAddNote.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddNoteFragment())
                viewModel.onAddNoteNavigated()
            }
        })

        viewModel.navigateToAddBox.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddboxFragment())
                viewModel.onAddBoxNavigated()
                closeFABMenu()
            }
        })



        binding.listButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }


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