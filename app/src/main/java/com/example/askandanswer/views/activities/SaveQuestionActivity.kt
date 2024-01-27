package com.example.askandanswer.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import com.example.askandanswer.MyApplication
import com.example.askandanswer.R
import com.example.askandanswer.databinding.ActivitySaveQuestionBinding
import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.SaveQuestionState
import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.viewmodels.QuestionViewModel
import com.example.askandanswer.viewmodels.UserViewModel

class SaveQuestionActivity : AppCompatActivity() {
    private lateinit var app: MyApplication
    private lateinit var binding: ActivitySaveQuestionBinding
    private val questionViewModel: QuestionViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var questionId: Int? = null
    private var minPriority: Int = 0
    private var maxPriority: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        questionId = intent.extras?.getInt(MyApplication.QUESTION_ID_KEY)
        observeLiveData()
        setListeners()
        if (questionId != null) {
            binding.textPageTitle.text = getString(R.string.update_question)
            questionViewModel.getQuestionById(questionId!!)
        }
    }

    private fun observeLiveData() {
        if (questionId != null) {
            questionViewModel.getQuestionByIdState.observe(this) { state ->
                when (state) {
                    is GetQuestionByIdState.Success -> {
                        app.stopLoading()
                        binding.editTextTitle.setText(state.question.title)
                        binding.editTextBody.setText(state.question.body)
                        binding.editTextPriority.setText(state.question.priorityLevel.toString())
                        minPriority = state.question.priorityLevel
                        userViewModel.getPointsAndCredits()
                    }
                    is GetQuestionByIdState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(this, binding.root, state.msg)
                    }
                    is GetQuestionByIdState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(this, state.msg)
                    }
                    is GetQuestionByIdState.Loading -> {
                        app.showLoading(this, layoutInflater)
                    }
                }
            }

            userViewModel.getPointsAndCreditsState.observe(this) { state ->
                when (state) {
                    is GetPointsAndCreditsState.Success -> {
                        app.stopLoading()
                        binding.textCredits.text = state.credits.toString()
                        maxPriority = minPriority + state.credits
                    }
                    is GetPointsAndCreditsState.Error -> {
                        app.stopLoading()
                        app.showSnackbar(this, binding.root, state.msg)
                    }
                    is GetPointsAndCreditsState.AuthenticationError -> {
                        app.stopLoading()
                        app.logout(this, state.msg)
                    }
                    is GetPointsAndCreditsState.Loading -> {
                        app.showLoading(this, layoutInflater)
                    }
                }
            }
        }

        questionViewModel.saveQuestionState.observe(this) { state ->
            when (state) {
                is SaveQuestionState.Success -> {
                    app.stopLoading()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MyApplication.SUCCESS_MSG_KEY, state.msg)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is SaveQuestionState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(this, binding.root, state.msg)
                }
                is SaveQuestionState.AuthenticationError -> {
                    app.stopLoading()
                    app.logout(this, state.msg)
                }
                is SaveQuestionState.ValidationError -> {
                    app.stopLoading()
                    val err = state.err
                    if (err.title != null) {
                        binding.textTitleError.text = err.title
                        binding.textTitleError.visibility = View.VISIBLE
                    }
                    if (err.body != null) {
                        binding.textBodyError.text = err.body
                        binding.textBodyError.visibility = View.VISIBLE
                    }
                    if (err.priorityLevel != null) {
                        binding.textPriorityError.text = err.priorityLevel
                        binding.textPriorityError.visibility = View.VISIBLE
                    }
                }
                is SaveQuestionState.Loading -> {
                    app.showLoading(this, layoutInflater)
                    binding.textTitleError.visibility = View.GONE
                    binding.textBodyError.visibility = View.GONE
                    binding.textPriorityError.visibility = View.GONE
                }
            }
        }
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            finish()
        }

        binding.editTextPriority.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) onEditTextPriorityFocusChange(v as EditText)
        }

        binding.btnSave.setOnClickListener {
            onEditTextPriorityFocusChange(binding.editTextPriority)
            val title = binding.editTextTitle.text.toString().trim()
            val body = binding.editTextBody.text.toString().trim()
            val priorityLevel = binding.editTextPriority.text.toString().toIntOrNull() ?: 0

            if (questionId != null) {
                questionViewModel.updateQuestion(questionId!!, title, body, priorityLevel)
            } else {
                questionViewModel.createQuestion(title, body, priorityLevel)
            }
        }
    }

    private fun onEditTextPriorityFocusChange(editTxtPriority: EditText) {
        var priority = editTxtPriority.text.toString().toIntOrNull() ?: 0

        if (priority < minPriority) {
            priority = minPriority
        } else if (priority > maxPriority) {
            priority = maxPriority
        }

        editTxtPriority.setText(priority.toString())
        binding.textCredits.text = (maxPriority - priority).toString()
    }
}