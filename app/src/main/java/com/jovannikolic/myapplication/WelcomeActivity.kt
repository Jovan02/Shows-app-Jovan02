package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jovannikolic.myapplication.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email: String? = intent.extras?.getString("username")

        val username: String? = email?.let { createUsername(it) }

        binding.welcometext.setText("Welcome, ".plus(username))





    }

    private fun createUsername(string: String): String {
        var username = ""
        for (i in string.indices){
            if(string[i] == '@'){
                break
            }else{
                username += string[i]
            }
        }
        return username
    }

}