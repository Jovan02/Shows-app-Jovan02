package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jovannikolic.myapplication.databinding.ActivityShowDetailsBinding

class ActivityShowDetails : AppCompatActivity() {

    lateinit var binding: ActivityShowDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)








    }
}