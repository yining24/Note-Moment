package com.angela.notemoment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.databinding.ActivityMainBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton




class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private var isFabOpen = false
    private lateinit var fab : FloatingActionButton
    private lateinit var fabBox : FloatingActionButton
    private lateinit var fabNote : FloatingActionButton

    private lateinit var binding: ActivityMainBinding

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_list -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalListFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalMapFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupBottomNav()
//        binding.toolbar.inflateMenu(R.menu.toolbar_menu)





        //fab setting
        fab = findViewById(R.id.fab)
        fabBox = findViewById(R.id.fab_box)
        fabNote = findViewById(R.id.fab_note)
        fab.bringToFront()
        fab.setOnClickListener {
            if (!isFabOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        viewModel.navigateToAddBox.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalAddboxFragment())
                viewModel.onAddBoxNavigated()
                closeFABMenu()
            }
        })

        viewModel.navigateToAddNote.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalAddNoteFragment())
                viewModel.onAddNoteNavigated()
                closeFABMenu()
            }
        })


    }


    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
    }


    @SuppressLint("RestrictedApi")
    private fun showFABMenu() {
        isFabOpen = true
        fabBox.visibility = View.VISIBLE
        fabNote.visibility = View.VISIBLE
        fabBox.animate().translationY(-getResources().getDimension(R.dimen.standard_105))
        fabNote.animate().translationY(-getResources().getDimension(R.dimen.standard_55))
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
    }
}