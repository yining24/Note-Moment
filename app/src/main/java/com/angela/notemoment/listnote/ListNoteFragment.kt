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
import com.angela.notemoment.Logger
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.FragmentListNoteBinding
import com.angela.notemoment.ext.getVmFactory

class ListNoteFragment : Fragment() {
    private val viewModel by viewModels<ListNoteViewModel> { getVmFactory (ListNoteFragmentArgs.fromBundle(arguments!!).BoxKey) }

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

        binding.listButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }



        return binding.root
    }

}