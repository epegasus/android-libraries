package com.sohaib.appusagehistory.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohaib.appusagehistory.ui.NonSystemAppsFragment
import com.sohaib.appusagehistory.ui.UsageEventsFragment

class HomeTabsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UsageEventsFragment()
            else -> NonSystemAppsFragment()
        }
    }
}