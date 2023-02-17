package com.example.passwordmanagerassignment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.passwordmanagerassignment.Utils.verifyLogInData
import com.example.passwordmanagerassignment.Utils.verifySignUpData
import com.example.passwordmanagerassignment.databinding.ActivityMainBinding
import com.example.passwordmanagerassignment.databinding.SignUpAdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser != null){
            startActivity(Intent(this, ManagerActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            if (verifyLogInData(email, pass)){
                logInUser(email, pass)
            }else{
                Toast.makeText(this, "Fill out all the fields!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtSignUp.setOnClickListener {
            buildAlertDialog()
        }

    }


    private fun buildAlertDialog() {
        val bindingItem = SignUpAdBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setView(bindingItem.root)
            .setTitle("Registration")
            .setNegativeButton("Close"){_,_->}
            .setPositiveButton("Create Account"){_,_->
                val email = bindingItem.edtEmail.text.toString()
                val pass = bindingItem.edtPassword.text.toString()
                val pass2 = bindingItem.edtPassword2.text.toString()
                if (verifySignUpData(email, pass, pass2)){
                    singUpUser(email, pass)
                }else{
                    Toast.makeText(this, "Fill out all the fields properly!", Toast.LENGTH_SHORT).show()
                }

            }
            .create().show()
    }

    private fun singUpUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ManagerActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logInUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this, "Sign in successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ManagerActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }
}