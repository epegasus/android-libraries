package com.sohaib.appusagehistory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sohaib.appusagehistory.adapter.UsageStatsAdapter
import com.sohaib.appusagehistory.databinding.FragmentUsageListBinding
import com.sohaib.appusagehistory.manager.AppUsageManager

class UsageEventsFragment : Fragment() {

    private var _binding: FragmentUsageListBinding? = null
    private val binding get() = _binding!!

    private val usageStatsAdapter by lazy { UsageStatsAdapter() }
    private val appUsageManager by lazy { AppUsageManager(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        fetchData()
    }

    private fun initRecyclerView() {
        binding.rvList.adapter = usageStatsAdapter
    }

    private fun fetchData() {
        val list = appUsageManager.getRecentUsageEvents()
        usageStatsAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
