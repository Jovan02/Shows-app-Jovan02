package com.jovannikolic.myapplication

import android.R
import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding
import models.Review
import java.text.DecimalFormat


class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    lateinit var adapter: ReviewsAdapter

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val reviews = emptyList<Review>()

    var sumOfReviews = 0.0
    var firstInit = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        adapter = ReviewsAdapter(reviews){ review -> }

        getData()

        clickReviewButton()

    }

    private fun initListeners() {

    }

    @SuppressLint("SetTextI18n")
    private fun showBottomSheet(){
        val dialog = context?.let { BottomSheetDialog(it) }

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog?.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.xbutton.setOnClickListener {
            dialog?.hide()
        }

        bottomSheetBinding.submitButton.setOnClickListener{

            val rating : Float = bottomSheetBinding.ratingbar.rating
            val comment : String = bottomSheetBinding.comment.editText?.text.toString()
            val author = args.username

            if(rating > 0){
                if(firstInit){
                    initReviewsRecycler()
                    binding.averageratingtext.isVisible = true
                    binding.averageratingbar.isVisible = true
                    binding.reviewsrecycler.isVisible = true
                    binding.noReviews.isVisible = false
                    firstInit = false
                }
                addReviewToList(author, comment, rating)
                dialog?.hide()
            }else{
                dialog?.hide()
            }

            var numOfReviews = adapter.itemCount
            sumOfReviews += rating

            val df = DecimalFormat("#.##")

            var averageReviews = df.format((sumOfReviews / numOfReviews).toFloat())

            binding.averageratingtext.text = "$numOfReviews REVIEWS, $averageReviews AVERAGE"

            binding.averageratingbar.rating = averageReviews.toFloat()

        }
        dialog?.show()
    }

    private fun clickReviewButton(){
        binding.reviewbutton.setOnClickListener{
            showBottomSheet()
        }
    }

    private fun getData(){
        val show = args.show
        binding.showimg.setImageResource(show.imageResourceId)
        binding.maintitle.text = show.name
        binding.showtext.text = show.description
    }

    private fun initReviewsRecycler(){

        binding.reviewsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.reviewsrecycler.adapter = adapter

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

    }

    private fun addReviewToList(author: String, comment: String, ratingNum: Float){
        adapter.addReview(Review(author, comment, ratingNum))
    }

}