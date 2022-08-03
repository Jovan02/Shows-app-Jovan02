package com.jovannikolic.myapplication.ui.login

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
import com.jovannikolic.myapplication.R
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
        initObservers()
    }

    private fun initObservers(){
        viewModel.emailHasErrorLiveData.observe(viewLifecycleOwner){ hasError ->
            binding.emailerror.text = if(hasError) "Invalid Email Address" else null
        }

        viewModel.passwordHasErrorLiveData.observe(viewLifecycleOwner) { hasError ->
            binding.passworderror.text = if (hasError) "Password must be at least 6 characters long." else null
        }

        viewModel.buttonIsEnabledLiveData.observe(viewLifecycleOwner){ isEnabled ->
            binding.loginbutton.isEnabled = isEnabled
            binding.loginbutton.setTextColor(if (isEnabled) requireContext().getColor(R.color.purple_600) else requireContext().getColor(R.color.white))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.emailtext.editText?.addTextChangedListener {
            viewModel.emailChanged(binding.emailtext.editText?.text.toString())
        }

        binding.passwordtext.editText?.addTextChangedListener {
            viewModel.passwordChanged(binding.passwordtext.editText?.text.toString())
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

}