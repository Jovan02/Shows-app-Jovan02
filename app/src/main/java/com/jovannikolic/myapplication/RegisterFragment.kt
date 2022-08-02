package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jovannikolic.myapplication.databinding.FragmentRegisterBinding
import models.RegisterRequest
import models.RegisterResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)

        ApiModule.initRetrofit(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        var isEmailValid = false
        var isPasswordValid = false
        var isRepeatPasswordValid = false

        //  Email TextWatcher
        binding.emailtext.editText?.addTextChangedListener {
            if (binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail()) {
                isEmailValid = true
                binding.emailerror.text = null
            } else {
                isEmailValid = false
                binding.emailerror.text = "Invalid Email Address"
            }
            checkRegisterButtonState(isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        //  Password TextWatcher
        binding.passwordtext.editText?.addTextChangedListener {
            if (validPassword()) {
                isPasswordValid = true
                binding.passworderror.text = null
            } else {
                isPasswordValid = false
                binding.passworderror.text = "Password must be at least 6 characters long."
            }
            checkRegisterButtonState(isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        //  Repeat Password TextWatcher
        binding.repeatPasswordText.editText?.addTextChangedListener {
            if (validRepeatPassword()) {
                isRepeatPasswordValid = true
                binding.passworderror.text = null
            } else {
                isRepeatPasswordValid = false
                binding.passworderror.text = "Passwords do not match."
            }
            checkRegisterButtonState(isEmailValid, isPasswordValid, isRepeatPasswordValid)
        }

        binding.registerButton.setOnClickListener {
            viewModel.sendDataToApi(
                requireContext(),
                this,
                binding.emailtext.editText?.text.toString(),
                binding.passwordtext.editText?.text.toString(),
                binding.repeatPasswordText.editText?.text.toString()
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validEmail(): Boolean {
        val emailText = binding.emailtext.editText?.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return false
        }
        return true
    }

    private fun validPassword(): Boolean {
        val passText = binding.passwordtext.editText?.text.toString()

        if (passText.length < 6) {
            return false
        }
        return true
    }

    private fun validRepeatPassword(): Boolean =
        binding.passwordtext.editText?.text.toString() == binding.repeatPasswordText.editText?.text.toString()

    private fun checkRegisterButtonState(email: Boolean, password: Boolean, repeatPassword: Boolean) {
        binding.registerButton.isEnabled = email && password && repeatPassword
        if (email && password && repeatPassword) {
            binding.registerButton.setTextColor(Color.parseColor("#52369C"))
        }
    }

}