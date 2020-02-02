package com.angela.notemoment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.angela.notemoment.databinding.FragmentHomeBinding
import com.angela.notemoment.databinding.FragmentProfileBinding

class HomeFragment : Fragment() {
//    private val listViewModel by viewModels<ListViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


//        val listAdapter = MyorderListAdapter(myorderViewModel)
//        val detailAdapter = MyorderDetailAdapter(myorderViewModel)


        binding.lifecycleOwner = this
//        binding.recyclerMyorderList.adapter = listAdapter


        return binding.root
    }
}