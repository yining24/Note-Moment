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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angela.notemoment.*
import com.angela.notemoment.databinding.FragmentListBoxBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.login.UserManager
import com.angela.notemoment.util.Logger
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ListBoxFragment : Fragment() {

    private val viewModel by viewModels<ListBoxViewModel> { getVmFactory() }

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


        viewModel.navigateToListNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalListNoteFragment(it))
                viewModel.notSelectBox()
            }
        })

        viewModel.navigateToAddBox.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddboxFragment())
                viewModel.onAddBoxNavigated()
                closeFABMenu()
            }
        })

        viewModel.navigateToAddNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddNoteFragment())
                viewModel.onAddNoteNavigated()
                closeFABMenu()
            }
        })


        //check login status
        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->

                Logger.i("test user manager, UserManager.userId=${UserManager.userId}}")
                Logger.i("test user manager, UserManager.isLogin=${UserManager.isLogin}}")

                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    Logger.i("listBox nav to login")
                    findNavController().navigate(NavigationDirections.actionGlobalLoginFragment())

                } else {
                    Logger.i("listBox login ${user.displayName}")


                    val mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
                    mainViewModel.getMainUser(user.uid)

                    viewModel.getBoxesResult()
                }
            }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)




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