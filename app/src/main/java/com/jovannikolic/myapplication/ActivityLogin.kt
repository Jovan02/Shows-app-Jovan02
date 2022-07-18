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
import com.jovannikolic.myapplication.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Login button - active if required conditions are fulfilled
        val generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(validPassword().equals("") && binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail().equals("")){
                    binding.loginbutton.isEnabled = true
                    binding.loginbutton.isClickable = true
                    binding.loginbutton.setBackgroundResource(R.drawable.login_button)
                }else{
                    binding.loginbutton.isEnabled = false
                    binding.loginbutton.isClickable = false
                    binding.loginbutton.setBackgroundResource(R.drawable.login_button_not_clickable)
                }

                if(!validEmail()){
                    //binding.emailtext.setError(validEmail())
                    binding.emailerror.text = "Invalid Email Address"
                }else{
                    //binding.emailtext.setError(null)
                    binding.emailerror.text = null
                }

                if(!validPassword()){
                    binding.passworderror.text = "Password must be at least 7 characters long."
                }else{
                    binding.passworderror.text = null
                }

            }
        }
        binding.emailtext.editText?.addTextChangedListener(generalTextWatcher)
        binding.passwordtext.editText?.addTextChangedListener(generalTextWatcher)

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


}