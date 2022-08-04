package com.jovannikolic.myapplication.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.RegisterRequest
import models.RegisterResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _emailHasError = MutableLiveData<Boolean>()
    val emailHasErrorLiveData: LiveData<Boolean> = _emailHasError

    private val _passwordHasError = MutableLiveData<Boolean>()
    val passwordHasErrorLiveData: LiveData<Boolean> = _passwordHasError

    private val _passwordRepeatHasError = MutableLiveData<Boolean>()
    val passwordRepeatHasErrorLiveData: LiveData<Boolean> = _passwordRepeatHasError

    private val _buttonIsEnabled = MutableLiveData<Boolean>()
    val buttonIsEnabledLiveData: LiveData<Boolean> = _buttonIsEnabled

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var email: String = ""
    private var password: String = ""
    private var passwordRepeat: String = ""

    fun emailChanged(email: String) {
        this.email = email
        _emailHasError.value = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        checkRegisterButtonState()
    }

    fun passwordChanged(password: String) {
        this.password = password
        _passwordHasError.value = password.length < 6
        checkRegisterButtonState()
    }

    fun passwordRepeatChanged(repeatPassword: String) {
        this.passwordRepeat = repeatPassword
        _passwordRepeatHasError.value = repeatPassword != this.password
        checkRegisterButtonState()
    }

    private fun checkRegisterButtonState() {
        _buttonIsEnabled.value = _emailHasError.value == false && _passwordHasError.value == false && _passwordRepeatHasError.value == false
    }

    fun register() {
        val registerRequest = RegisterRequest(
            email = this.email,
            password = this.password,
            passwordConfirmation = this.passwordRepeat
        )
        ApiModule.retrofit.register(registerRequest)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    _isLoading.value = true
                    _isRegistered.value = response.isSuccessful
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _isRegistered.value = false
                    _isLoading.value = false
                }
            })
    }
}