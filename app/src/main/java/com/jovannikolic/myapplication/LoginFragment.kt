package com.jovannikolic.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<ShowsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        var isEmailValid = false
        var isPasswordValid = false

        //  Email TextWatcher
        binding.emailtext.editText?.addTextChangedListener {
            if(binding.emailtext.editText?.text.toString().isNotEmpty() && validEmail()){
                isEmailValid = true
                binding.emailerror.text = null
            }else{
                isEmailValid = false
                binding.emailerror.text = "Invalid Email Address"
            }
            checkLoginButtonState(isEmailValid, isPasswordValid)
        }


        //  Password TextWatcher
        binding.passwordtext.editText?.addTextChangedListener {
            if(validPassword()){
                isPasswordValid = true
                binding.passworderror.text = null
            }else{
                isPasswordValid = false
                binding.passworderror.text = "Password must be at least 6 characters long."
            }
            checkLoginButtonState(isEmailValid, isPasswordValid)
        }

        //  Login button - opens new activity
        binding.loginbutton.setOnClickListener{
            val direction = LoginFragmentDirections.toShowFragment(binding.emailtext.editText?.text.toString())
            findNavController().navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validEmail(): Boolean {

        val emailText = binding.emailtext.editText?.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return false
        }
        return true
    }

    private fun validPassword(): Boolean {

        val passText = binding.passwordtext.editText?.text.toString()

        if(passText.length < 6) {
            return false
        }

        return true
    }

    private fun checkLoginButtonState(email : Boolean, password : Boolean){
        binding.loginbutton.isEnabled = email && password
        if(email && password) {
            binding.loginbutton.setTextColor(Color.parseColor("#52368C"))
        }else{
            binding.loginbutton.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

}