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
import com.angela.notemoment.ServiceLocator.repository
import com.angela.notemoment.addbox.AddBoxViewModel
import com.angela.notemoment.data.Box
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch







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
                .setLogo(R.drawable.flag_taiwan)
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
                if(IdpResponse.fromResultIntent(data)?.isNewUser == true) {
                    GlobalScope.launch (Dispatchers.Main) {
                        val defaultBox = Box(
                            title = "未分類",
                            startDate = -2208988800000,
                            endDate = 4133980799000,
                            country = "未選擇",
                            image = "")

                        repository?.publishBox(defaultBox, null)
                        Logger.i("new user add $defaultBox")
                    }
                }
                // Successfully signed in
                Toast.makeText(context, "登入成功", Toast.LENGTH_SHORT).show()
                val user = FirebaseAuth.getInstance().currentUser
                Logger.i("log in user name ${user?.displayName}")
                findNavController().navigateUp()
            }
        }

//        val metadata = FirebaseAuth.getInstance().currentUser?.metadata
//        if (metadata?.creationTimestamp == metadata?.lastSignInTimestamp) {
//        Logger.i("metadata?.creationTimestamp = ${metadata?.creationTimestamp}")
//        Logger.i("metadata?.lastSignInTimestamp = ${metadata?.lastSignInTimestamp}")
//
//            // The user is new, show them a fancy intro screen!
//            CoroutineScope(Dispatchers.Default).launch {
//
//                val defaultBox = Box(
//                    "",
//                    "未分類",
//                    -2208988800000,
//                    4133980799000,
//                    "未選擇",
//                    "")
//
//                repository?.publishBox(defaultBox)
//                Logger.i("new user add $defaultBox")
//            }
//        } else {
//            // This is an existing user, show them a welcome back screen.
//            Logger.i("already sign up :: welcome back")
//        }
    }
    private fun signOut() {
        AuthUI.getInstance()
            .signOut(context!!)
            .addOnSuccessListener {
                Toast.makeText(context, "已登出", Toast.LENGTH_SHORT).show()
            }
    }

}