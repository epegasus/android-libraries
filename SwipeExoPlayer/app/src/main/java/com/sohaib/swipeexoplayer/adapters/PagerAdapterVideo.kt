package com.sohaib.swipeexoplayer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohaib.swipeexoplayer.fragments.FragmentVideo

/**
 * Created by: Sohaib Ahmed
 * Date: 3/3/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class PagerAdapterVideo(fragmentActivity: FragmentActivity, private val videoUrls: List<String>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = videoUrls.size

    override fun createFragment(position: Int): Fragment {
        return FragmentVideo.newInstance(videoUrls[position], position)
    }
}