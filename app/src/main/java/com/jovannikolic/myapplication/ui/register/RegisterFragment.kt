package com.jovannikolic.myapplication.ui.register

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.FragmentRegisterBinding
import models.Constants.APP
import networking.ApiModule

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(APP, Context.MODE_PRIVATE)

        ApiModule.initRetrofit(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers(){
        viewModel.emailHasErrorLiveData.observe(viewLifecycleOwner){ hasError ->
            binding.emailerror.text = if(hasError) "Invalid Email Address" else null
        }

        viewModel.passwordHasErrorLiveData.observe(viewLifecycleOwner){ hasError ->
            binding.passworderror.text = if (hasError) "Password must be at least 6 characters long." else null
        }

        viewModel.passwordRepeatHasErrorLiveData.observe(viewLifecycleOwner){ hasError ->
            binding.passworderror.text = if (hasError) "Passwords don't match." else null
        }

        viewModel.buttonIsEnabledLiveData.observe(viewLifecycleOwner){ isEnabled ->
            binding.registerButton.isEnabled = isEnabled
            binding.registerButton.setTextColor(if (isEnabled) requireContext().getColor(R.color.purple_600) else requireContext().getColor(R.color.white))
        }

        viewModel.isRegistered.observe(viewLifecycleOwner){ isRegistered ->
            if(isRegistered){
                findNavController().navigate(RegisterFragmentDirections.toLoginFragment(true))
            } else if(isRegistered){
                Toast.makeText(context, R.string.registration_failed, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
            }
        }

    }

    private fun initListeners() {

        binding.emailtext.editText?.addTextChangedListener {
            viewModel.emailChanged(binding.emailtext.editText?.text.toString())
        }

        binding.passwordtext.editText?.addTextChangedListener {
            viewModel.passwordChanged(binding.passwordtext.editText?.text.toString())
        }

        binding.repeatPasswordText.editText?.addTextChangedListener {
            viewModel.passwordRepeatChanged(binding.repeatPasswordText.editText?.text.toString())
        }

        binding.registerButton.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            viewModel.register()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}