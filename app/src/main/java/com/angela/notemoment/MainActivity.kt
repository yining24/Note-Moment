package com.angela.notemoment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.angela.notemoment.databinding.ActivityMainBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.util.CurrentFragmentType
import com.google.android.material.bottomnavigation.BottomNavigationItemView


class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    lateinit var binding: ActivityMainBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupNavController()

        viewModel.onUpdateViewModel.observe(this, Observer {
            Logger.d("viewModel.onUpdateViewModel.observe, it=$it")
            it?.let {
                binding.viewModel = viewModel
                viewModel.onViewModelUpdated()
            }
        })
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

