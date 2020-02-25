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


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123
    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    lateinit var binding: ActivityMainBinding

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
//                R.id.navigation_home -> {
//
//                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalHomeFragment())
//                    return@OnNavigationItemSelectedListener true
//                }
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

        //check login status
        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalLoginFragment())

                } else {
                    val user = FirebaseAuth.getInstance().currentUser
                    Logger.i("main login ${user?.displayName}")
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user!!.uid)
                        .set(User(user.uid, user.displayName ?: "", "Note my moment"))
                }
            }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)


        setupNavController()

    }


    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding.bottomNavView.itemIconTintList = null
//        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(2) as BottomNavigationItemView

    }

    private fun setupNavController() {
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

