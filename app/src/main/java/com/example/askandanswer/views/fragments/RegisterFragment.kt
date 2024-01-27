package com.example.askandanswer.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.FragmentRegisterBinding
import com.example.askandanswer.models.users.AuthState
import com.example.askandanswer.viewmodels.UserViewModel
import com.example.askandanswer.views.activities.MainActivity

class RegisterFragment : Fragment() {
    private lateinit var app: MyApplication
    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        app = requireActivity().application as MyApplication
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setListeners()
    }

    private fun observeLiveData() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Success -> {
                    app.stopLoading()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is AuthState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(requireContext(), binding.root, state.msg)
                }
                is AuthState.ValidationError -> {
                    app.stopLoading()
                    val err = state.err
                    if (err.name != null) {
                        binding.textNameError.text = err.name
                        binding.textNameError.visibility = View.VISIBLE
                    }
                    if (err.email != null) {
                        binding.textEmailError.text = err.email
                        binding.textEmailError.visibility = View.VISIBLE
                    }
                    if (err.password != null) {
                        binding.textPasswordError.text = err.password
                        binding.textPasswordError.visibility = View.VISIBLE
                    }
                }
                is AuthState.Loading -> {
                    app.showLoading(requireContext(), layoutInflater)
                    binding.textNameError.visibility = View.GONE
                    binding.textEmailError.visibility = View.GONE
                    binding.textPasswordError.visibility = View.GONE
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.editTextName.text.toString().trim(),
                binding.editTextEmail.text.toString().trim(),
                binding.editTextPassword.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}