package com.jovannikolic.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.jovannikolic.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        sharedPreferences = this.getSharedPreferences("LoginData", Context.MODE_PRIVATE)

        val myNavHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = myNavHostFragment.navController.navInflater
        val graph: NavGraph

        if (sharedPreferences.getBoolean("remember", false)) {
            graph = inflater.inflate(R.navigation.show_nav)
        } else {
            graph = inflater.inflate(R.navigation.main)
        }

        myNavHostFragment.navController.graph = graph


        setContentView(binding.root)
    }


}