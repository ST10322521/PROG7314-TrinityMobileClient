package Emeris.PROG7314.trinitymobileclient.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object AuthProvider {

    /**
     * Firebase Authentication instance to be used throughout the app
     */
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    /**
     * Creates and returns an AuthRepo using the shared firebase auth instance
     */
    fun repository(): AuthRepository {
        return AuthRepository(auth)
    }
}