package com.angela.notemoment.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.*
import com.angela.notemoment.addnote.AddNoteFragmentDirections
import com.angela.notemoment.ext.getVmFactory
import com.angela.notemoment.ext.showToast
import com.angela.notemoment.util.MyRequestCode
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //fb and google login
        val authProvider: List<AuthUI.IdpConfig> = listOf(
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(authProvider)
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.logo_background_color)
                .setTheme(R.style.LoginTheme)
                .build(),
            MyRequestCode.RC_SIGN_IN.value
        )

        return super.onCreateView(inflater, container, savedInstanceState)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MyRequestCode.RC_SIGN_IN.value) {
            if (resultCode != Activity.RESULT_OK) {
                val response = IdpResponse.fromResultIntent(data)
                if (response == null) {
                    // back button is pressed
                    this.showToast("尚未登入")
                    (activity as MainActivity).finish()
                }
            } else {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    this.showToast("Hello~${it.displayName}")
                    viewModel.checkUserResult(user.uid)
                }

                viewModel.navigateToListBox.observe(this, Observer {
                    it?.let {
                        findNavController().navigate(NavigationDirections.actionGlobalListFragment())
                        viewModel.onListBoxNavigated()
                    }
                })
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
                Toast.makeText(context, "Log out", Toast.LENGTH_SHORT).show()
            }
    }

}