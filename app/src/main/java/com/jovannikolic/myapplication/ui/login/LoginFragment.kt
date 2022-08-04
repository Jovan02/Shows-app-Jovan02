package com.jovannikolic.myapplication.ui.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.FragmentLoginBinding
import models.Constants.APP
import models.Constants.REMEMBER_ME

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val args by navArgs<LoginFragmentArgs>()

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(APP, Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferences.getBoolean(REMEMBER_ME, false)) {
            findNavController().navigate(LoginFragmentDirections.toShowsFragment())
        }

        viewModel.isRegistered(args.registered)

        initListeners()
        initObservers()
    }

    private fun initObservers(){
        viewModel.emailHasErrorLiveData.observe(viewLifecycleOwner){ hasError ->
            binding.emailerror.text = if(hasError) getString(R.string.invalid_email) else null
        }

        viewModel.passwordHasErrorLiveData.observe(viewLifecycleOwner) { hasError ->
            binding.passworderror.text = if (hasError) getString(R.string.short_password) else null
        }

        viewModel.buttonIsEnabledLiveData.observe(viewLifecycleOwner){ isEnabled ->
            binding.loginbutton.isEnabled = isEnabled
        }

        viewModel.isRegisteredChecked.observe(viewLifecycleOwner){ registered ->
            if (registered) {
                binding.logintext.text = getString(R.string.registration_successful)
                binding.registerButton.visibility = View.GONE
            } else {
                binding.logintext.text = getString(R.string.login)
                binding.registerButton.visibility = View.VISIBLE
            }
        }

        viewModel.isSuccessfulLogin.observe(viewLifecycleOwner){ isSuccessful ->
            if (isSuccessful == true) {
                findNavController().navigate(LoginFragmentDirections.toShowsFragment())
            } else {
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
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

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.emailtext.editText?.addTextChangedListener {
            viewModel.emailChanged(binding.emailtext.editText?.text.toString())
        }

        binding.passwordtext.editText?.addTextChangedListener {
            viewModel.passwordChanged(binding.passwordtext.editText?.text.toString())
        }

        binding.loginbutton.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            viewModel.login(sharedPreferences)
            viewModel.isRememberMeChecked.observe(viewLifecycleOwner){ checked ->
                sharedPreferences.edit{
                    putBoolean(REMEMBER_ME, checked)
                    apply()
                }
            }
        }

        binding.rememberMeCheck.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isRememberMeChecked(isChecked)
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