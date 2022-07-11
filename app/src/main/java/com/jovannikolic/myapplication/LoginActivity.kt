package com.jovannikolic.myapplication

import android.annotation.SuppressLint
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

        //  Floating Email label
        binding.emailtext.setOnFocusChangeListener { _, focused ->
            if(!binding.emailtext.text.toString().equals("") || focused){
                binding.emaillabel.setVisibility(View.VISIBLE)
                binding.emailtext.hint = " "
            }else if(binding.emailtext.text.toString().equals("") || focused){
                binding.emaillabel.setVisibility(View.INVISIBLE)
                binding.emailtext.hint = "Email"
            }
        }

        //  Show/Hide button
        binding.visiblebutton.setOnClickListener {
            if (pressed){
                binding.visiblebutton.setBackgroundResource(R.drawable.invisible_button)
                binding.passwordtext.transformationMethod = PasswordTransformationMethod.getInstance()
                pressed = false
            }else{
                binding.visiblebutton.setBackgroundResource(R.drawable.visible_button)
                binding.passwordtext.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pressed = true
            }
        }

        //  Floating Password label and Show/Hide button
        binding.passwordtext.setOnFocusChangeListener { _, focused ->

            if(!binding.passwordtext.text.toString().equals("") || focused){
                binding.visiblebutton.setVisibility(View.VISIBLE)
                binding.passwordlabel.setVisibility(View.VISIBLE)
                binding.passwordtext.hint = " "
            }else if(binding.passwordtext.text.toString().equals("") || focused){
                binding.visiblebutton.setVisibility(View.GONE)
                binding.passwordlabel.setVisibility(View.INVISIBLE)
                binding.passwordtext.hint = "Password"
            }
        }


        //  Login button - active if required conditions are fulfilled
        val generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.passwordtext.text.toString().length > 6 && binding.emailtext.text.toString().isNotEmpty() && validEmail().equals("")){
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
                    //binding.passwordtext.setError(validPassword())
                    binding.passworderror.text = validPassword()
                }else{
                    //binding.passwordtext.setError(null)
                    binding.passworderror.text = null
                }

            }
        }
        binding.emailtext.addTextChangedListener(generalTextWatcher)
        binding.passwordtext.addTextChangedListener(generalTextWatcher)



    }

    private fun validEmail(): String {

        val emailText = binding.emailtext.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        return ""
    }

    private fun validPassword(): String {

        val passText = binding.passwordtext.text.toString()

        if(passText.length < 7) {
            return "Password must be at least 7 characters long."
        }

        return ""
    }



}