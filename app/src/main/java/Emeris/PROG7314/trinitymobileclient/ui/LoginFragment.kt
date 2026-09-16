package Emeris.PROG7314.trinitymobileclient.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import Emeris.PROG7314.trinitymobileclient.R
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentLoginBinding
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // enable view binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        // TODO: FOR TESTING ONLY
        auth.useEmulator("10.0.2.2",9099)

        Log.d("firebaseAuth", "Firebase authentication initialized")
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

    override fun onCreateView(
        inflater:LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
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
        auth.signInWithEmailAndPassword(email,password ).addOnCompleteListener(requireActivity()) { task ->
            if(task.isSuccessful){
                val user = auth.currentUser

                Log.d("firebaseAuth", "Login successful. User: ${user?.uid}")
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
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful){
                val user = auth.currentUser

                Log.d("firebaseAuth", "Registration successful, User: ${user?.uid}")
            } else {
                Log.e("firebaseAuth", "Registration failed", task.exception)
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
}