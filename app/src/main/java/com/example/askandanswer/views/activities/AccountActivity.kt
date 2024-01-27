package com.example.askandanswer.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.askandanswer.MyApplication
import com.example.askandanswer.R
import com.example.askandanswer.databinding.ActivityAccountBinding
import com.example.askandanswer.models.users.LogoutState
import com.example.askandanswer.viewmodels.UserViewModel
import com.example.askandanswer.views.adapters.AccountViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import java.util.concurrent.locks.ReentrantLock

class AccountActivity : AppCompatActivity() {
    private lateinit var app: MyApplication
    private lateinit var binding: ActivityAccountBinding
    private val viewModel: UserViewModel by viewModels()
    private val lock = ReentrantLock()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        configureViewPager()
        observeLiveData()
        setListeners()
    }

    private fun configureViewPager() {
        val pagerAdapter = AccountViewPagerAdapter(this)
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
        viewModel.logoutState.observe(this) { state ->
            when (state) {
                is LogoutState.Success -> {
                    app.stopLoading()
                    app.logout(this)
                }
                is LogoutState.Error -> {
                    app.stopLoading()
                    app.logout(this, state.msg)
                }
                is LogoutState.Loading -> {
                    app.showLoading(this, layoutInflater)
                }
            }
        }
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            safelyPerformAction { finish() }
        }

        binding.imageLogout.setOnClickListener {
            safelyPerformAction {
                app.showConfirmationDialog(this, R.string.confirm_logout) { _, _ ->
                    viewModel.logout()
                }
            }
        }
    }

    private fun safelyPerformAction(action: () -> Unit) {
        if (lock.tryLock()) {
            try {
                action()
            } catch (e: Exception) {
                Log.e("AccountActivity", "Error from safelyPerformAction method.", e)
            } finally {
                lock.unlock()
            }
        }
    }
}