package com.jovannikolic.myapplication

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding
import models.Review
import java.text.DecimalFormat
import models.AddReviewRequest
import models.AddReviewResponse
import models.GetReviewsResponse
import models.ShowDetailsResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val viewModel by viewModels<ShowDetailsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapter: ReviewsAdapter

    private lateinit var show_id: String

    var firstInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbarTitle)

        viewModel.averageReviewsLiveData.observe(viewLifecycleOwner){ average ->
            viewModel.numberOfReviewsLiveData.observe(viewLifecycleOwner){ number ->
                binding.averageratingtext.text = getString(R.string.reviews_average, number.toString(), average.toString())
            }
                binding.averageratingbar.rating = average
        }

        getShowData()
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.reviewbutton.setOnClickListener {
            showBottomSheet()
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
                    binding.averageratingtext.isVisible = true
                    binding.averageratingbar.isVisible = true
                    binding.reviewsrecycler.isVisible = true
                    binding.noReviews.isVisible = false
                    firstInit = false
                }

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

    private fun getShowData() {
        ApiModule.retrofit.getShowDetails(sharedPreferences.getString("show-id", "1")!!)
            .enqueue(object: Callback<ShowDetailsResponse>{
                override fun onResponse(call: Call<ShowDetailsResponse>, response: Response<ShowDetailsResponse>) {
                    if(response.isSuccessful){
                        Toast.makeText(requireContext(), "Call getShowData Successful.", Toast.LENGTH_SHORT).show()
                        val options = RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.family_guy)
                            .error(R.drawable.family_guy)
                        Glide.with(requireContext()).load(response.body()!!.show.image_url).apply(options).into(binding.showimg)
                        binding.collapsingToolbar.title = response.body()!!.show.title
                        binding.showtext.text = response.body()!!.show.description
                        getReviews(response.body()!!.show.id)
                    }
                    else
                        Toast.makeText(requireContext(), "Call getShowData Failed OnResponse.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Call getShowData Failed OnFailure.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getReviews(show_id: String){
        ApiModule.retrofit.getReviews(show_id)
            .enqueue(object: Callback<GetReviewsResponse>{
                override fun onResponse(call: Call<GetReviewsResponse>, response: Response<GetReviewsResponse>) {
                    if(response.isSuccessful) {
                        Toast.makeText(requireContext(), "Call getReviews Successful.", Toast.LENGTH_SHORT).show()
                        viewModel.setReviewList(response.body()!!.reviews)
                        initReviewsRecycler()
                    }else
                        Toast.makeText(requireContext(), "Call getReviews Failed OnResponse.", Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(call: Call<GetReviewsResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Call getReviews Failed OnFailure.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun addReview(rating: Int, comment: String, show_id: Int){
        val addReviewRequest = AddReviewRequest(rating, comment, show_id)
        ApiModule.retrofit.addReview(addReviewRequest)
            .enqueue(object: Callback<AddReviewResponse>{
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    if(response.isSuccessful) {
                        Toast.makeText(requireContext(), "Call addReview Successful.", Toast.LENGTH_SHORT).show()
                    }else
                        Toast.makeText(requireContext(), "Call getReview Failed OnResponse.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Call addReview Failed OnFailure.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initReviewsRecycler() {

        viewModel.reviewListLiveData.observe(viewLifecycleOwner){ reviewsList ->
            adapter = ReviewsAdapter(requireContext(), reviewsList) {}
        }

        binding.averageratingtext.isVisible = true
        binding.averageratingbar.isVisible = true
        binding.reviewsrecycler.isVisible = true
        binding.noReviews.isVisible = false

        binding.reviewsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.reviewsrecycler.adapter = adapter

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

    }

}