package com.angela.notemoment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.angela.notemoment.Logger
import com.angela.notemoment.R
import com.angela.notemoment.databinding.FragmentProfileBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        val userId = FirebaseAuth.getInstance().currentUser?.uid?:""

        viewModel.getUser(userId)


        viewModel.user.observe(viewLifecycleOwner, Observer {
            it.let {
                Logger.w("observe::$it")
            }
        })


        return binding.root
    }



}




