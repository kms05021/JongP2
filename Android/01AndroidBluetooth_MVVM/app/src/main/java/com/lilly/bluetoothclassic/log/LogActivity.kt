package com.lilly.bluetoothclassic.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lilly.bluetoothclassic.fragment.PagerFragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lilly.bluetoothclassic.databinding.ActivityLogBinding

class LogActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 탭 설정
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // 뷰페이지 어댑터 연결
        binding.viewPager.adapter = PagerFragmentStateAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            when (position) {
                0 -> tab.text = "Daily"
                1 -> tab.text = "Week"
                2 -> tab.text = "Month"
            }
        }.attach()
    }
}