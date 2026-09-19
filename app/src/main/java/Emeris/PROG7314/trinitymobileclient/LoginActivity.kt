package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

// Login screen, nothing here yet
class LoginActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityLoginBinding
    // Firebase authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var authRepository: AuthRepository
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        authRepository = AuthRepository(auth)
        credentialManager = CredentialManager.create(this)
        // TODO: FOR TESTING ONLY
        auth.useEmulator("127.0.0.1", 9099)

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
            openHome()
        } else {
            Log.d("firebaseAuth", "No user is currently signed in")
        }
    }

    // Login with Email/Password
    private fun loginUser(){
        Log.d("firebaseAuth", "Email/Password login selected")
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        //basic input validation
        if(!inputVal(email,password)){
            return
        }

        Log.d("firebaseAuth", "Attempting email/password login")

        // login attempt
        authRepository.loginUser(email,password) { result ->

            result.onSuccess { user ->
                Log.d("firebaseAuth", "Login successful. User: ${user?.uid}")
                openHome()
            }
            .onFailure {
                exception ->
                Log.e("firebaseAuth", "login failed", exception)
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // Registration attempt with Email/Password
    private fun registerUser(){
        Log.d("firebaseAuth", "Email/Password Registration Selected")
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        //basic input validation
        if(!inputVal(email,password)){
            return
        }

        // registration attempt
        authRepository.registerUser(email,password) { result ->
            result.onSuccess { user ->
                Log.d("firebaseAuth", "Registration successful, User: ${user?.uid}")
                openHome()
            }
            .onFailure { exception ->
                Log.e("firebaseAuth", "Registration failed", exception)
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
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
            // Allow users to sign in with an account even if they have not used it before.
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
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }

        } else {
            Log.w("firebaseAuth", "Credential is not of type Google ID!")
            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("firebaseAuth", "Authenticating Google credentials with firebase")

        authRepository.signInWithSso(idToken) {result ->
            result.onSuccess {
                // Sign in success, update UI with the signed-in user's information
                Log.d("firebaseAuth", "signInWithCredential:success")
                openHome()
            }
            .onFailure { exception ->
                // If sign in fails, display a message to the user
                Log.w("firebaseAuth", "signInWithCredential:failure", exception)
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
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
