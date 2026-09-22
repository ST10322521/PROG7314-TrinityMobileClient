package Emeris.PROG7314.trinitymobileclient.auth

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/**
 * https://firebase.google.com/docs/auth/android/password-auth
 * https://firebase.google.com/docs/auth/android/google-signin
 */
class AuthRepository(
    private val auth: FirebaseAuth
) {

    internal fun mapAuthError(exception: Exception): AuthError {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> AuthError.INVALID_CREDENTIALS
            is FirebaseAuthInvalidUserException -> AuthError.INVALID_CREDENTIALS
            is FirebaseAuthUserCollisionException -> AuthError.EMAIL_ALREADY_IN_USE
            is FirebaseNetworkException -> AuthError.NETWORK_ERROR
            else -> AuthError.UNKNOWN_ERROR
        }
    }

    /**
     * Register a new user using their email and password.
     *
     * @param email
     * @param password
     * @param onResult Callback invoked with the result of the registration attempt.
     * @returns [Result.success] containing the [FirebaseUser] if registered, or [Result.failure] containing the error if registration fails
     */
    fun registerUser(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ){
        // registration attempt
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = task.result?.user

                if(user != null){
                    onResult(Result.success(user))
                } else {
                    onResult(Result.failure(IllegalStateException("Successful Registration, No User Returned")))
                }
            } else {
                val exception = task.exception ?: Exception("Registration Failed")
                val authError = mapAuthError(exception)
                onResult(Result.failure(AuthException(authError, exception)))
            }
        }
    }

    /**
     * Sign user in with email and password.
     *
     * @param email
     * @param password
     * @param onResult Callback invoked with the result of the registration attempt.
     * @returns [Result.success] containing the [FirebaseUser] if login succeeds, or [Result.failure] containing the error if login fails
     */
    fun loginUser(
        email: String,
        password: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ){
        // login attempt
        auth.signInWithEmailAndPassword(email,password ).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val user = task.result?.user

                if(user != null){
                    onResult(Result.success(user))
                } else {
                    onResult(Result.failure(IllegalStateException("Successful Login, No User Returned")))
                }
            } else {
                val exception = task.exception ?: Exception("Login Failed")
                val authError = mapAuthError(exception)
                onResult(Result.failure(AuthException(authError, exception)))
            }
        }
    }

    /**
     * Sign user in with SSO
     *
     * @param idToken SSO Token obtained from Credential Manager
     * @param onResult Callback invoked with the result of the registration attempt.
     * @returns [Result.success] containing the [FirebaseUser] if login succeeds, or [Result.failure] containing the error if login fails
     */
    fun signInWithSso(
        idToken: String,
        onResult: (Result<FirebaseUser>) -> Unit
    ){

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null){
                        onResult(Result.success(user))
                    } else {
                        onResult(Result.failure(IllegalStateException("Successful SSO Login, no user returned")))
                    }
                } else {
                    val exception = task.exception ?: Exception("SSO Login Failed")
                    val authError = mapAuthError(exception)
                    onResult(Result.failure(AuthException(authError, exception)))
                }
            }
    }

    /**
    * Return current auth used
    *
    * @returns authenticated user uid [userId], or null if no user is authenticated
     */
    fun currentUserId(): String? {
        val userId = auth.currentUser?.uid

        if (userId != null){
            Log.d("firebaseAuth", "Authenticated user: $userId")
        } else {
            Log.d("firebaseAuth", "No authenticated user found")
        }
        return userId
    }

    /**
     * Returns auth users username
     *
     * @return authenticated users [username], or null if it cant be found
     */
    fun currentUserUsername(): String? {
        val userEmail = auth.currentUser?.email
        val username = userEmail?.substringBefore("@")

        if (username != null){
            Log.d("firebaseAuth", "Authenticated user: $username")
        } else {
            Log.d("firebaseAuth", "No authenticated user found")
        }
        return username
    }

    /**
     * Signs out current user
      */
    fun logout() {
        val user = auth.currentUser

        if (user != null){
            Log.d("firebaseAuth", "Signing out ${user.uid}")
        } else {
            Log.e("firebaseAuth", "Sign-out requested with no signed in user")
        }

        auth.signOut()
    }
}