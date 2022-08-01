package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding
import models.LoginRequest
import models.LoginResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferences.getBoolean("remember", false)) {
            val navOptions: NavOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()
            Navigation.findNavController(binding.root).navigate(R.id.toShowsFragment, null, navOptions)
        }

        if (sharedPreferences.getBoolean("registered", false)) {
            binding.logintext.text = getString(R.string.registration_successful)
            binding.registerButton.visibility = View.GONE
            sharedPreferences.edit {
                putBoolean("registered", false)
            }
        } else {
            binding.logintext.text = "Login"
            binding.registerButton.visibility = View.VISIBLE
        }
        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        var isEmailValid = false
        var isPasswordValid = false

        //  Email TextWatcher
        binding.emailtext.editText?.addTextChangedListener {
            if (binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail()) {
                isEmailValid = true
                binding.emailerror.text = null
            } else {
                isEmailValid = false
                binding.emailerror.text = "Invalid Email Address"
            }
            checkLoginButtonState(isEmailValid, isPasswordValid)
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
            checkLoginButtonState(isEmailValid, isPasswordValid)
        }

        //  Login button - opens new activity
        binding.loginbutton.setOnClickListener {
            sendDataToApi(binding.emailtext.editText?.text.toString(), binding.passwordtext.editText?.text.toString())

        }

        binding.rememberMeCheck.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit {
                putBoolean("remember", isChecked)
            }
        }

        binding.registerButton.setOnClickListener {
            val direction = LoginFragmentDirections.toRegisterFragment()
            findNavController().navigate(direction)
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

    private fun checkLoginButtonState(email: Boolean, password: Boolean) {
        binding.loginbutton.isEnabled = email && password
        if (email && password) {
            binding.loginbutton.setTextColor(Color.parseColor("#52368C"))
        } else {
            binding.loginbutton.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun sendDataToApi(emailData: String, passwordData: String) {
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
                        findNavController().navigate(destination)
                    } else {
                        Toast.makeText(requireContext(), "Login Failed.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Login Failed.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}