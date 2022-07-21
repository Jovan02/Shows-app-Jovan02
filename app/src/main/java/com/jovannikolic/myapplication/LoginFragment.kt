package com.jovannikolic.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {

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