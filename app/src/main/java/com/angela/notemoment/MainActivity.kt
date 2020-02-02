package com.angela.notemoment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.angela.notemoment.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton




class MainActivity : AppCompatActivity() {

    private var isFabOpen = false
    private lateinit var fab : FloatingActionButton
    private lateinit var fabBox : FloatingActionButton
    private lateinit var fabNote : FloatingActionButton

    //    val viewModel by viewModels<MainViewModel> { getVmFactory() }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel

        setupBottomNav()
//        binding.toolbar.inflateMenu(R.menu.toolbar_menu)



        //fab setting
        fab = findViewById(R.id.fab)
        fabBox = findViewById(R.id.fab_box)
        fabNote = findViewById(R.id.fab_note)
        fab.setOnClickListener {
            if (!isFabOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }


    }


    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
    }


    private fun showFABMenu() {
        isFabOpen = true
        fabBox.animate().translationY(-getResources().getDimension(R.dimen.standard_105))
        fabNote.animate().translationY(-getResources().getDimension(R.dimen.standard_55))
    }

    private fun closeFABMenu() {
        isFabOpen = false
        fabBox.animate().translationY(0F)
        fabNote.animate().translationY(0F)
    }
}