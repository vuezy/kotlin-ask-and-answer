package com.example.askandanswer.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.askandanswer.MyApplication
import com.example.askandanswer.databinding.ActivityMainBinding
import com.example.askandanswer.views.fragments.QuestionFragment

class MainActivity : AppCompatActivity() {
    private lateinit var app: MyApplication
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        showMessage()
        configureQuestionFragment()
        setListeners()
    }

    private fun showMessage() {
        val snackbarMsg = intent.extras?.getString(MyApplication.SUCCESS_MSG_KEY)
        if (snackbarMsg != null) {
            app.showSnackbar(this, binding.root, snackbarMsg, true)
        }
    }

    private fun configureQuestionFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(binding.fragmentQuestion.id, QuestionFragment(QuestionFragment.ViewType.Main))
            .commit()
    }

    private fun setListeners() {
        binding.textSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.imageSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.fabAddQuestion.setOnClickListener {
            val intent = Intent(this, SaveQuestionActivity::class.java)
            startActivity(intent)
        }

        binding.imageProfile.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}