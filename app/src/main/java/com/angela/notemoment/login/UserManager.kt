package com.angela.notemoment.login

import com.google.firebase.auth.FirebaseAuth

object UserManager {

    val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    val isLogin: Boolean
        get() = userId != null
}