package com.sohaib.appusagehistory.manager

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserManager
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppUsageManager(context: Context) {

    private val appContext = context.applicationContext

    // Checks whether usage access permission is currently granted.
    @Suppress("DEPRECATION")
    fun isPermissionGranted(): Boolean {
        val appOpsManager = appContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow("android:get_usage_stats", Process.myUid(), appContext.packageName)
        } else {
            appOpsManager.checkOpNoThrow("android:get_usage_stats", Process.myUid(), appContext.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Checks whether the current Android user profile is unlocked.
    fun isUserUnlocked(): Boolean {
        val userManager = appContext.getSystemService(Context.USER_SERVICE) as UserManager
        return userManager.isUserUnlocked
    }

    // Provides an intent that opens Usage Access settings screen.
    fun getUsageAccessSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    // Returns recent usage events for launcher-visible apps with optional system-app inclusion.
    fun getRecentUsageEvents(lastMinutes: Int = 10, includeSystemApps: Boolean = false): List<String> {
        val pm = appContext.packageManager
        val usageStatsManager = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = ArrayList<Pair<Long, String>>()
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val launcherApps = pm.queryIntentActivities(launcherIntent, 0).distinctBy { it.activityInfo.packageName }
        val launcherPackages = launcherApps.map { it.activityInfo.packageName }.toHashSet()
        val systemPackageMap = launcherApps.associate {
            val isSystem = it.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            it.activityInfo.packageName to isSystem
        }
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (1000L * 60L * lastMinutes)
        val usageEvents = usageStatsManager.queryEvents(startTime, currentTime)
        val usageEvent = UsageEvents.Event()
        val dateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(usageEvent)
            val packageName = usageEvent.packageName ?: "Unknown package"
            if (!launcherPackages.contains(packageName)) continue
            if (!includeSystemApps && systemPackageMap[packageName] == true) continue
            val eventTime = dateFormatter.format(Date(usageEvent.timeStamp))
            events.add(usageEvent.timeStamp to "$packageName\nUsed at: $eventTime")
        }
        val result = events
            .sortedByDescending { it.first }
            .distinctBy { it.second }
            .map { it.second }
        return result.ifEmpty { listOf("No recent usage events in last $lastMinutes minutes") }
    }

    // Returns launcher-visible installed apps with optional system-app inclusion.
    fun getInstalledApps(includeSystemApps: Boolean = false): List<String> {
        val pm = appContext.packageManager

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = pm.queryIntentActivities(intent, 0)

        val result = apps
            .distinctBy { it.activityInfo.packageName }
            .filter { resolveInfo ->
                if (includeSystemApps) return@filter true
                val appInfo = resolveInfo.activityInfo.applicationInfo
                appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
            .map {
                val label = it.loadLabel(pm).toString()
                val packageName = it.activityInfo.packageName
                "$label\nPackage: $packageName"
            }
            .sortedBy { it.lowercase() }

        return result.ifEmpty { listOf("No apps found") }
    }
}