package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.jovannikolic.myapplication.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isEmailValid = false
        var isPasswordValid = false

        //  Email TextWatcher
        binding.emailtext.editText?.addTextChangedListener {
            if(binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail()){
                isEmailValid = true
                binding.emailerror.text = null
            }else{
                isEmailValid = false
                binding.emailerror.text = "Invalid Email Address"
            }
            checkLoginButtonState(isEmailValid, isPasswordValid)
        }


        //  Password TextWatcher
        binding.passwordtext.editText?.addTextChangedListener {
            if(validPassword()){
                isPasswordValid = true
                binding.passworderror.text = null
            }else{
                isPasswordValid = false
                binding.passworderror.text = "Password must be at least 7 characters long."
            }
            checkLoginButtonState(isEmailValid, isPasswordValid)
        }

        //  Login button - opens new activity
        binding.loginbutton.setOnClickListener{
            val intent = Intent(this, ActivityWelcome::class.java)
            intent.putExtra("username", binding.emailtext.editText?.text.toString())
            startActivity(intent)
        }

    }

    private fun validEmail(): Boolean {

        val emailText = binding.emailtext.editText?.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return false
        }
        return true
    }

    private fun validPassword(): Boolean {

        val passText = binding.passwordtext.editText?.text.toString()

        if(passText.length < 7) {
            return false
        }

        return true
    }

    private fun checkLoginButtonState(email : Boolean, password : Boolean){
        binding.loginbutton.isEnabled = email && password
    }


}