package com.jovannikolic.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding
import java.text.DecimalFormat
import models.AddReviewRequest
import models.AddReviewResponse
import models.GetReviewsResponse
import models.Review
import models.ShowDetailsResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val _sumOfReviewsLiveData = MutableLiveData<Float>()
    val sumOfReviewsLiveData: LiveData<Float> = _sumOfReviewsLiveData

    private val _averageReviewsLiveData = MutableLiveData<Float>()
    val averageReviewsLiveData: LiveData<Float> = _averageReviewsLiveData

    private val _reviewLiveData = MutableLiveData<Review>()
    val reviewLiveData: LiveData<Review> = _reviewLiveData

    private val _reviewListLiveData = MutableLiveData<List<Review>>()
    val reviewListLiveData: LiveData<List<Review>> = _reviewListLiveData

    private val _adapterLiveData = MutableLiveData<ReviewsAdapter>()
    val adapterLiveData: LiveData<ReviewsAdapter> = _adapterLiveData

    private val _numberOfReviewsLiveData = MutableLiveData<Int>()
    val numberOfReviewsLiveData: LiveData<Int> = _numberOfReviewsLiveData

    private lateinit var showId: String

    init {
        _sumOfReviewsLiveData.value = 0.0f
        _averageReviewsLiveData.value = 0.0f
        _sumOfReviewsLiveData.value = 0.0f
        _reviewListLiveData.value = emptyList()
        _numberOfReviewsLiveData.value = 0
    }

    fun calculateRating(numOfReviews: Int, rating: Float) {
        _sumOfReviewsLiveData.value = _sumOfReviewsLiveData.value?.plus(rating)
        _averageReviewsLiveData.value = _sumOfReviewsLiveData.value?.div(numOfReviews)
    }

    fun addReviewToList(review: Review) {
        _adapterLiveData.value?.addReview(review)
        _reviewListLiveData.value?.plus(review)
    }

    fun setReviewList(list: List<Review>) {
        _reviewListLiveData.value = list
    }

    fun setAverageReviewsLiveData(avg: Float) {
        _averageReviewsLiveData.value = avg
    }

    fun setNumberOfReviewsLiveData(num: Int) {
        _numberOfReviewsLiveData.value = num
    }

    fun getShowData(context: Context, viewLifecycleOwner: LifecycleOwner, fragment: Fragment, binding: FragmentShowDetailsBinding, show_id: String) {
        ApiModule.retrofit.getShowDetails(show_id)
            .enqueue(object : Callback<ShowDetailsResponse> {
                override fun onResponse(call: Call<ShowDetailsResponse>, response: Response<ShowDetailsResponse>) {
                    if (response.isSuccessful) {
                        val options = RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.family_guy)
                            .error(R.drawable.family_guy)
                        Glide.with(context).load(response.body()!!.show.image_url).apply(options).into(binding.showimg)
                        binding.collapsingToolbar.title = response.body()!!.show.title
                        binding.showtext.text = response.body()!!.show.description

                        getReviews(context, viewLifecycleOwner, binding, response.body()!!.show.id)
                        showId = response.body()!!.show.id

                        setAverageReviewsLiveData(response.body()!!.show.average_rating)
                        setNumberOfReviewsLiveData(response.body()!!.show.no_of_reviews)
                    } else
                        Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun getReviews(context: Context, viewLifecycleOwner: LifecycleOwner, binding: FragmentShowDetailsBinding, show_id: String) {
        ApiModule.retrofit.getReviews(show_id)
            .enqueue(object : Callback<GetReviewsResponse> {
                override fun onResponse(call: Call<GetReviewsResponse>, response: Response<GetReviewsResponse>) {
                    if (response.isSuccessful) {
                        setReviewList(response.body()!!.reviews)
                        initReviewsRecycler(context, binding, viewLifecycleOwner)
                    } else
                        Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<GetReviewsResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun addReview(context: Context, viewLifecycleOwner: LifecycleOwner, binding: FragmentShowDetailsBinding, rating: Int, comment: String, show_id: Int) {
        val addReviewRequest = AddReviewRequest(rating, comment, show_id)
        ApiModule.retrofit.addReview(addReviewRequest)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    if (response.isSuccessful) {
                        getReviews(context, viewLifecycleOwner, binding, show_id.toString())
                    } else
                        Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun initReviewsRecycler(context: Context, binding: FragmentShowDetailsBinding, viewLifecycleOwner: LifecycleOwner) {
        reviewListLiveData.observe(viewLifecycleOwner) { reviewsList ->
            _adapterLiveData.value = ReviewsAdapter(context, reviewsList) {}
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

        binding.reviewsrecycler.adapter = _adapterLiveData.value

        binding.reviewsrecycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
    }

    private fun showBottomSheet(context: Context, viewLifecycleOwner: LifecycleOwner, binding: FragmentShowDetailsBinding, layoutInflater: LayoutInflater) {
        val dialog = context.let { BottomSheetDialog(it) }

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.xbutton.setOnClickListener {
            dialog.hide()
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            val rating: Float = bottomSheetBinding.ratingbar.rating
            val comment: String = bottomSheetBinding.comment.editText?.text.toString()

            if (rating > 0) {
                addReview(context, viewLifecycleOwner, binding, rating.toInt(), comment, showId.toInt())
                dialog.hide()
            } else {
                dialog.hide()
            }
        }
        dialog.show()
    }

    fun initListeners(context: Context, viewLifecycleOwner: LifecycleOwner, binding: FragmentShowDetailsBinding, fragment: Fragment, layoutInflater: LayoutInflater) {
        binding.toolbar.setNavigationOnClickListener {
            findNavController(fragment).popBackStack()
        }

        binding.reviewbutton.setOnClickListener {
            showBottomSheet(context, viewLifecycleOwner, binding, layoutInflater)
        }
    }
}