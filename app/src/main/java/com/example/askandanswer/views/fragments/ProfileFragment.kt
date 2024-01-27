package com.example.askandanswer.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.FragmentProfileBinding
import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.models.users.GetUserDataState
import com.example.askandanswer.viewmodels.UserViewModel

class ProfileFragment : Fragment() {
    private lateinit var app: MyApplication
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        app = requireActivity().application as MyApplication
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setListeners()
        viewModel.getUserData()
    }

    private fun observeLiveData() {
        viewModel.getUserDataState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetUserDataState.Success -> {
                    app.stopLoading()
                    binding.textName.text = state.user.name
                    binding.textEmail.text = state.user.email
                    viewModel.getPointsAndCredits()
                }
                is GetUserDataState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(requireContext(), binding.root, state.msg)
                }
                is GetUserDataState.AuthenticationError -> {
                    app.stopLoading()
                    app.logout(requireContext(), state.msg)
                }
                is GetUserDataState.Loading -> {
                    app.showLoading(requireContext(), layoutInflater)
                }
            }
        }

        viewModel.getPointsAndCreditsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetPointsAndCreditsState.Success -> {
                    app.stopLoading()
                    binding.textPoints.text = state.points.toString()
                    binding.textCredits.text = state.credits.toString()
                }
                is GetPointsAndCreditsState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(requireContext(), binding.root, state.msg)
                }
                is GetPointsAndCreditsState.AuthenticationError -> {
                    app.stopLoading()
                    app.logout(requireContext(), state.msg)
                }
                is GetPointsAndCreditsState.Loading -> {
                    app.showLoading(requireContext(), layoutInflater)
                }
            }
        }
    }

    private fun setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.getUserData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}