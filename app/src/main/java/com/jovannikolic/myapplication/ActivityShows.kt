package com.jovannikolic.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jovannikolic.myapplication.databinding.ActivityShowsBinding
import layout.Show

class ActivityShows : AppCompatActivity() {

    companion object {
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ActivityShows:: class.java)
        }

    }

    private val shows = listOf(
        Show(0, "Family Guy", "Family Guy is an American adult animated sitcom created by Seth...", R.drawable.family_guy),
        Show(1, "Simpsons", "The Simpsons is an American animated sitcom created by Matt Groening. The...", R.drawable.simpsons),
        Show(2, "Prison Break", "Prison Break is an American serial drama television series created by Paul...", R.drawable.prison_break),
        Show(3, "Sherlock", "Sherlock is a British mystery crime drama television series based on Sir Arthur...", R.drawable.sherlock),
        Show(4, "Witcher", "The Witcher is a Polish-American fantasy drama television series created by...", R.drawable.witcher),
        Show(5, "Friends", "Friends is an American television sitcom created by David Crane and Marta...", R.drawable.friends),
    )



    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter : ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.showbutton.setOnClickListener{
            binding.emptystateimage.setVisibility(View.INVISIBLE)
            binding.emptystatetext.setVisibility(View.INVISIBLE)
            binding.showbutton.setVisibility(View.GONE)

            initShowsRecycler()
        }


    }

    private fun initShowsRecycler(){

        adapter = ShowsAdapter(shows){ show ->
            Toast.makeText(this, show.name, Toast.LENGTH_SHORT).show()
        }

        binding.showsrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.showsrecycler.adapter = adapter
    }

}