package com.example.askandanswer.views.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.ItemAnswerBinding
import com.example.askandanswer.interfaces.AnswerItemUtils
import com.example.askandanswer.models.answers.Answer
import com.example.askandanswer.interfaces.SaveAnswerListener
import com.example.askandanswer.views.activities.QuestionActivity
import com.example.askandanswer.views.fragments.AnswerFragment

class AnswerAdapter(
    private val fragmentViewType: AnswerFragment.ViewType,
    private val answerItemUtils: AnswerItemUtils? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var answers = mutableListOf<Answer>(); private set
    private var activeEditAnswerPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    @JvmName("setListOfAnswers")
    fun setAnswers(answers: List<Answer>) {
        this.answers = answers.toMutableList()
        notifyDataSetChanged()
    }

    fun setActiveEditAnswerPosition(answerId: Int) {
        if (activeEditAnswerPosition > -1) notifyItemChanged(activeEditAnswerPosition)
        activeEditAnswerPosition = findItemPositionByAnswerId(answerId)
    }

    fun updateAnswer(id: Int, body: String) {
        val position = findItemPositionByAnswerId(id)
        if (position == -1) return
        answers[position].body = body
        notifyItemChanged(position)
    }

    fun deleteAnswer(id: Int) {
        val position = findItemPositionByAnswerId(id)
        if (position == -1) return
        answers.removeAt(position)
        notifyItemRemoved(position)
    }

    fun voteAnswer(id: Int, votes: Int) {
        val position = findItemPositionByAnswerId(id)
        if (position == -1) return
        answers[position].votes = votes
        notifyItemChanged(position)
    }

    fun findItemPositionByAnswerId(id: Int): Int {
        return answers.indexOfFirst { answer -> answer.id == id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAnswerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return when (fragmentViewType) {
            AnswerFragment.ViewType.Question -> QuestionTypeViewHolder(binding, answerItemUtils!!)
            AnswerFragment.ViewType.Account -> AccountTypeViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (fragmentViewType) {
            AnswerFragment.ViewType.Question -> (holder as QuestionTypeViewHolder).bind(answers[position])
            AnswerFragment.ViewType.Account -> (holder as AccountTypeViewHolder).bind(answers[position])
        }
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    class QuestionTypeViewHolder(
        private val binding: ItemAnswerBinding,
        private val answerItemUtils: AnswerItemUtils
    ) : RecyclerView.ViewHolder(binding.root) {
        private var answer: Answer? = null

        init {
            setListeners()
        }

        fun bind(answer: Answer) {
            this.answer = answer
            binding.editTextBody.visibility = View.GONE
            binding.textBodyError.visibility = View.GONE
            binding.btnSaveAnswer.visibility = View.GONE
            binding.btnCancelAnswer.visibility = View.GONE
            binding.textBody.visibility = View.VISIBLE
            binding.textUpdatedAt.visibility = View.VISIBLE

            binding.textName.text = answer.userName
            binding.textEmail.text = answer.userEmail
            binding.textBody.text = answer.body
            binding.editTextBody.setText(answer.body)
            binding.textVotes.text = answer.votes.toString()
            binding.textUpdatedAt.text =
                MyApplication.getInstance().formatDateString(answer.updatedAt)

            if (answer.userId == answerItemUtils.userId) {
                binding.btnUpvote.visibility = View.GONE
                binding.btnDownvote.visibility = View.GONE
                binding.layoutActionButtons.visibility = View.VISIBLE
            } else {
                binding.layoutActionButtons.visibility = View.GONE
                binding.btnUpvote.visibility = View.VISIBLE
                binding.btnDownvote.visibility = View.VISIBLE
            }
        }

        private fun setListeners() {
            binding.btnEditAnswer.setOnClickListener {
                answer?.let { data ->
                    answerItemUtils.startEdit(data.id)
                    binding.textBody.visibility = View.GONE
                    binding.textUpdatedAt.visibility = View.GONE
                    binding.layoutActionButtons.visibility = View.GONE
                    binding.btnUpvote.visibility = View.GONE
                    binding.btnDownvote.visibility = View.GONE
                    binding.editTextBody.visibility = View.VISIBLE
                    binding.btnSaveAnswer.visibility = View.VISIBLE
                    binding.btnCancelAnswer.visibility = View.VISIBLE
                    binding.editTextBody.requestFocus()
                }
            }

            binding.btnCancelAnswer.setOnClickListener {
                answerItemUtils.cancelEdit()
            }

            binding.btnSaveAnswer.setOnClickListener {
                answer?.let { data ->
                    answerItemUtils.save(
                        data.id,
                        binding.editTextBody.text.toString(),
                        data.questionId,
                        object : SaveAnswerListener {
                            override fun onSuccess() {
                                answerItemUtils.cancelEdit()
                            }

                            override fun onError(msg: String) {
                                binding.textBodyError.text = msg
                                binding.textBodyError.visibility = View.VISIBLE
                            }
                        }
                    )
                }
            }

            binding.btnDeleteAnswer.setOnClickListener {
                answer?.let { data ->
                    answerItemUtils.delete(data.id)
                }
            }

            binding.btnUpvote.setOnClickListener {
                answer?.let { data ->
                    answerItemUtils.vote(data.id, true)
                }
            }

            binding.btnDownvote.setOnClickListener {
                answer?.let { data ->
                    answerItemUtils.vote(data.id, false)
                }
            }
        }
    }

    class AccountTypeViewHolder(private val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        private var answer: Answer? = null

        init {
            binding.root.setOnClickListener {
                val context = it.context
                answer?.let { data ->
                    val intent = Intent(context, QuestionActivity::class.java)
                    intent.putExtra(MyApplication.QUESTION_ID_KEY, data.questionId)
                    intent.putExtra(MyApplication.ANSWER_ID_KEY, data.id)
                    context.startActivity(intent)
                }
            }
        }

        fun bind(answer: Answer) {
            this.answer = answer
            binding.textName.visibility = View.GONE
            binding.textEmail.visibility = View.GONE
            binding.btnUpvote.visibility = View.GONE
            binding.btnDownvote.visibility = View.GONE
            binding.textBody.maxLines = 3
            binding.textBody.ellipsize = TextUtils.TruncateAt.END
            binding.textBody.text = answer.body
            binding.textVotes.text = answer.votes.toString()
            binding.textUpdatedAt.text = MyApplication.getInstance().formatDateString(answer.updatedAt)
        }
    }
}