package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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







    }
}