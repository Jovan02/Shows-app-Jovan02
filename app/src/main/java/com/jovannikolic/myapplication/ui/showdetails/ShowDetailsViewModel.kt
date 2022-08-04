package com.jovannikolic.myapplication.ui.showdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import database.ShowsDatabase
import java.util.concurrent.Executors
import models.AddReviewRequest
import models.AddReviewResponse
import models.GetReviewsResponse
import models.Review
import models.ReviewEntity
import models.Show
import models.ShowDetailsResponse
import models.User
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel(
    private val database : ShowsDatabase
) : ViewModel() {

    private val _sumOfReviewsLiveData = MutableLiveData<Float>()
    val sumOfReviewsLiveData: LiveData<Float> = _sumOfReviewsLiveData

    private val _averageReviewsLiveData = MutableLiveData<Float>()
    val averageReviewsLiveData: LiveData<Float> = _averageReviewsLiveData

    private val _reviewLiveData = MutableLiveData<Review>()
    val reviewLiveData: LiveData<Review> = _reviewLiveData

    private val _reviewListLiveData = MutableLiveData<List<Review>>()
    val reviewListLiveData: LiveData<List<Review>> = _reviewListLiveData

    private val _numberOfReviewsLiveData = MutableLiveData<Int>()
    val numberOfReviewsLiveData: LiveData<Int> = _numberOfReviewsLiveData

    private val _isGetShowDataSuccessful = MutableLiveData<Boolean>()
    val isGetShowDataSuccessful: LiveData<Boolean> = _isGetShowDataSuccessful

    private val _showImageUrl = MutableLiveData<String>()
    val showImageUrl: LiveData<String> = _showImageUrl

    private val _showTitle = MutableLiveData<String>()
    val showTitle: LiveData<String> = _showTitle

    private val _showDescription = MutableLiveData<String>()
    val showDescription: LiveData<String> = _showDescription

    private val _isGetReviewsSuccessful = MutableLiveData<Boolean>()
    val isGetReviewsSuccessful: LiveData<Boolean> = _isGetReviewsSuccessful

    private val _isAddReviewSuccessful = MutableLiveData<Boolean>()
    val isAddReviewSuccessful: LiveData<Boolean> = _isAddReviewSuccessful

    private var _currentShow = MutableLiveData<Show>()
    val currentShow: LiveData<Show> = _currentShow

    private val _showId = MutableLiveData<String>()
    val showId: LiveData<String> = _showId


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
        _reviewListLiveData.value?.plus(review)
    }

    fun setShow(): LiveData<Show> {
        return database.showDao().getShow(_showId.value!!)
    }

    fun setReviewList(list: List<Review>) {
        _reviewListLiveData.value = list
    }

    fun setReviewListFromDatabase() : LiveData<List<ReviewEntity>>{
        return database.reviewDao().getAllShowReviews(_showId.value!!)
    }

    fun setAverageReviewsLiveData(avg: Float) {
        _averageReviewsLiveData.value = avg
    }

    fun setNumberOfReviewsLiveData(num: Int) {
        _numberOfReviewsLiveData.value = num
    }

    fun getShowData(show_id: String) {
        ApiModule.retrofit.getShowDetails(show_id)
            .enqueue(object : Callback<ShowDetailsResponse> {
                override fun onResponse(call: Call<ShowDetailsResponse>, response: Response<ShowDetailsResponse>) {
                    _isGetShowDataSuccessful.value = response.isSuccessful
                    if (response.isSuccessful) {
                        _showImageUrl.value = response.body()!!.show.image_url
                        _showDescription.value = response.body()!!.show.description
                        _showTitle.value = response.body()!!.show.title
                        getReviews(show_id)
                        setAverageReviewsLiveData(response.body()!!.show.average_rating)
                        setNumberOfReviewsLiveData(response.body()!!.show.no_of_reviews)

                    }
                    _showId.value = show_id
                }

                override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                    _isGetShowDataSuccessful.value = false
                    val show = database.showDao().getShow(show_id).value
                    _showImageUrl.value = show?.image_url
                    _showDescription.value = show?.description
                    _showTitle.value = show?.title
                    _showId.value = show_id
                }

            })
    }

    fun getReviews(show_id: String) {
        ApiModule.retrofit.getReviews(show_id)
            .enqueue(object : Callback<GetReviewsResponse> {
                override fun onResponse(call: Call<GetReviewsResponse>, response: Response<GetReviewsResponse>) {
                    _isGetReviewsSuccessful.value = response.isSuccessful
                    if (response.isSuccessful) {
                        setReviewList(response.body()!!.reviews)
                        Executors.newSingleThreadExecutor().execute{
                            database.reviewDao().insertAllReviews(response.body()!!.reviews.map { review ->
                                ReviewEntity(review.id, review.comment, review.rating, review.show_id, review.user.id, review.user.email, review.user.imageUrl)
                            })
                        }
                    }
                }
                override fun onFailure(call: Call<GetReviewsResponse>, t: Throwable) {
                    _isGetReviewsSuccessful.value = false
                }

            })
    }

    fun addReview(rating: Int, comment: String, show_id: Int) {
        val addReviewRequest = AddReviewRequest(rating, comment, show_id)
        ApiModule.retrofit.addReview(addReviewRequest)
            .enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    _isAddReviewSuccessful.value = response.isSuccessful
                    if (response.isSuccessful) {
                        getShowData(show_id.toString())
                        getReviews(show_id.toString())
                    }
                }

                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    _isAddReviewSuccessful.value = false
                }
            })
    }
}