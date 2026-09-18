package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

// Login screen, nothing here yet
class LoginActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityLoginBinding
    // Firebase authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)
        // TODO: FOR TESTING ONLY
        auth.useEmulator("10.0.2.2",9099)

        // pad for the status/nav bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Login with email and password
        binding.btnSignIn.setOnClickListener { loginUser() }
        // Register with email and password
        binding.btnRegister.setOnClickListener { registerUser() }
        // SSO
        binding.btnSso.setOnClickListener { signInWithSso() }
    }

    override fun onStart(){
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null){
            Log.d("firebaseAuth", "User is signed in: ${currentUser.uid}")
        } else {
            Log.d("firebaseAuth", "No user is currently signed in")
        }
    }

    private fun loginUser(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        //basic input validation
        if(!inputVal(email,password)){
            return
        }

        Log.d("firebaseAuth", "Attempting email/password login")

        // login attempt
        auth.signInWithEmailAndPassword(email,password ).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                val user = auth.currentUser

                Log.d("firebaseAuth", "Login successful. User: ${user?.uid}")

                openHome()
            } else {
                Log.e("firebaseAuth", "login failed", task.exception)
            }
        }
    }

    private fun registerUser(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        //basic input validation
        if(!inputVal(email,password)){
            return
        }


        // registration attempt
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful){
                val user = auth.currentUser

                Log.d("firebaseAuth", "Registration successful, User: ${user?.uid}")

                openHome()
            } else {
                Log.e("firebaseAuth", "Registration failed", task.exception)
            }
        }
    }

    // Sign in/on with sso
    private fun signInWithSso(){
        Log.d("firebaseAuth", "SSO sign in selected")

        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(getString(R.string.default_web_client_id))
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                handleGoogleCredential(result.credential)
            } catch (e: Exception) {
                Log.e("firebaseAuth", "Google SSO failed", e)
            }
        }
    }

    private fun handleGoogleCredential(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // Sign in to Firebase with using the token
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } catch (e: Exception) {
                Log.e("firebaseAuth", "Invalid Google ID token", e)
            }

        } else {
            Log.w("firebaseAuth", "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("firebaseAuth", "Authenticating Google credentials with firebase")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebaseAuth", "signInWithCredential:success")
                    openHome()
                } else {
                    // If sign in fails, display a message to the user
                    Log.w("firebaseAuth", "signInWithCredential:failure", task.exception)
                    openHome()
                }
            }
    }

    private fun inputVal(email:String, password:String): Boolean{
        //basic input validation
        if(email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        }
        if(password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
