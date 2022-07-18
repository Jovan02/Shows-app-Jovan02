package com.jovannikolic.myapplication

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

        var pressed = true

        //  Login button - active if required conditions are fulfilled
        val generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

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

                if(!validEmail().equals("")){
                    //binding.emailtext.setError(validEmail())
                    binding.emailerror.text = validEmail()
                }else{
                    //binding.emailtext.setError(null)
                    binding.emailerror.text = null
                }

                if(!validPassword().equals("")){
                    binding.passworderror.text = validPassword()
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

    private fun validEmail(): String {

        val emailText = binding.emailtext.editText?.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        return ""
    }

    private fun validPassword(): String {

        val passText = binding.passwordtext.editText?.text.toString()

        if(passText.length < 7) {
            return "Password must be at least 7 characters long."
        }

        return ""
    }


}