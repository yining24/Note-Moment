package com.angela.notemoment.listnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.util.Logger
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentListNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListNoteFragment : Fragment() {
    private val viewModel by viewModels<ListNoteViewModel> { getVmFactory (ListNoteFragmentArgs.fromBundle(requireArguments()).BoxKey) }

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

        viewModel.navigateToDetailNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.box.value?.let {box ->
                    findNavController().navigate(NavigationDirections.actionGlobalDetailNoteFragment(it, box))
                    viewModel.onSelectNote()
                }
            }
        })

        viewModel.navigateToAddNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddNoteFragment(it))
                viewModel.onAddNoteNavigated()
            }
        })

        viewModel.navigateToAddBox.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddboxFragment(it))
                viewModel.onEditBoxNavigated()
            }
        })



        viewModel.note.observe(viewLifecycleOwner, Observer {
            viewModel.noteSorted.value= viewModel.toListNoteSorted(it)
        })



        //fab setting
        fabNote = binding.fabNote



        return binding.root
    }


}