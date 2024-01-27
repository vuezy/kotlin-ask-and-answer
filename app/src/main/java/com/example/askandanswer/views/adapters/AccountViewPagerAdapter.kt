package com.example.askandanswer.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.askandanswer.views.fragments.AnswerFragment
import com.example.askandanswer.views.fragments.ProfileFragment
import com.example.askandanswer.views.fragments.QuestionFragment

class AccountViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment()
            1 -> QuestionFragment(QuestionFragment.ViewType.Account)
            2 -> AnswerFragment(AnswerFragment.ViewType.Account)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}