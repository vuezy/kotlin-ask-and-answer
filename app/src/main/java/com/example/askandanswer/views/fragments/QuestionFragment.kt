package com.example.askandanswer.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.FragmentQuestionBinding
import com.example.askandanswer.models.questions.GetQuestionsState
import com.example.askandanswer.viewmodels.QuestionViewModel
import com.example.askandanswer.views.activities.SaveQuestionActivity
import com.example.askandanswer.views.adapters.QuestionAdapter

class QuestionFragment(private val viewType: ViewType) : Fragment() {
    private lateinit var app: MyApplication
    private var _binding: FragmentQuestionBinding? = null
    private val binding: FragmentQuestionBinding get() = _binding!!
    private val viewModel: QuestionViewModel by activityViewModels()
    private lateinit var questionAdapter: QuestionAdapter

    enum class ViewType {
        Main, Account, Search
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        app = requireActivity().application as MyApplication
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionAdapter = QuestionAdapter()
        binding.recyclerViewQuestion.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewQuestion.adapter = questionAdapter

        observeLiveData()
        setListeners()

        binding.swipeRefreshLayout.isEnabled = false
        when (viewType) {
            ViewType.Main -> {
                binding.textNoQuestions.visibility = View.VISIBLE
                viewModel.getQuestions()
            }
            ViewType.Account -> {
                binding.textNoQuestionsAsked.visibility = View.VISIBLE
                binding.btnSecondaryAddQuestion.visibility = View.VISIBLE
                viewModel.getQuestionsByUserId()
            }
            ViewType.Search -> {
                binding.textNoQuestions.visibility = View.VISIBLE
            }
        }
    }

    private fun observeLiveData() {
        viewModel.getQuestionsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetQuestionsState.Success -> {
                    stopLoading()
                    manageHintMsg(state.questions.isEmpty())
                    if (viewType == ViewType.Account  && state.questions.isNotEmpty()) {
                        binding.btnAddQuestion.visibility = View.VISIBLE
                    }
                    questionAdapter.setQuestions(state.questions)
                }
                is GetQuestionsState.Error -> {
                    stopLoading()
                    manageHintMsg(true)
                    app.showSnackbar(requireContext(), binding.root, state.msg)
                }
                is GetQuestionsState.AuthenticationError -> {
                    stopLoading()
                    manageHintMsg(true)
                    app.logout(requireContext(), state.msg)
                }
                is GetQuestionsState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun setListeners() {
        if (viewType != ViewType.Search) {
            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isEnabled = false
                binding.swipeRefreshLayout.isRefreshing = false
                if (viewType == ViewType.Main) {
                    viewModel.getQuestions()
                } else if (viewType == ViewType.Account) {
                    viewModel.getQuestionsByUserId()
                }
            }
        }

        if (viewType == ViewType.Account) {
            binding.btnAddQuestion.setOnClickListener {
                val intent = Intent(context, SaveQuestionActivity::class.java)
                startActivity(intent)
            }

            binding.btnSecondaryAddQuestion.setOnClickListener {
                val intent = Intent(context, SaveQuestionActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLoading() {
        binding.textNoQuestions.visibility = View.GONE
        binding.textNoQuestionsAsked.visibility = View.GONE
        binding.btnSecondaryAddQuestion.visibility = View.GONE
        binding.recyclerViewQuestion.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.GONE
        if (viewType != ViewType.Search) binding.swipeRefreshLayout.isEnabled = true
    }

    private fun manageHintMsg(visible: Boolean) {
        binding.btnAddQuestion.visibility = View.GONE
        if (visible) {
            binding.recyclerViewQuestion.visibility = View.GONE
            when (viewType) {
                ViewType.Main -> binding.textNoQuestions.visibility = View.VISIBLE
                ViewType.Account -> {
                    binding.textNoQuestionsAsked.visibility = View.VISIBLE
                    binding.btnSecondaryAddQuestion.visibility = View.VISIBLE
                }
                ViewType.Search -> binding.textNoQuestions.visibility = View.VISIBLE
            }
        } else {
            binding.recyclerViewQuestion.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}