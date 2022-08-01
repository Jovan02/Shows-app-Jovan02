package com.jovannikolic.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.jovannikolic.myapplication.databinding.ActivityMainBinding
import networking.ApiModule

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiModule.initRetrofit(this)
    }
}