package com.jovannikolic.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding
import models.LoginRequest
import models.LoginResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {


    fun sendDataToApi(context: Context, fragment: Fragment, binding: FragmentLoginBinding, sharedPreferences: SharedPreferences , emailData: String, passwordData: String) {
        val loginRequest = LoginRequest(
            email = emailData,
            password = passwordData
        )
        ApiModule.retrofit.login(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val isSuccessfulLogin = response.isSuccessful

                    val email = binding.emailtext.editText?.text.toString()
                    sharedPreferences.edit {
                        putString("email", email)
                    }

                    val headers = response.headers()
                    sharedPreferences.edit {
                        putString("token-type", headers["token-type"])
                        putString("access-token", headers["access-token"])
                        putString("client", headers["client"])
                        putString("uid", headers["uid"])
                        putString("expiry", headers["expiry"])
                        putBoolean("logged", isSuccessfulLogin)
                    }.apply{}

                    if (isSuccessfulLogin) {
                        val destination = LoginFragmentDirections.toShowsFragment()
                        findNavController(fragment).navigate(destination)
                    } else {
                        Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                }
            })
    }
}