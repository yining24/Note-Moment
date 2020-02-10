package com.angela.notemoment.listnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.angela.notemoment.R
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.FragmentListNoteBinding
import com.angela.notemoment.ext.getVmFactory

class ListNoteFragment : Fragment() {
    private val viewModel by viewModels<ListNoteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentListNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_note, container, false)
        binding.viewModel = viewModel

        val adapter = ListNoteAdapter(viewModel)
        binding.recyclerListNoteDetail.adapter = adapter

        binding.lifecycleOwner = this




        val listNote = listOf(
            ListNoteSorted(
                12345,
                listOf(Note(
                    "",
                    22222,
                    "",
                    "",
                    "",
                    listOf(),
                    ""
                )
                )
            ), ListNoteSorted(
                12112,
                listOf(Note(
            "",
            11111,
            "",
            "",
            "",
            listOf(),
            ""
            )
        )
            )
        )

        val note = listOf(Note(
            "",
            11111,
            "",
            "",
            "",
            listOf(),
            ""
        ),
            Note(
                "",
                22222,
                "",
                "",
                "",
                listOf(),
                ""
            ))


        adapter.submitList(listNote)


        return binding.root
    }

}