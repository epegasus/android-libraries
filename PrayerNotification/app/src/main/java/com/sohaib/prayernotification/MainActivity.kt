package com.sohaib.prayernotification

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.sohaib.prayernotification.scheduler.PrayerAlarmScheduler
import com.sohaib.prayernotification.service.AzanForegroundService
import com.sohaib.prayernotification.ui.screens.HomeScreen
import com.sohaib.prayernotification.ui.theme.PrayerNotificationTheme

class MainActivity : ComponentActivity() {

    private val scheduler by lazy { PrayerAlarmScheduler(this) }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        Log.d(TAG, "POST_NOTIFICATIONS permission result: $isGranted")
        showToast(if (isGranted) "Notification permission granted." else "Notification permission denied.")
        scheduleIfPossible()
    }

    private val exactAlarmPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val isGranted = activityResult.resultCode == RESULT_OK
        Log.d(TAG, "Exact Alarm permission result: isGranted: $isGranted")
        showToast(if (isGranted) "Exact Alarm permission granted." else "Exact Alarm permission denied.")
        scheduleIfPossible()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrayerNotificationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onEnableClick = { enablePrayerNotifications() },
                        onDisableClick = { disablePrayerNotifications() },
                    )
                }
            }
        }
    }

    /* ---------------------------------- Button Clicks ---------------------------------- */

    private fun enablePrayerNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
            Log.d(TAG, "Requesting POST_NOTIFICATIONS permission.")
            requestNotificationPermission()
            return
        }

        if (!canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms; redirecting to settings.")
            showToast("Please allow exact alarms in settings.")
            openExactAlarmSettings()
            return
        }

        val result = scheduler.scheduleNextPrayer()
        showToast(result.message)
    }

    private fun disablePrayerNotifications() {
        stopService(Intent(this, AzanForegroundService::class.java).apply { action = AzanForegroundService.ACTION_STOP })
        val result = scheduler.cancelNextPrayer()
        showToast(result.message)
    }

    private fun scheduleIfPossible() {
        if (canScheduleExactAlarms() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission())) {
            val result = scheduler.scheduleNextPrayer()
            Log.d(TAG, "scheduleIfPossible result: ${result.message}")
            showToast(result.message)
        }
    }

    /* ---------------------------------- Notification Permission ---------------------------------- */

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /* ---------------------------------- Exact Alarm Permission ---------------------------------- */

    private fun canScheduleExactAlarms(): Boolean {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:$packageName".toUri()
            }
            exactAlarmPermissionLauncher.launch(intent)
        }
    }

    /* ---------------------------------- Utils ---------------------------------- */

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "TAG_MainActivity"
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PrayerNotificationTheme {
        HomeScreen(
            onEnableClick = {},
            onDisableClick = {}
        )
    }
}