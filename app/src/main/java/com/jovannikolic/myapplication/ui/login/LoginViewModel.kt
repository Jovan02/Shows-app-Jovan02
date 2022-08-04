package com.jovannikolic.myapplication.ui.login

import android.content.SharedPreferences
import android.util.Patterns
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.Constants.ACCESS_TOKEN
import models.Constants.CLIENT
import models.Constants.EMAIL
import models.Constants.EXPIRY
import models.Constants.LOGGED
import models.Constants.TOKEN_TYPE
import models.Constants.UID
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
                    _isLoading.value = true
                    if(response.isSuccessful){
                        val headers = response.headers()
                        sharedPreferences.edit {
                            putString(TOKEN_TYPE, headers[TOKEN_TYPE])
                            putString(ACCESS_TOKEN, headers[ACCESS_TOKEN])
                            putString(CLIENT, headers[CLIENT])
                            putString(UID, headers[UID])
                            putString(EXPIRY, headers[EXPIRY])
                            putBoolean(LOGGED, response.isSuccessful)
                            putString(EMAIL, email)
                            apply()
                        }
                        _isSuccessfulLogin.value = true
                    } else {
                        _isSuccessfulLogin.value = false
                    }
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isSuccessfulLogin.value = false
                    _isLoading.value = true
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

    fun changeRememberMe(checked: Boolean) {
        _isRememberMeChecked.value = checked
    }

}