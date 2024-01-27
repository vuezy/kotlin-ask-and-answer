package com.example.askandanswer.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.example.askandanswer.MyApplication
import com.example.askandanswer.views.adapters.AuthViewPagerAdapter
import com.example.askandanswer.databinding.ActivityAuthBinding
import com.example.askandanswer.models.users.IsLoggedInState
import com.example.askandanswer.viewmodels.UserViewModel
import com.google.android.material.tabs.TabLayout

class AuthActivity : AppCompatActivity() {
    private lateinit var app: MyApplication
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: UserViewModel by viewModels()
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreen
        }
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        configureViewPager()
        observeLiveData()
        showMessageOrLogUserIn()
    }

    private fun showMessageOrLogUserIn() {
        val snackbarMsg = intent.extras?.getString(MyApplication.LOGOUT_MSG_KEY)
        if (snackbarMsg != null) {
            keepSplashScreen = false
            if (snackbarMsg != "") app.showSnackbar(this, binding.root, snackbarMsg)
        } else {
            viewModel.isLoggedIn()
        }
    }

    private fun configureViewPager() {
        val pagerAdapter = AuthViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeLiveData() {
        viewModel.isLoggedInState.observe(this) { state ->
            when (state) {
                is IsLoggedInState.Success -> {
                    app.stopLoading()
                    keepSplashScreen = false
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is IsLoggedInState.Error -> {
                    app.stopLoading()
                    keepSplashScreen = false
                }
                is IsLoggedInState.Loading -> {
                    app.showLoading(this, layoutInflater)
                }
            }
        }
    }
}