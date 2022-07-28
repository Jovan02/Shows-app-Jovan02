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

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val viewModel by viewModels<ShowDetailsViewModel>()

    var firstInit = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

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

                viewModel.addReviewToList(Review(author, comment, rating))

                dialog?.hide()
            } else {
                dialog?.hide()
            }

            var numOfReviews = 0
            viewModel.adapterLiveData.observe(viewLifecycleOwner){ adapter ->
                numOfReviews = adapter.itemCount
            }

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

        viewModel.adapterLiveData.observe(viewLifecycleOwner){ adapter ->
            binding.reviewsrecycler.adapter = adapter
        }

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

    }

}