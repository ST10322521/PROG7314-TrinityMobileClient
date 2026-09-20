package Emeris.PROG7314.trinitymobileclient.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object AuthProvider {
    private val auth: FirebaseAuth by lazy {
        Firebase.auth.apply {
            //TODO: FOR TESTING ONLY
            useEmulator("127.0.0.1", 9099)

            Log.d("firebaseAuth", "FirebaseAuth emulator configured")
        }
    }

    fun repository(): AuthRepository {
        return AuthRepository(auth)
    }
}