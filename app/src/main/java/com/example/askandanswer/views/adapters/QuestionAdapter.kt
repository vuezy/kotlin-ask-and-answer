package com.example.askandanswer.views.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.ItemQuestionBinding
import com.example.askandanswer.models.questions.Question
import com.example.askandanswer.views.activities.QuestionActivity

class QuestionAdapter : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    private var questions = listOf<Question>()

    @SuppressLint("NotifyDataSetChanged")
    fun setQuestions(questions: List<Question>) {
        this.questions = questions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    class ViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        private var question: Question? = null

        init {
            binding.root.setOnClickListener {
                val context = it.context
                question?.let { data ->
                    val intent = Intent(context, QuestionActivity::class.java)
                    intent.putExtra(MyApplication.QUESTION_ID_KEY, data.id)
                    context.startActivity(intent)
                }
            }
        }

        fun bind(question: Question) {
            this.question = question
            binding.textTitle.text = question.title
            binding.textBody.text = question.body
            binding.textPriority.text = question.priorityLevel.toString()
            binding.textUpdatedAt.text = MyApplication.getInstance().formatDateString(question.updatedAt)
            binding.cardClosed.visibility = if (question.closed) View.VISIBLE else View.INVISIBLE
        }
    }
}