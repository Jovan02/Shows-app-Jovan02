package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

    private val viewModel by viewModels<ShowDetailsViewModel>()

    var firstInit = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        adapter = ReviewsAdapter(reviews) { review -> }

        getData()

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)

        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbarTitle)

        clickReviewButton()
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    private fun showBottomSheet() {
        val dialog = context?.let { BottomSheetDialog(it) }

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog?.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.xbutton.setOnClickListener {
            dialog?.hide()
        }

        bottomSheetBinding.submitButton.setOnClickListener {

            val rating: Float = bottomSheetBinding.ratingbar.rating
            val comment: String = bottomSheetBinding.comment.editText?.text.toString()
            val author = args.username

            if (rating > 0) {
                if (firstInit) {
                    initReviewsRecycler()
                    binding.averageratingtext.isVisible = true
                    binding.averageratingbar.isVisible = true
                    binding.reviewsrecycler.isVisible = true
                    binding.noReviews.isVisible = false
                    firstInit = false
                }

                viewModel.createReview(author, comment, rating)

                var reviewToBeAdded : Review = Review("author", "comment", 3.0f)

                viewModel.reviewLiveData.observe(viewLifecycleOwner) { review ->
                    reviewToBeAdded = review
                }
                    addReviewToList(reviewToBeAdded)

                dialog?.hide()
            } else {
                dialog?.hide()
            }

            val numOfReviews = adapter.itemCount

            viewModel.calculateRating(numOfReviews, rating)

            viewModel.averageReviewsLiveData.observe(viewLifecycleOwner) { averageRating ->
                val df = DecimalFormat("#.##")
                val averageRatingRounded = df.format(averageRating)
                binding.averageratingtext.text = getString(R.string.reviews_average, numOfReviews.toString(), averageRatingRounded.toString())
                binding.averageratingbar.rating = averageRatingRounded.toFloat()
            }
        }
        dialog?.show()
    }

    private fun clickReviewButton() {
        binding.reviewbutton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun getData() {
        val show = args.show
        binding.showimg.setImageResource(show.imageResourceId)
        binding.collapsingToolbar.title = show.name
        binding.showtext.text = show.description
    }

    private fun initReviewsRecycler() {

        binding.reviewsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.reviewsrecycler.adapter = adapter

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

    }

    private fun addReviewToList(review: Review) {
        adapter.addReview(review)
    }

}