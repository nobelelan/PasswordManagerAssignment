package com.example.passwordmanagerassignment

object Utils {

    fun verifyLogInData(email: String, pass: String): Boolean {
        return (email.isNotEmpty() && pass.isNotEmpty())
    }

    fun verifySignUpData(email: String, pass: String, pass2: String): Boolean{
        return (email.isNotEmpty() && pass.isNotEmpty() && pass2.isNotEmpty() && (pass == pass2))
    }
}