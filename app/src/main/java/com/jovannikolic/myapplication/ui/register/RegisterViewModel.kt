package com.jovannikolic.myapplication.ui.register

import android.content.Context
import android.graphics.Color
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.FragmentRegisterBinding
import models.RegisterRequest
import models.RegisterResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {





    fun initListeners(context: Context,fragment: Fragment , binding: FragmentRegisterBinding) {
        var isEmailValid = false
        var isPasswordValid = false
        var isRepeatPasswordValid = false

        //  Email TextWatcher
        binding.emailtext.editText?.addTextChangedListener {
            if (binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail(binding)) {
                isEmailValid = true
                binding.emailerror.text = null
            } else {
                isEmailValid = false
                binding.emailerror.text = fragment.getString(R.string.invalid_email)
            }
            checkRegisterButtonState(binding, isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        //  Password TextWatcher
        binding.passwordtext.editText?.addTextChangedListener {
            if (validPassword(binding)) {
                isPasswordValid = true
                binding.passworderror.text = null
            } else {
                isPasswordValid = false
                binding.passworderror.text = fragment.getString(R.string.short_password)
            }
            checkRegisterButtonState(binding, isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        //  Repeat Password TextWatcher
        binding.repeatPasswordText.editText?.addTextChangedListener {
            if (validRepeatPassword(binding)) {
                isRepeatPasswordValid = true
                binding.passworderror.text = null
            } else {
                isRepeatPasswordValid = false
                binding.passworderror.text = fragment.getString(R.string.passwords_do_not_match)
            }
            checkRegisterButtonState(binding, isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        binding.registerButton.setOnClickListener {
            sendDataToApi(
                context,
                fragment,
                binding.emailtext.editText?.text.toString(),
                binding.passwordtext.editText?.text.toString(),
                binding.repeatPasswordText.editText?.text.toString()
            )
        }
    }

    private fun validEmail(binding: FragmentRegisterBinding): Boolean {
        val emailText = binding.emailtext.editText?.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return false
        }
        return true
    }

    private fun validPassword(binding: FragmentRegisterBinding): Boolean {
        val passText = binding.passwordtext.editText?.text.toString()

        if (passText.length < 6) {
            return false
        }
        return true
    }

    private fun validRepeatPassword(binding: FragmentRegisterBinding): Boolean =
        binding.passwordtext.editText?.text.toString() == binding.repeatPasswordText.editText?.text.toString()

    private fun checkRegisterButtonState(binding: FragmentRegisterBinding, email: Boolean, password: Boolean, repeatPassword: Boolean) {
        binding.registerButton.isEnabled = email && password && repeatPassword
        if (email && password && repeatPassword) {
            binding.registerButton.setTextColor(Color.parseColor("#52369C"))
        }
    }

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
                        Toast.makeText(context, R.string.registration_failed, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(context, R.string.registration_failed, Toast.LENGTH_SHORT).show()
                }
            })
    }
}