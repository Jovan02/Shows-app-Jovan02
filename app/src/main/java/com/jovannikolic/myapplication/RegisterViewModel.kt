package com.jovannikolic.myapplication

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import models.RegisterRequest
import models.RegisterResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    fun sendDataToApi(context: Context, fragment: Fragment, emailData: String, passwordData: String, repeatPasswordData: String) {
        val registerRequest = RegisterRequest(
            email = emailData,
            password = passwordData,
            passwordConfirmation = repeatPasswordData
        )
        ApiModule.retrofit.register(registerRequest)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if(response.isSuccessful){
                        val direction = RegisterFragmentDirections.toLoginFragment(true)
                        findNavController(fragment).navigate(direction)
                    } else {
                        Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}