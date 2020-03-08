package com.angela.notemoment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.data.User
import com.angela.notemoment.databinding.ActivityMainBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.login.LoginFragment
import com.angela.notemoment.util.CurrentFragmentType
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.animation.AnimatorListenerAdapter
import android.view.animation.*
import androidx.navigation.ui.setupWithNavController
import com.angela.notemoment.data.source.NoteRepository
import com.angela.notemoment.login.UserManager


class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    lateinit var binding: ActivityMainBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupBottomNav()
        setupNavController()

    }


    private fun setupBottomNav() {
        binding.bottomNavView.itemIconTintList = null
    }

    private fun setupNavController() {
        binding.bottomNavView.setupWithNavController(findNavController(R.id.myNavHostFragment))
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->

            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.addNoteFragment -> CurrentFragmentType.ADDNOTE
                R.id.addboxFragment -> CurrentFragmentType.ADDBOX
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.listFragment -> CurrentFragmentType.LIST
                R.id.listNoteFragment -> CurrentFragmentType.LISTNOTE
                R.id.mapFragment -> CurrentFragmentType.MAP

                else -> viewModel.currentFragmentType.value
            }
        }
    }

}

