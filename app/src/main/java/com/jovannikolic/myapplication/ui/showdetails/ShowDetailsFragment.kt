package com.jovannikolic.myapplication.ui.showdetails

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import com.jovannikolic.myapplication.ui.adapter.ReviewsAdapter
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding
import com.jovannikolic.myapplication.ui.activity.MainApplication
import models.Constants.APP
import models.Review
import models.User

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val viewModel:ShowDetailsViewModel by viewModels{
        ShowDetailsViewModelFactory((requireActivity().application as MainApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapter: ReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbarTitle)

        viewModel.getShowData(args.show.id)
        viewModel.getReviews(args.show.id)
    }

    private fun initObservers() {
        viewModel.averageReviewsLiveData.observe(viewLifecycleOwner) { average ->
            viewModel.numberOfReviewsLiveData.observe(viewLifecycleOwner) { number ->
                binding.averageratingtext.text = getString(R.string.reviews_average, number.toString(), average.toString())
            }
            binding.averageratingbar.rating = average
        }

        viewModel.isGetShowDataSuccessful.observe(viewLifecycleOwner){ isSuccessful ->
            if(!isSuccessful){
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
            }
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.family_guy)
                .error(R.drawable.family_guy)

            viewModel.currentShow.observe(viewLifecycleOwner){ show ->
                Glide.with(requireContext()).load(show.image_url).apply(options).into(binding.showimg)
                binding.collapsingToolbar.title = show.title
                binding.showtext.text = show.description
            }
        }

        viewModel.isGetReviewsSuccessful.observe(viewLifecycleOwner){ isSuccesful ->
            if(isSuccesful){
                initReviewsRecycler()
            } else {
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                viewModel.setReviewListFromDatabase().observe(viewLifecycleOwner){ reviewList ->
                    viewModel.setReviewList(reviewList.map { review ->
                        Review(review.id, review.comment, review.rating, review.show_id, User(review.user_id, review.user_email, review.user_image_url))
                    })
                }
            }
        }
    }

    fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ShowDetailsFragmentDirections.toShowsFragment())
        }

        binding.reviewbutton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.xbutton.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            val rating: Float = bottomSheetBinding.ratingbar.rating
            val comment: String = bottomSheetBinding.comment.editText?.text.toString()

            if (rating > 0) {
                viewModel.addReview(rating.toInt(), comment, args.show.id.toInt())
                dialog.dismiss()
            } else {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun initReviewsRecycler() {
        viewModel.reviewListLiveData.observe(viewLifecycleOwner) { reviewsList ->
            adapter = ReviewsAdapter(requireContext(), reviewsList) {}
            if (reviewsList.isEmpty()) {
                binding.averageratingtext.isVisible = false
                binding.averageratingbar.isVisible = false
                binding.reviewsrecycler.isVisible = false
                binding.noReviews.isVisible = true
            } else {
                binding.averageratingtext.isVisible = true
                binding.averageratingbar.isVisible = true
                binding.reviewsrecycler.isVisible = true
                binding.noReviews.isVisible = false
            }
        }

        binding.reviewsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.reviewsrecycler.adapter = adapter

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
    }

}