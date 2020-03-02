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
import androidx.test.core.app.ActivityScenario.launch
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.angela.notemoment.*
import com.angela.notemoment.data.User
import com.angela.notemoment.ext.showToast
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {

    val RC_SIGN_IN = 12345

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
            RC_SIGN_IN
        )

        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                val response = IdpResponse.fromResultIntent(data)
                if (response == null) {
                    // back button is pressed
                    Toast.makeText(
                        context,
                        "尚未登入",
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? MainActivity)?.finish()
                }
            } else {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(context, "Hello~${user?.displayName}", Toast.LENGTH_SHORT).show()

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user!!.uid).get().addOnSuccessListener {
                        Logger.w("Welcome back ${user.displayName}")

                    }.addOnFailureListener {
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.uid)
                            .set(User(user.uid, user.displayName ?: "", "Spot Moment", user.email?:""))
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