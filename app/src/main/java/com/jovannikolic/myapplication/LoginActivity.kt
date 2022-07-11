package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jovannikolic.myapplication.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)


        setContentView(binding.root)

        binding.emailtext.setOnFocusChangeListener { _, focused ->
            if(!binding.emailtext.text.toString().equals("") || focused){
                binding.emaillabel.setVisibility(View.VISIBLE)
                binding.emailtext.hint = " "
            }else if(binding.emailtext.text.toString().equals("") || focused){
                binding.emaillabel.setVisibility(View.INVISIBLE)
                binding.emailtext.hint = "Email"
            }
        }




    }
}