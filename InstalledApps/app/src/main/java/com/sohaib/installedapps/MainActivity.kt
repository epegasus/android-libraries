package com.sohaib.installedapps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sohaib.installedapps.adapters.AdapterApps
import com.sohaib.installedapps.dataClasses.App
import com.sohaib.installedapps.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter by lazy { AdapterApps() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView()
        fetchData()
    }

    private fun initRecyclerView() {
        binding.rvList.adapter = adapter
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val list = getInstalledApps()
            adapter.submitList(list)
            binding.progressBar.visibility = View.GONE
        }
    }

    private suspend fun getInstalledApps(): List<App> = withContext(Dispatchers.IO) {
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val arrayList = arrayListOf<App>()

        installedApps.forEachIndexed { index, appInfo ->

            // This is a User-Installed app
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = appInfo.loadLabel(packageManager).toString()
                val packageName = appInfo.packageName
                val appIcon = appInfo.loadIcon(packageManager)

                // Print the app name and package name
                Log.d("TAG", "getInstalledApps: User-Installed App Name: $appName, Package Name: $packageName")
                arrayList.add(App(id = index, icon = appIcon, appName = appName, packageName = packageName))
            }
        }

        arrayList.sortBy { it.appName }
        arrayList
    }
}