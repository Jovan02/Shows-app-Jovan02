package com.jovannikolic.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                System.exit(0)
            }
        })

    }

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
            val email = binding.emailtext.editText?.text.toString()
            sharedPreferences.edit{
                putString("email", email)
            }
            findNavController().navigate(R.id.toShowNav)
        }

        binding.rememberMeCheck.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit {
                putBoolean("remember", isChecked)
            }
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