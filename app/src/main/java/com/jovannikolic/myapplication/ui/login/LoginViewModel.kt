package com.jovannikolic.myapplication.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding
import models.LoginRequest
import models.LoginResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _emailHasError = MutableLiveData<Boolean>()
    val emailHasErrorLiveData: LiveData<Boolean> = _emailHasError

    private val _passwordHasError = MutableLiveData<Boolean>()
    val passwordHasErrorLiveData: LiveData<Boolean> = _passwordHasError

    private val _buttonIsEnabled = MutableLiveData<Boolean>()
    val buttonIsEnabledLiveData: LiveData<Boolean> = _buttonIsEnabled

    private val _rememberMeChecked = MutableLiveData<Boolean>()
    val rememberMeChecked: LiveData<Boolean> = _rememberMeChecked



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

    fun emailChanged(email: String) {
        _emailHasError.value = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        checkLoginButtonState()
    }

    fun passwordChanged(password: String) {
        _passwordHasError.value = password.length < 6
        checkLoginButtonState()
    }

    private fun checkLoginButtonState() {
        _buttonIsEnabled.value = _emailHasError.value == false && _passwordHasError.value == false
    }

}