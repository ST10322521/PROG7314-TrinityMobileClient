package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// Login screen, nothing here yet
class LoginActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityLoginBinding
    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
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
        // TODO: add sso feature
        Log.d("firebaseAuth", "SSO sign in selected")
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
