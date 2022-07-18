package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.jovannikolic.myapplication.databinding.ActivityLoginBinding


class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val generalTextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(validPassword() && binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail()){
                    binding.loginbutton.isEnabled = true
                    binding.loginbutton.isClickable = true
                }else{
                    binding.loginbutton.isEnabled = false
                    binding.loginbutton.isClickable = false
                }

                if(!validEmail()){
                    binding.emailerror.text = "Invalid Email Address"
                }else{
                    binding.emailerror.text = null
                }

                if(!validPassword()){
                    binding.passworderror.text = "Password must be at least 6 characters long."
                }else{
                    binding.passworderror.text = null
                }

            }
        }
        binding.emailtext.editText?.addTextChangedListener(generalTextWatcher)
        binding.passwordtext.editText?.addTextChangedListener(generalTextWatcher)




        //  Login button - opens new activity
        binding.loginbutton.setOnClickListener{
            val intent = Intent(this, ActivityShows::class.java)
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

        if(passText.length < 6) {
            return false
        }

        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }



}