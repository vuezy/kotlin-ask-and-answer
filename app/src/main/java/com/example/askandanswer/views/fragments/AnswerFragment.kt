package com.example.askandanswer.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askandanswer.MyApplication
import com.example.askandanswer.R
import com.example.askandanswer.databinding.FragmentAnswerBinding
import com.example.askandanswer.interfaces.AnswerItemUtils
import com.example.askandanswer.interfaces.SaveAnswerListener
import com.example.askandanswer.models.answers.CreateAnswerState
import com.example.askandanswer.models.answers.DeleteAnswerState
import com.example.askandanswer.models.answers.GetAnswersState
import com.example.askandanswer.models.answers.UpdateAnswerState
import com.example.askandanswer.models.answers.VoteAnswerState
import com.example.askandanswer.viewmodels.AnswerViewModel
import com.example.askandanswer.views.adapters.AnswerAdapter

class AnswerFragment(
    private val viewType: ViewType,
    private val questionId: Int? = null,
    private val userId: Int? = null,
    private var answerId: Int? = null,
    private val collapseAppBarLayout: (() -> Unit)? = null
) : Fragment() {
    private lateinit var app: MyApplication
    private var _binding: FragmentAnswerBinding? = null
    private val binding: FragmentAnswerBinding get() = _binding!!
    private val viewModel: AnswerViewModel by activityViewModels()
    private lateinit var answerAdapter: AnswerAdapter
    private var saveAnswerListener: SaveAnswerListener? = null
    private var focusPosition: Int = -1
    private var modifiedAnswerId: Int = 0
    private var updatedAnswerBody: String = ""

    enum class ViewType {
        Question, Account
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        app = requireActivity().application as MyApplication
        _binding = FragmentAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
        setListeners()
        configureAnswerAdapter(userId)

        binding.swipeRefreshLayout.isEnabled = false
        if (viewType == ViewType.Question) {
            changeViewPosition(binding.progressBar)
            changeViewPosition(binding.textNoAnswers)
            binding.textLabelAnswers.visibility = View.VISIBLE
            binding.btnAddAnswer.visibility = View.VISIBLE
            viewModel.getAnswersByQuestionId(questionId!!)
        } else {
            viewModel.getAnswersByUserId()
        }
    }

    private fun observeLiveData() {
        viewModel.getAnswersState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetAnswersState.Success -> {
                    stopLoading()
                    manageHintMsg(state.answers.isEmpty())
                    answerAdapter.setAnswers(state.answers)
                }
                is GetAnswersState.Error -> {
                    stopLoading()
                    manageHintMsg(true)
                    app.showSnackbar(requireContext(), binding.root, state.msg)
                }
                is GetAnswersState.AuthenticationError -> {
                    stopLoading()
                    manageHintMsg(true)
                    app.logout(requireContext(), state.msg)
                }
                is GetAnswersState.Loading -> {
                    showLoading()
                }
            }
        }

        if (viewType == ViewType.Question) {
            viewModel.createAnswerState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is CreateAnswerState.Success -> {
                        app.stopLoading()
                        hideAddAnswerCard()
                        focusPosition = answerAdapter.itemCount + 1
                        viewModel.getAnswersByQuestionId(questionId!!)
                        app.showSnackbar(requireContext(), binding.root, state.msg, true)
                    }
                    is CreateAnswerState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(requireContext(), binding.root, state.msg)
                    }
                    is CreateAnswerState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(requireContext(), state.msg)
                    }
                    is CreateAnswerState.ValidationError -> {
                        app.stopLoading()
                        val err = state.err
                        if (err.body != null) {
                            binding.textBodyError.text = err.body
                            binding.textBodyError.visibility = View.VISIBLE
                        }
                        if (err.questionId != null) {
                            app.showSnackbar(requireContext(), binding.root, err.questionId)
                        }
                    }
                    is CreateAnswerState.Loading -> {
                        app.showLoading(requireContext(), layoutInflater)
                        binding.textBodyError.visibility = View.GONE
                    }
                }
            }

            viewModel.updateAnswerState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UpdateAnswerState.Success -> {
                        app.stopLoading()
                        saveAnswerListener!!.onSuccess()
                        answerAdapter.updateAnswer(modifiedAnswerId, updatedAnswerBody)
                        app.showSnackbar(requireContext(), binding.root, state.msg, true)
                    }
                    is UpdateAnswerState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(requireContext(), binding.root, state.msg)
                    }
                    is UpdateAnswerState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(requireContext(), state.msg)
                    }
                    is UpdateAnswerState.ValidationError -> {
                        app.stopLoading()
                        val err = state.err
                        if (err.body != null) {
                            saveAnswerListener!!.onError(err.body)
                        }
                        if (err.questionId != null) {
                            app.showSnackbar(requireContext(), binding.root, err.questionId)
                        }
                    }
                    is UpdateAnswerState.Loading -> {
                        app.showLoading(requireContext(), layoutInflater)
                    }
                }
            }

            viewModel.deleteAnswerState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is DeleteAnswerState.Success -> {
                        app.stopLoading()
                        answerAdapter.deleteAnswer(modifiedAnswerId)
                        app.showSnackbar(requireContext(), binding.root, state.msg, true)
                    }
                    is DeleteAnswerState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(requireContext(), binding.root, state.msg)
                    }
                    is DeleteAnswerState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(requireContext(), state.msg)
                    }
                    is DeleteAnswerState.Loading -> {
                        app.showLoading(requireContext(), layoutInflater)
                    }
                }
            }

            viewModel.voteAnswerState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is VoteAnswerState.Success -> {
                        app.stopLoading()
                        answerAdapter.voteAnswer(modifiedAnswerId, state.votes)
                        app.showSnackbar(requireContext(), binding.root, state.msg, true)
                    }
                    is VoteAnswerState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(requireContext(), binding.root, state.msg)
                    }
                    is VoteAnswerState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(requireContext(), state.msg)
                    }
                    is VoteAnswerState.Loading -> {
                        app.showLoading(requireContext(), layoutInflater)
                    }
                }
            }
        }
    }

    private fun configureAnswerAdapter(userId: Int? = null) {
        if (viewType == ViewType.Question) {
            answerAdapter = AnswerAdapter(viewType, object : AnswerItemUtils {
                override val userId: Int = userId!!

                override fun startEdit(id: Int) {
                    hideAddAnswerCard()
                    answerAdapter.setActiveEditAnswerPosition(id)
                }

                override fun cancelEdit() {
                    answerAdapter.setActiveEditAnswerPosition(-1)
                }

                override fun save(
                    id: Int,
                    body: String,
                    questionId: Int,
                    saveAnswerListener: SaveAnswerListener
                ) {
                    modifiedAnswerId = id
                    updatedAnswerBody = body
                    viewModel.updateAnswer(id, body, questionId)
                    this@AnswerFragment.saveAnswerListener = saveAnswerListener
                }

                override fun delete(id: Int) {
                    modifiedAnswerId = id
                    app.showConfirmationDialog(requireContext(), R.string.confirm_delete_answer) { _, _ ->
                        viewModel.deleteAnswer(id)
                    }
                }

                override fun vote(id: Int, upvote: Boolean) {
                    modifiedAnswerId = id
                    viewModel.voteAnswer(id, upvote)
                }
            })

            answerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    if (answerId != null && answerId!! > -1) {
                        collapseAppBarLayout?.let { it() }
                        binding.recyclerViewAnswer.scrollToPosition(
                            answerAdapter.findItemPositionByAnswerId(answerId!!)
                        )
                        answerId = null
                    } else if (focusPosition > -1) {
                        collapseAppBarLayout?.let { it() }
                        binding.recyclerViewAnswer.smoothScrollToPosition(focusPosition)
                        focusPosition = -1
                    }
                }
            })
        } else {
            answerAdapter = AnswerAdapter(viewType)
        }

        binding.recyclerViewAnswer.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewAnswer.adapter = answerAdapter
    }

    private fun setListeners() {
        if (viewType == ViewType.Question) {
            binding.btnAddAnswer.setOnClickListener {
                answerAdapter.setActiveEditAnswerPosition(-1)
                showAddAnswerCard()
            }

            binding.imageClose.setOnClickListener {
                hideAddAnswerCard()
            }

            binding.imageSend.setOnClickListener {
                viewModel.createAnswer(
                    binding.editTextBody.text.toString(),
                    questionId!!
                )
            }
        } else {
            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isEnabled = false
                binding.swipeRefreshLayout.isRefreshing = false
                viewModel.getAnswersByUserId()
            }
        }
    }

    private fun showAddAnswerCard() {
        binding.cardAddAnswer.visibility = View.VISIBLE
        binding.spaceAddAnswer.visibility = View.VISIBLE
        binding.editTextBody.requestFocus()
    }

    private fun hideAddAnswerCard() {
        binding.spaceAddAnswer.visibility = View.GONE
        binding.cardAddAnswer.visibility = View.GONE
        binding.textBodyError.visibility = View.GONE
        binding.editTextBody.setText("")
    }

    private fun showLoading() {
        hideAddAnswerCard()
        binding.recyclerViewAnswer.visibility = View.GONE
        binding.btnAddAnswer.visibility = if (viewType == ViewType.Question) View.INVISIBLE else View.GONE
        binding.textNoAnswers.visibility = View.GONE
        binding.textNoQuestionsAnswered.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.GONE
        if (viewType == ViewType.Question) {
            binding.btnAddAnswer.visibility = View.VISIBLE
        } else {
            binding.swipeRefreshLayout.isEnabled = true
        }
    }

    private fun manageHintMsg(visible: Boolean) {
        if (visible) {
            binding.recyclerViewAnswer.visibility = View.GONE
            if (viewType == ViewType.Question) {
                binding.textNoAnswers.visibility = View.VISIBLE
            } else {
                binding.textNoQuestionsAnswered.visibility = View.VISIBLE
            }
        } else {
            binding.recyclerViewAnswer.visibility = View.VISIBLE
        }
    }

    private fun changeViewPosition(view: View) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomToTop = binding.guideline.id
        view.layoutParams = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}