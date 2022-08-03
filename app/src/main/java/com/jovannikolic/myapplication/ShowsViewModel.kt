package com.jovannikolic.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import database.ShowsDatabase
import java.io.File
import java.util.concurrent.Executors
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

class ShowsViewModel(
    private val database: ShowsDatabase
) : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    fun setShowsList(shows: List<Show>) {
        _showsLiveData.value = shows
    }

    fun setShowsListFromDatabase() : LiveData<List<Show>>{
        return database.showDao().getAllShows()
    }

    init {
        _showsLiveData.value = emptyList()
    }

    fun updateProfilePhoto(context: Context, sharedPreferences: SharedPreferences) {
        val path = sharedPreferences.getString("image", "test")!!

        val requestBody = MultipartBody.Part
            .createFormData("image", "avatar.jpg",
                File(path).asRequestBody("multipart/form-data".toMediaType()))

        ApiModule.retrofit.updatePhoto(sharedPreferences.getString("email", "")!!, requestBody)
            .enqueue(object : Callback<UpdatePhotoResponse> {
                override fun onResponse(call: Call<UpdatePhotoResponse>, response: Response<UpdatePhotoResponse>) {
                    if (response.isSuccessful)
                        Toast.makeText(context, "Call Successful.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<UpdatePhotoResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }

            })

    }

    fun getUserData(context: Context) {
        ApiModule.retrofit.userData()
            .enqueue(object : retrofit2.Callback<UserDataResponse> {
                override fun onResponse(call: Call<UserDataResponse>, response: Response<UserDataResponse>) {
                    if (response.isSuccessful)
                        Toast.makeText(context, "Call Successful.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context, "Call Failed OnResponse.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

            })
    }

    fun getShowsList(context: Context, viewLifecycleOwner: LifecycleOwner,page: Int, items: Int) {
        ApiModule.retrofit.showsList(page.toString(), items.toString())
            .enqueue(object : retrofit2.Callback<ShowsListResponse> {
                override fun onResponse(call: Call<ShowsListResponse>, response: Response<ShowsListResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(context, "Call Successful.", Toast.LENGTH_SHORT).show()
                        Executors.newSingleThreadExecutor().execute{
                            database.showDao().insertAllShows(response.body()!!.shows)
                        }
                        setShowsList(response.body()!!.shows)
                    } else {
                        Toast.makeText(context, "Call Failed OnResponse.", Toast.LENGTH_SHORT).show()
                        setShowsListFromDatabase().observe(viewLifecycleOwner) { showList ->
                            setShowsList(showList.map { show ->
                                Show(show.id, show.average_rating, show.description, show.image_url, show.no_of_reviews, show.title)
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<ShowsListResponse>, t: Throwable) {
                    setShowsListFromDatabase().observe(viewLifecycleOwner) { showList ->
                        setShowsList(showList.map { show ->
                            Show(show.id, show.average_rating, show.description, show.image_url, show.no_of_reviews, show.title)
                        })
                    }
                }

            })
    }
}