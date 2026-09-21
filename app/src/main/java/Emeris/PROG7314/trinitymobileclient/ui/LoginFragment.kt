package Emeris.PROG7314.trinitymobileclient.ui

import Emeris.PROG7314.trinitymobileclient.HomeActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.auth.AuthError
import Emeris.PROG7314.trinitymobileclient.auth.AuthException
import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLoginBinding
import android.content.Intent
import androidx.credentials.CredentialManager
import android.util.Log
import android.widget.Toast
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    // set up view binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // firebase auth
    private lateinit var authRepository: AuthRepository
    // sso credential manager
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        authRepository = AuthProvider.repository()

        // setup credential manager
        credentialManager = CredentialManager.create(requireContext())

        // auto login if there is an active session
        checkCurrentUser()

        setupListeners()
    }

    private fun setupListeners() {
        // login with Email/Password
        binding.btnSignIn.setOnClickListener {
            loginUser()
        }
        // register with Email/Password
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        // register/login with SSO
        binding.btnSso.setOnClickListener {
            signInWithSso()
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
                Log.d("firebaseAuth", "Login successful. User: ${user.uid}")
                openHome()
            }
            result.onFailure { exception ->
                Log.e("firebaseAuth", "login failed", exception)
                Toast.makeText(requireContext(), getAuthErrorMessage(exception), Toast.LENGTH_SHORT).show()
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
                Log.d("firebaseAuth", "Registration successful, User: ${user.uid}")
                openHome()
            }
            .onFailure { exception ->
                Log.e("firebaseAuth", "Registration failed", exception)
                Toast.makeText(requireContext(), getAuthErrorMessage(exception), Toast.LENGTH_SHORT).show()
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
                    context = requireContext(),
                    request = request
                )

                handleGoogleCredential(result.credential)
            } catch (e: GetCredentialCancellationException) {
                Log.e("firebaseAuth", "SSO cancelled by user",e)
            }
            catch (e: Exception) {
                Log.e("firebaseAuth", "Google SSO failed", e)
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            }

        } else {
            Log.w("firebaseAuth", "Credential is not of type Google ID!")
            Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), getAuthErrorMessage(exception), Toast.LENGTH_SHORT).show()
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
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun checkCurrentUser(){
        val currentUser = authRepository.currentUserId()

        if (currentUser != null) {
            Log.d("firebaseAuth", "User is already signed in: $currentUser")
            openHome()
        } else {
            Log.d("firebaseAuth", "No user is currently signed in")
        }
    }

    private fun getAuthErrorMessage(exception: Throwable): String {
        return if (exception is AuthException) {
            when (exception.error){
                AuthError.INVALID_CREDENTIALS -> getString(R.string.invalid_email_or_password)
                AuthError.INVALID_EMAIL -> getString(R.string.please_enter_a_valid_email_address)
                AuthError.EMAIL_ALREADY_IN_USE -> getString(R.string.an_account_with_this_email_already_exists)
                AuthError.NETWORK_ERROR -> getString(R.string.unable_to_connect_please_try_again)
                AuthError.UNKNOWN_ERROR -> getString(R.string.authentication_failed_please_try_again)
            }
        } else {
            getString(R.string.authentication_failed_please_try_again)
        }
    }
}