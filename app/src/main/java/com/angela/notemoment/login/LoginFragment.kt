package com.angela.notemoment.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger
import com.angela.notemoment.MainActivity
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.data.User
import com.angela.notemoment.ext.showToast
import com.angela.notemoment.util.MyRequestCode
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {


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
                .setLogo(R.drawable.logo_trans)
                .setTheme(R.style.LoginTheme)
                .build(),
            MyRequestCode.RC_SIGN_IN.value
        )
        return inflater.inflate(R.layout.fragment_login, container, false)
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
                this.showToast("Hello~${user?.displayName}")

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user!!.uid)
                    .get()
                    .addOnSuccessListener {
                        Logger.w("Welcome back ${user.displayName}")
                        Logger.w("user snap it = $it")
                        if (it != null) {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .set(User(user.uid, user.displayName ?: "", "Spot Moment", user.email?:""))
                        }
                    }
                    .addOnFailureListener {
                        Logger.w("First log in $user")
                    }

                findNavController().navigate(NavigationDirections.actionGlobalListFragment())
            }
        }


    }
    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
                Toast.makeText(context, "已登出", Toast.LENGTH_SHORT).show()
            }
    }

}