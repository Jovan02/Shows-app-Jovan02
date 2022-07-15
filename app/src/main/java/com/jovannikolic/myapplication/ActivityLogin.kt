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
import com.google.android.material.textfield.TextInputLayout
import com.jovannikolic.myapplication.databinding.ActivityLoginBinding


class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)


        setContentView(binding.root)

        var pressed = true

        val editEmail : TextInputLayout = findViewById(R.id.emailtext)

        val editPassword : TextInputLayout = findViewById(R.id.passwordtext)
        val generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(editPassword.editText?.text.toString().length > 6 && editEmail.editText?.text.toString().isNotEmpty() && validEmail().equals("")){
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

                if(!editPassword.editText?.text.toString().equals("")){
                    binding.visiblebutton.setVisibility(View.VISIBLE)
                }else if(editPassword.editText?.text.toString().equals("")){
                    binding.visiblebutton.setVisibility(View.GONE)
                }

            }
        }
        editEmail.editText?.addTextChangedListener(generalTextWatcher)
        editPassword.editText?.addTextChangedListener(generalTextWatcher)

        //  Show/Hide button - needs little fix
        binding.visiblebutton.setOnClickListener {
            if (pressed){
                binding.visiblebutton.setBackgroundResource(R.drawable.invisible_button)
                editPassword.editText?.transformationMethod = PasswordTransformationMethod.getInstance()
                pressed = false
            }else{
                binding.visiblebutton.setBackgroundResource(R.drawable.visible_button)
                editPassword.editText?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pressed = true
            }
        }






        //  Login button - opens new activity
        binding.loginbutton.setOnClickListener{
            val intent = Intent(this, ActivityShows::class.java)
            intent.putExtra("username", editEmail.editText?.text.toString())
            startActivity(intent)
        }



    }

    private fun validEmail(): String {

        val editEmail : TextInputLayout = findViewById(R.id.emailtext)

        val emailText = editEmail.editText?.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        return ""
    }

    private fun validPassword(): String {

        val editPassword : TextInputLayout = findViewById(R.id.passwordtext)

        val passText = editPassword.editText?.text.toString()

        if(passText.length < 7) {
            return "Password must be at least 7 characters long."
        }

        return ""
    }



}