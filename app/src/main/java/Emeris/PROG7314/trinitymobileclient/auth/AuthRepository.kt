package Emeris.PROG7314.trinitymobileclient.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository(
    private val auth: FirebaseAuth
) {
    /**
     * Register a new user using their email and password.
     *
     * @param email
     * @param password
     * @param onResult Callback invoked with the result of the registration attempt.
     * Returns [Result.success] containing the [FirebaseUser] if registered, or [Result.failure] containing the error if registration fails
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
                onResult(Result.failure(task.exception?: Exception("Registration Failed")))
            }
        }
    }

    /**
     * Sign user in with email and password.
     *
     * @param email
     * @param password
     * @param onResult Callback invoked with the result of the registration attempt.
     * Returns [Result.success] containing the [FirebaseUser] if login succeeds, or [Result.failure] containing the error if login fails
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
                onResult(Result.failure(task.exception?: Exception("Login Failed")))
            }
        }
    }

    /**
     * Sign user in with SSO
     *
     * @param idToken SSO Token obtained from Credential Manager
     * @param onResult Callback invoked with the result of the registration attempt.
     * Returns [Result.success] containing the [FirebaseUser] if login succeeds, or [Result.failure] containing the error if login fails
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
                    onResult(Result.failure(task.exception?: Exception("SSO Login Failed")))
                }
            }
    }
}