package com.example.askandanswer.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.lifecycleScope
import com.example.askandanswer.databinding.ActivitySearchBinding
import com.example.askandanswer.viewmodels.QuestionViewModel
import com.example.askandanswer.views.fragments.QuestionFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: QuestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.searchView.requestFocus()

        configureQuestionFragment()
        setListeners()
    }

    private fun configureQuestionFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(binding.fragmentQuestion.id, QuestionFragment(QuestionFragment.ViewType.Search))
            .commit()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            finish()
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            private var debounceJob: Job? = null

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                debounceJob?.cancel()
                debounceJob = lifecycleScope.launch {
                    delay(700L)
                    if (!newText.isNullOrEmpty()) viewModel.getQuestions(newText)
                }
                return true
            }
        })
    }
}