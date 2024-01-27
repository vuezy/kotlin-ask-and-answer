package com.example.askandanswer.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import com.example.askandanswer.MyApplication
import com.example.askandanswer.R
import com.example.askandanswer.databinding.ActivityQuestionBinding
import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.QuestionOperationState
import com.example.askandanswer.models.users.GetUserDataState
import com.example.askandanswer.viewmodels.AnswerViewModel
import com.example.askandanswer.viewmodels.QuestionViewModel
import com.example.askandanswer.viewmodels.UserViewModel
import com.example.askandanswer.views.fragments.AnswerFragment

class QuestionActivity : AppCompatActivity() {
    private lateinit var app: MyApplication
    private lateinit var binding: ActivityQuestionBinding
    private val questionViewModel: QuestionViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val answerViewModel: AnswerViewModel by viewModels()
    private var questionId: Int? = null
    private var userId: Int? = null
    private var answerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        questionId = intent.extras?.getInt(MyApplication.QUESTION_ID_KEY)
        if (questionId == null) finish()

        answerId = intent.extras?.getInt(MyApplication.ANSWER_ID_KEY, -1)
        observeLiveData()
        setListeners()
        userViewModel.getUserData()
    }

    private fun observeLiveData() {
        userViewModel.getUserDataState.observe(this) { state ->
            when (state) {
                is GetUserDataState.Success -> {
                    app.stopLoading()
                    userId = state.user.id
                    configureAnswerFragment()
                    questionViewModel.getQuestionById(questionId!!)
                }
                is GetUserDataState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(this, binding.root, state.msg)
                }
                is GetUserDataState.AuthenticationError -> {
                    app.stopLoading()
                    app.logout(this, state.msg)
                }
                is GetUserDataState.Loading -> {
                    app.showLoading(this, layoutInflater)
                }
            }
        }

        questionViewModel.getQuestionByIdState.observe(this) { state ->
            when (state) {
                is GetQuestionByIdState.Success -> {
                    app.stopLoading()
                    val question = state.question

                    binding.textTitle.text = question.title
                    binding.textBody.text = question.body
                    binding.textPriority.text = question.priorityLevel.toString()
                    binding.textUpdatedAt.text = app.formatDateString(question.updatedAt)
                    binding.textUserName.text = question.userName
                    binding.textUserEmail.text = question.userEmail
                    binding.cardClosed.visibility = if (question.closed) View.VISIBLE else View.GONE
                    if (!question.closed && userId != null && question.userId == userId) {
                        binding.imageActions.visibility = View.VISIBLE
                    } else {
                        binding.imageActions.visibility = View.INVISIBLE
                    }
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

        questionViewModel.questionOperationState.observe(this) { state ->
            when (state) {
                is QuestionOperationState.Success -> {
                    app.stopLoading()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MyApplication.SUCCESS_MSG_KEY, state.msg)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is QuestionOperationState.Error -> {
                    app.stopLoading()
                    app.showSnackbar(this, binding.root, state.msg)
                }
                is QuestionOperationState.AuthenticationError -> {
                    app.stopLoading()
                    app.logout(this, state.msg)
                }
                is QuestionOperationState.Loading -> {
                    app.showLoading(this, layoutInflater)
                }
            }
        }
    }

    private fun setListeners() {
        val actionsMenu = PopupMenu(this, binding.imageActions)
        actionsMenu.inflate(R.menu.question_actions)
        actionsMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    val intent = Intent(this, SaveQuestionActivity::class.java)
                    intent.putExtra(MyApplication.QUESTION_ID_KEY, questionId)
                    startActivity(intent)
                    true
                }
                R.id.close -> {
                    app.showConfirmationDialog(this, R.string.confirm_close) { _, _ ->
                        questionViewModel.closeQuestion(questionId!!)
                    }
                    true
                }
                R.id.delete -> {
                    app.showConfirmationDialog(this, R.string.confirm_delete_question) { _, _ ->
                        questionViewModel.deleteQuestion(questionId!!)
                    }
                    true
                }
                else -> false
            }
        }

        binding.imageActions.setOnClickListener {
            actionsMenu.show()
        }

        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefreshLayout.isEnabled = verticalOffset == 0
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isEnabled = false
            binding.swipeRefreshLayout.isRefreshing = false
            questionViewModel.getQuestionById(questionId!!)
            answerViewModel.getAnswersByQuestionId(questionId!!)
        }
    }

    private fun configureAnswerFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                binding.fragmentAnswer.id,
                AnswerFragment(AnswerFragment.ViewType.Question, questionId, userId, answerId) {
                    binding.appBarLayout.setExpanded(false)
                }
            )
            .commit()
    }
}