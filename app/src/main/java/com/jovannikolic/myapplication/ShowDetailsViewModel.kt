package com.jovannikolic.myapplication

import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import models.Review

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

}