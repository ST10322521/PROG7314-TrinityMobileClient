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
import android.text.method.PasswordTransformationMethod
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
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.content.Context
import androidx.core.content.edit

/**
 * https://firebase.google.com/docs/auth/android/google-signin
 * https://developer.android.com/identity/sign-in/biometric-auth
 * https://developer.android.com/reference/kotlin/androidx/biometric/BiometricPrompt
 */
class LoginFragment : Fragment() {

    // View binding for the login screen
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication repository
    private lateinit var authRepository: AuthRepository

    // Credential Manager for Google SSO
    private lateinit var credentialManager: CredentialManager

    // Biometric authentication prompt
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricPromptInfo: BiometricPrompt.PromptInfo

    // Shared preferences used to store the biometric login setting
    private val biometricPreferences by lazy {
        requireContext().getSharedPreferences(
            "biometric_preferences",
            Context.MODE_PRIVATE
        )
    }

    // Determines what should happen after biometric authentication
    private enum class BiometricAction {
        NONE,
        ENABLE,
        LOGIN
    }

    private var biometricAction = BiometricAction.NONE

    private companion object {
        const val BIOMETRIC_LOGIN_ENABLED = "biometric_login_enabled"
        const val BIOMETRIC_USER_ID = "biometric_user_id"
    }

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

        // Get the Firebase authentication repository
        authRepository = AuthProvider.repository()

        // Set up Credential Manager for Google SSO
        credentialManager = CredentialManager.create(requireContext())

        // Set up biometric authentication
        setupBiometricPrompt()

        // Check whether a user already has an active session
        checkCurrentUser()

        setupListeners()
    }

    private fun setupListeners() {

        // Login with email and password
        binding.btnSignIn.setOnClickListener {
            loginUser()
        }

        // Register with email and password
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Login or register using Google SSO
        binding.btnSso.setOnClickListener {
            signInWithSso()
        }

        // Show or hide the password
        binding.ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    // Reveal/mask the password; dim eye = hidden.
    private var passwordVisible = false

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        binding.etPassword.transformationMethod =
            if (passwordVisible) null else PasswordTransformationMethod.getInstance()

        binding.ivTogglePassword.alpha =
            if (passwordVisible) 1f else 0.5f

        binding.etPassword.setSelection(
            binding.etPassword.text?.length ?: 0
        )
    }

    // Login with email and password
    private fun loginUser() {
        Log.d("firebaseAuth", "Email/Password login selected")

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validate the login fields before attempting authentication
        if (!inputVal(email, password)) {
            return
        }

        Log.d("firebaseAuth", "Attempting email/password login")

        // Attempt to authenticate the user with Firebase
        authRepository.loginUser(email, password) { result ->

            result.onSuccess { user ->
                Log.d("firebaseAuth", "Login successful. User: ${user.uid}")

                // Offer biometric login after successful authentication
                offerBiometricLogin()
            }

            result.onFailure { exception ->
                Log.e("firebaseAuth", "login failed", exception)

                Toast.makeText(
                    requireContext(),
                    getAuthErrorMessage(exception),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Registration attempt with email and password
    private fun registerUser() {
        Log.d("firebaseAuth", "Email/Password Registration Selected")

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validate the registration fields
        if (!inputVal(email, password)) {
            return
        }

        // Attempt to register the user with Firebase
        authRepository.registerUser(email, password) { result ->

            result.onSuccess { user ->
                Log.d(
                    "firebaseAuth",
                    "Registration successful, User: ${user.uid}"
                )

                // Offer biometric login after successful registration
                offerBiometricLogin()
            }

                .onFailure { exception ->
                    Log.e("firebaseAuth", "Registration failed", exception)

                    Toast.makeText(
                        requireContext(),
                        getAuthErrorMessage(exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Sign in using Google SSO
    private fun signInWithSso() {
        Log.d("firebaseAuth", "SSO sign in selected")

        // Configure the Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()

            // Use the web client ID generated by Google Services
            .setServerClientId(getString(R.string.default_web_client_id))

            // Allow users to sign in with an account they have not used before
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

                // Process the selected Google credential
                handleGoogleCredential(result.credential)

            } catch (e: GetCredentialCancellationException) {
                Log.e(
                    "firebaseAuth",
                    "SSO cancelled by user",
                    e
                )

            } catch (e: Exception) {
                Log.e(
                    "firebaseAuth",
                    "Google SSO failed",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleGoogleCredential(credential: Credential) {

        // Check that the returned credential is a Google ID credential
        if (
            credential is CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                // Create a Google ID token credential from the returned data
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                // Authenticate the Google account with Firebase
                firebaseAuthWithGoogle(
                    googleIdTokenCredential.idToken
                )

            } catch (e: Exception) {
                Log.e(
                    "firebaseAuth",
                    "Invalid Google ID token",
                    e
                )

                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Log.w(
                "firebaseAuth",
                "Credential is not of type Google ID!"
            )

            Toast.makeText(
                requireContext(),
                getString(R.string.login_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(
            "firebaseAuth",
            "Authenticating Google credentials with firebase"
        )

        // Authenticate the Google ID token with Firebase
        authRepository.signInWithSso(idToken) { result ->

            result.onSuccess {
                Log.d(
                    "firebaseAuth",
                    "signInWithCredential:success"
                )

                // Offer biometric login after successful authentication
                offerBiometricLogin()
            }

                .onFailure { exception ->
                    Log.w(
                        "firebaseAuth",
                        "signInWithCredential:failure",
                        exception
                    )

                    Toast.makeText(
                        requireContext(),
                        getAuthErrorMessage(exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Validate the email and password fields
    private fun inputVal(
        email: String,
        password: String
    ): Boolean {

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.email_is_required)
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_is_required)
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    // Open the main application screen
    private fun openHome() {
        startActivity(
            Intent(
                requireContext(),
                HomeActivity::class.java
            )
        )

        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear the binding when the view is destroyed
        _binding = null
    }

    private fun checkCurrentUser() {

        // Check whether Firebase already has an authenticated user
        val currentUser = authRepository.currentUserId()

        if (currentUser == null) {
            Log.d(
                "firebaseAuth",
                "No user is currently signed in"
            )
            return
        }

        Log.d(
            "firebaseAuth",
            "User is already signed in: $currentUser"
        )

        // Read the saved biometric login settings
        val biometricEnabled = biometricPreferences.getBoolean(
            BIOMETRIC_LOGIN_ENABLED,
            false
        )

        val biometricUserId = biometricPreferences.getString(
            BIOMETRIC_USER_ID,
            null
        )

        // Make sure the biometric setting belongs to the current Firebase user
        val biometricBelongsToCurrentUser =
            biometricUserId == currentUser

        if (
            biometricEnabled &&
            biometricBelongsToCurrentUser &&
            isBiometricAvailable()
        ) {
            Log.d(
                "biometricAuth",
                "Biometric login enabled for current user"
            )

            // Authenticate using the user's biometric
            biometricAction = BiometricAction.LOGIN
            biometricPrompt.authenticate(biometricPromptInfo)

        } else {
            Log.d(
                "biometricAuth",
                "Biometric login unavailable for current user"
            )

            // No biometric authentication is required
            openHome()
        }
    }

    // Convert authentication exceptions into user-friendly messages
    private fun getAuthErrorMessage(exception: Throwable): String {
        return if (exception is AuthException) {
            when (exception.error) {
                AuthError.INVALID_CREDENTIALS ->
                    getString(R.string.invalid_email_or_password)

                AuthError.INVALID_EMAIL ->
                    getString(R.string.please_enter_a_valid_email_address)

                AuthError.EMAIL_ALREADY_IN_USE ->
                    getString(R.string.an_account_with_this_email_already_exists)

                AuthError.NETWORK_ERROR ->
                    getString(R.string.unable_to_connect_please_try_again)

                AuthError.UNKNOWN_ERROR ->
                    getString(R.string.authentication_failed_please_try_again)
            }
        } else {
            getString(R.string.authentication_failed_please_try_again)
        }
    }

    // Configure the biometric authentication prompt
    private fun setupBiometricPrompt() {

        val executor =
            ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    Log.d(
                        "biometricAuth",
                        "Biometric authentication successful"
                    )

                    // Perform the action associated with the biometric request
                    when (biometricAction) {

                        BiometricAction.ENABLE -> {

                            // Get the currently authenticated Firebase user
                            val currentUserId =
                                authRepository.currentUserId()

                            if (currentUserId == null) {
                                Log.e(
                                    "biometricAuth",
                                    "Cannot enable biometric login without a Firebase user"
                                )
                            } else {

                                // Save the biometric preference for this user
                                biometricPreferences.edit {
                                    putBoolean(
                                        BIOMETRIC_LOGIN_ENABLED,
                                        true
                                    )

                                    putString(
                                        BIOMETRIC_USER_ID,
                                        currentUserId
                                    )
                                }

                                Log.d(
                                    "biometricAuth",
                                    "Biometric login enabled for user: $currentUserId"
                                )

                                openHome()
                            }
                        }

                        BiometricAction.LOGIN -> {
                            Log.d(
                                "biometricAuth",
                                "Biometric login successful"
                            )

                            // Open the app after successful biometric authentication
                            openHome()
                        }

                        BiometricAction.NONE -> {
                            Log.w(
                                "biometricAuth",
                                "Biometric authentication had no action"
                            )
                        }
                    }

                    // Reset the biometric action after handling it
                    biometricAction = BiometricAction.NONE
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )

                    Log.e(
                        "biometricAuth",
                        "Authentication error: $errorCode - $errString"
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Log.w(
                        "biometricAuth",
                        "Biometric authentication failed"
                    )
                }
            }
        )

        // Configure the biometric prompt displayed to the user
        biometricPromptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_login))
                .setSubtitle(getString(R.string.use_your_fingerprint_to_sign_in))
                .setNegativeButtonText(getString(R.string.use_another_login_method))
                .setConfirmationRequired(false)
                .build()
    }

    // Check whether strong biometric authentication is available
    private fun isBiometricAvailable(): Boolean {

        val biometricManager =
            BiometricManager.from(requireContext())

        return when (
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> true

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d(
                    "biometricAuth",
                    "No biometric hardware"
                )
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d(
                    "biometricAuth",
                    "Biometric hardware unavailable"
                )
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d(
                    "biometricAuth",
                    "No biometric enrolled"
                )
                false
            }

            else -> {
                Log.d(
                    "biometricAuth",
                    "Biometric authentication unavailable"
                )
                false
            }
        }
    }

    // Ask the user whether they want to enable biometric login
    private fun offerBiometricLogin() {

        if (!isBiometricAvailable()) {
            openHome()
            return
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enable_fingerprint_login))
            .setMessage(getString(R.string.use_your_fingerprint_to_quickly_unlock_the_app_the_next_time_you_sign_in))
            .setPositiveButton("Enable") { _, _ ->
                enableBiometricLogin()
            }
            .setNegativeButton("Not now") { _, _ ->
                openHome()
            }
            .setCancelable(false)
            .show()
    }

    // Start biometric authentication to confirm enabling biometric login
    private fun enableBiometricLogin() {

        Log.d(
            "biometricAuth",
            "Requesting biometric enrollment confirmation"
        )

        biometricAction = BiometricAction.ENABLE

        biometricPrompt.authenticate(
            biometricPromptInfo
        )
    }
}