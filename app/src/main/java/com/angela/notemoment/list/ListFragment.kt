package com.angela.notemoment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.databinding.FragmentListBinding
import com.angela.notemoment.ext.getVmFactory

class ListFragment : Fragment() {
    private val listViewModel by viewModels<ListViewModel> { getVmFactory() }


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

        val layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerList.layoutManager = layoutManager



        binding.lifecycleOwner = this

        val box = listOf( Box(
            "123",
            "哈哈哈",
            ""
        ), Box("124",
            "哈哈哈",
            ""
        ),Box("123",
            "哈哈哈",
            ""
        ),Box("125",
            "哈哈哈",
            ""
        ),Box("126",
            "哈哈哈",
            ""
        )

)
        adapter.submitList(box)


        return binding.root
    }
}