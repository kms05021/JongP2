package com.lilly.bluetoothclassic.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class PagerFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
{
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position)
        {
            0 -> DayFragment()
            1 -> WeekFragment()
            else -> MonthFragment()
        }
    }

}