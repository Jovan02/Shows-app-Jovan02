package com.jovannikolic.myapplication.ui.shows

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jovannikolic.myapplication.R
import java.io.File
import models.Show
import models.ShowsListResponse
import models.UpdatePhotoResponse
import models.UserDataResponse
import networking.ApiModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _isUpdatedPhoto = MutableLiveData<Boolean>()
    val isUpdatedPhoto: LiveData<Boolean> = _isUpdatedPhoto

    private val _isGetDataSuccessful = MutableLiveData<Boolean>()
    val isGetDataSuccessful: LiveData<Boolean> = _isGetDataSuccessful

    private val _isGetShowsSuccessful = MutableLiveData<Boolean>()
    val isGetShowsSuccessful: LiveData<Boolean> = _isGetShowsSuccessful

    fun setShowsList(shows: List<Show>) {
        _showsLiveData.value = shows
    }

    init {
        _showsLiveData.value = emptyList()
    }

    fun updateProfilePhoto(sharedPreferences: SharedPreferences) {
        val path = sharedPreferences.getString("image", "test")!!

        val requestBody = MultipartBody.Part
            .createFormData(
                "image", "avatar.jpg",
                File(path).asRequestBody("multipart/form-data".toMediaType())
            )

        ApiModule.retrofit.updatePhoto(sharedPreferences.getString("email", "")!!, requestBody)
            .enqueue(object : Callback<UpdatePhotoResponse> {
                override fun onResponse(call: Call<UpdatePhotoResponse>, response: Response<UpdatePhotoResponse>) {
                    _isUpdatedPhoto.value = response.isSuccessful
                }

                override fun onFailure(call: Call<UpdatePhotoResponse>, t: Throwable) {
                    _isUpdatedPhoto.value = false
                }
            })
    }

    fun getUserData() {
        ApiModule.retrofit.userData()
            .enqueue(object : retrofit2.Callback<UserDataResponse> {
                override fun onResponse(call: Call<UserDataResponse>, response: Response<UserDataResponse>) {
                    _isGetDataSuccessful.value = response.isSuccessful
                }

                override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                    _isGetDataSuccessful.value = false
                }

            })
    }

    fun getShowsList() {
        val page = 1
        val items = 20
        ApiModule.retrofit.showsList(page.toString(), items.toString())
            .enqueue(object : retrofit2.Callback<ShowsListResponse> {
                override fun onResponse(call: Call<ShowsListResponse>, response: Response<ShowsListResponse>) {
                    _isGetShowsSuccessful.value = response.isSuccessful
                    if (response.isSuccessful) {
                        setShowsList(response.body()!!.shows)
                    }
                }

                override fun onFailure(call: Call<ShowsListResponse>, t: Throwable) {
                    _isGetShowsSuccessful.value = false
                }

            })
    }
}