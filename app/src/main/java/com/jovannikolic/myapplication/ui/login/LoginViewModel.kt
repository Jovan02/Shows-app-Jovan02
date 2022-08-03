package com.jovannikolic.myapplication.ui.login

import android.content.SharedPreferences
import android.util.Patterns
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.LoginRequest
import models.LoginResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel() : ViewModel() {

    private val _emailHasError = MutableLiveData<Boolean>()
    val emailHasErrorLiveData: LiveData<Boolean> = _emailHasError

    private val _passwordHasError = MutableLiveData<Boolean>()
    val passwordHasErrorLiveData: LiveData<Boolean> = _passwordHasError

    private val _buttonIsEnabled = MutableLiveData<Boolean>()
    val buttonIsEnabledLiveData: LiveData<Boolean> = _buttonIsEnabled

    private val _isRegisteredChecked = MutableLiveData<Boolean>()
    val isRegisteredChecked: LiveData<Boolean> = _isRegisteredChecked

    private val _isSuccessfulLogin = MutableLiveData<Boolean>()
    val isSuccessfulLogin: LiveData<Boolean> = _isSuccessfulLogin

    private val _isRememberMeChecked = MutableLiveData<Boolean>()
    val isRememberMeChecked: LiveData<Boolean> = _isRememberMeChecked

    private var email: String = ""
    private var password: String = ""

    fun login(sharedPreferences: SharedPreferences) {
        val loginRequest = LoginRequest(
            email = this.email,
            password = this.password
        )
        ApiModule.retrofit.login(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if(response.isSuccessful){
                        val headers = response.headers()
                        sharedPreferences.edit {
                            putString("token-type", headers["token-type"])
                            putString("access-token", headers["access-token"])
                            putString("client", headers["client"])
                            putString("uid", headers["uid"])
                            putString("expiry", headers["expiry"])
                            putBoolean("logged", response.isSuccessful)
                            putString("email", email)
                            apply()
                        }
                        _isSuccessfulLogin.value = true
                    } else {
                        _isSuccessfulLogin.value = false
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isSuccessfulLogin.value = false
                }
            })
    }

    fun emailChanged(email: String) {
        this.email = email
        _emailHasError.value = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        checkLoginButtonState()
    }

    fun passwordChanged(password: String) {
        this.password = password
        _passwordHasError.value = password.length < 6
        checkLoginButtonState()
    }

    private fun checkLoginButtonState() {
        _buttonIsEnabled.value = _emailHasError.value == false && _passwordHasError.value == false
    }

    fun isRegistered(registered: Boolean) {
        _isRegisteredChecked.value = registered
    }

    fun isRememberMeChecked(checked: Boolean) {
        _isRememberMeChecked.value = checked
    }

}