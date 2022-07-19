package com.jovannikolic.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jovannikolic.myapplication.databinding.ActivityShowsBinding
import models.Show

class ActivityShows : AppCompatActivity() {

    companion object {
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity, ActivityShows:: class.java)
        }

    }

    private val shows = listOf(
        Show(0, "Family Guy", "Family Guy is an American adult animated sitcom created by Seth MacFarlane. The series centers on the Griffins, a family consisting of parents Peter and Lois; their children, Meg, Chris, and Stewie; and their anthropomorphic pet dog, Brian. Set in the fictional city of Quahog, Rhode Island, the show exhibits much of its humor in the form of metafictional cutaway gags that often lampoon American culture.", R.drawable.family_guy),
        Show(1, "Simpsons", "The Simpsons is an American animated sitcom created by Matt Groening. The series is a satirical depiction of American life, epitomized by the Simpson family, which consists of Homer, Marge, Bart, Lisa, and Maggie. The show is set in the fictional town of Springfield and parodies American culture and society, television, and the human condition.", R.drawable.simpsons),
        Show(2, "Prison Break", "Prison Break is an American serial drama television series created by Paul Scheuring. The series revolves around two brothers, Lincoln Burrows and Michael Scofield. Burrows has been sentenced to death for a crime he did not commit, and Scofield devises an elaborate plan to help his brother escape prison and clear his name.", R.drawable.prison_break),
        Show(3, "Sherlock", "Sherlock is a British mystery crime drama television series based on Sir Arthur Conan Doyle's Sherlock Holmes detective stories. Sherlock depicts \"consulting detective\" Sherlock Holmes solving various mysteries in modern-day London. Holmes is assisted by his flatmate and friend, Dr John Watson , who has returned from military service in Afghanistan with the Royal Army Medical Corps.", R.drawable.sherlock),
        Show(4, "Witcher", "The Witcher is a Polish-American fantasy drama television series created by Lauren Schmidt Hissrich, based on the book series of the same name by Polish writer Andrzej Sapkowski. Set on a fictional, medieval-inspired landmass known as \"the Continent\", The Witcher explores the legend of Geralt of Rivia and Princess Ciri, who are linked to each other by destiny.", R.drawable.witcher),
        Show(5, "Game of Thrones", "Game of Thrones is an American fantasy drama television series created by David Benioff and D. B. Weiss. Set on the fictional continents of Westeros and Essos, Game of Thrones has a large ensemble cast and follows several story arcs throughout the course of the show. The first major arc concerns the Iron Throne of the Seven Kingdoms of Westeros through a web of political conflicts among the noble families either vying to claim the throne or fighting for independence from whoever sits on it. ", R.drawable.game_of_thrones),
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
            //Toast.makeText(this, show.name, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ActivityShowDetails:: class.java)
            intent.putExtra("title", show.name)
            intent.putExtra("imageResource", show.imageResourceId)
            intent.putExtra("description", show.description)

            startActivity(intent)
        }

        binding.showsrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.showsrecycler.adapter = adapter
    }

    private fun addReviewBottomSheet(){

    }

}