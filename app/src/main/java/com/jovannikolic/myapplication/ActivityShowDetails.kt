package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.ActivityShowDetailsBinding
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import models.Review
import java.text.DecimalFormat

class ActivityShowDetails : AppCompatActivity() {

    lateinit var binding: ActivityShowDetailsBinding

    lateinit var adapter : ReviewsAdapter

    private val reviews = emptyList<Review>()

    var sumOfReviews = 0.0

    var firstInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar

        val colorDrawable = ColorDrawable(getColor(R.color.white))

        actionBar?.setBackgroundDrawable(colorDrawable)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = intent.extras?.get("title").toString()

        getData()

        clickReviewButton()

    }

    @SuppressLint("SetTextI18n")
    private fun showBottomSheet(){
        val dialog = BottomSheetDialog(this)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.xbutton.setOnClickListener {
            dialog.hide()
        }

        bottomSheetBinding.submitButton.setOnClickListener{

            val rating : Float = bottomSheetBinding.ratingbar.rating
            val comment : String = bottomSheetBinding.comment.editText?.text.toString()
            val author : String = intent.extras?.getString("author").toString()

            if(rating > 0){
                if(firstInit){
                    initReviewsRecycler()
                    binding.averageratingtext.isVisible = true
                    binding.averageratingbar.isVisible = true
                    binding.reviewsrecycler.isVisible = true
                    binding.noreviews.isVisible = false
                    firstInit = false
                }
                addReviewToList(author, comment, rating)
                dialog.hide()
            }else{
                dialog.hide()
            }

            var numOfReviews = adapter.itemCount
            sumOfReviews += rating

            val df = DecimalFormat("#.##")

            var averageReviews = df.format((sumOfReviews / numOfReviews).toFloat())

            binding.averageratingtext.text = "$numOfReviews REVIEWS, $averageReviews AVERAGE"

            binding.averageratingbar.rating = averageReviews.toFloat()

        }
        dialog.show()
    }

    private fun clickReviewButton(){
        binding.reviewbutton.setOnClickListener{
            showBottomSheet()
        }
    }

    private fun getData(){
        binding.maintitle.text = intent.extras?.getString("title")
        binding.showimg.setImageResource(intent.getIntExtra("imageResource", 0))
        binding.showtext.text = intent.extras?.getString("description")
    }

    private fun initReviewsRecycler(){

        adapter = ReviewsAdapter(reviews){ review ->

        }

        binding.reviewsrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.reviewsrecycler.adapter = adapter

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

    }

    private fun addReviewToList(author: String, comment: String, ratingNum: Float){
        adapter.addReview(Review(author, comment, ratingNum))
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}