package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jovannikolic.myapplication.databinding.ActivityWelcomeBinding

class ActivityWelcome : AppCompatActivity() {

    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email: String? = intent.extras?.getString("username")

        val tokens = email?.split("@")
        val username = tokens?.get(0)

        binding.welcometext.setText("Welcome, ".plus(username))

    }

}