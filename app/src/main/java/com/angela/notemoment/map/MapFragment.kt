package com.angela.notemoment.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentListBinding
import com.angela.notemoment.databinding.FragmentMapBinding

class MapFragment : Fragment() {
//    private val listViewModel by viewModels<ListViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMapBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)


//        val listAdapter = MyorderListAdapter(myorderViewModel)
//        val detailAdapter = MyorderDetailAdapter(myorderViewModel)


        binding.lifecycleOwner = this
//        binding.recyclerMyorderList.adapter = listAdapter


        return binding.root
    }
}