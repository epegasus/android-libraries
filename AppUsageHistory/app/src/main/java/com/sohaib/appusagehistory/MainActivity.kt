package com.sohaib.appusagehistory

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.sohaib.appusagehistory.adapter.HomeTabsPagerAdapter
import com.sohaib.appusagehistory.databinding.ActivityMainBinding
import com.sohaib.appusagehistory.manager.AppUsageManager
import com.sohaib.appusagehistory.utils.GeneralUtils.showToast

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val appUsageManager by lazy { AppUsageManager(this) }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        fetchAppUsage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchAppUsage()

        binding.mbRequestPermission.setOnClickListener { requestForPermission() }
    }

    private fun fetchAppUsage() {
        if (!appUsageManager.isPermissionGranted()) {
            showToast("Permission Not Granted")
            binding.mbRequestPermission.visibility = View.VISIBLE
            binding.tabLayout.visibility = View.GONE
            binding.vpTabs.visibility = View.GONE
            return
        }
        if (!appUsageManager.isUserUnlocked()) {
            binding.mbRequestPermission.visibility = View.VISIBLE
            showToast("User device is not unlocked")
            binding.tabLayout.visibility = View.GONE
            binding.vpTabs.visibility = View.GONE
            return
        }
        setupTabs()
    }

    private fun setupTabs() {
        binding.mbRequestPermission.visibility = View.GONE
        binding.tabLayout.visibility = View.VISIBLE
        binding.vpTabs.visibility = View.VISIBLE

        binding.vpTabs.adapter = HomeTabsPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.vpTabs) { tab, position ->
            tab.text = if (position == 0) getString(R.string.tab_usage_events) else getString(R.string.tab_installed_apps)
        }.attach()
    }

    private fun requestForPermission() {
        try {
            resultLauncher.launch(appUsageManager.getUsageAccessSettingsIntent())
        } catch (_: ActivityNotFoundException) {
            showToast("Unable to open Usage Access settings")
        }
    }
}