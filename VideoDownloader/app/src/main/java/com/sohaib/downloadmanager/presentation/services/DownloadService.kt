package com.sohaib.downloadmanager.presentation.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sohaib.downloadmanager.R
import com.sohaib.downloadmanager.data.dataSources.local.AppDatabase
import com.sohaib.downloadmanager.data.dataSources.local.DataSourceLocalDownloads
import com.sohaib.downloadmanager.data.dataSources.remote.DataSourceRemoteDownloads
import com.sohaib.downloadmanager.data.repository.RepositoryDownloadsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by: Sohaib Ahmed
 * Date: 3/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

// Foreground Download Service
class DownloadService : Service() {

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val activeDownloads = mutableMapOf<Int, Job>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("download_url") ?: return START_NOT_STICKY
        val downloadId = url.hashCode()

        if (!isNetworkAvailable()) {
            showNotification("Download Failed", "No Internet Connection")
            return START_NOT_STICKY
        }

        if (activeDownloads.containsKey(downloadId)) return START_NOT_STICKY

        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = RepositoryDownloadsImpl(DataSourceLocalDownloads(AppDatabase.getDatabase(this@DownloadService).downloadsDao()), DataSourceRemoteDownloads(this@DownloadService))
                val download = repo.startDownload(url)
                activeDownloads.remove(downloadId)
                stopSelf()
            } catch (e: IOException) {
                showNotification("Download Failed", "Network Issue - Tap to Retry")
            }
        }

        activeDownloads[downloadId] = job
        return START_STICKY
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, "download_channel")
            .setSmallIcon(R.drawable.ic_svg_android)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}