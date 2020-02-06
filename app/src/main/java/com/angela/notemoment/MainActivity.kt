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
import androidx.navigation.findNavController
import com.angela.notemoment.data.User
import com.angela.notemoment.databinding.ActivityMainBinding
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.login.LoginFragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123
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
//                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalProfileFragment())
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


    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnSuccessListener {
                Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show()
            }
    }


}


