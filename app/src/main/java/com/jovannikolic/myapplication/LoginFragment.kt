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
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val args by navArgs<LoginFragmentArgs>()

    private val viewModel by viewModels<LoginViewModel>()

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
            val direction = LoginFragmentDirections.toShowsFragment()
            Navigation.findNavController(binding.root).navigate(direction)
        }

        if (args.registered) {
            binding.logintext.text = getString(R.string.registration_successful)
            binding.registerButton.visibility = View.GONE
        } else {
            binding.logintext.text = getString(R.string.login)
            binding.registerButton.visibility = View.VISIBLE
        }
        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        var isEmailValid = false
        var isPasswordValid = false

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

        binding.loginbutton.setOnClickListener {
            viewModel.sendDataToApi(requireContext(), this, binding, sharedPreferences, binding.emailtext.editText?.text.toString(), binding.passwordtext.editText?.text.toString())

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

}