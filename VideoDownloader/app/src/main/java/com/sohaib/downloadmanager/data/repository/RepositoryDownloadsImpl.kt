package com.sohaib.downloadmanager.data.repository

import com.sohaib.downloadmanager.data.dataSources.local.DataSourceLocalDownloads
import com.sohaib.downloadmanager.data.dataSources.remote.DataSourceRemoteDownloads
import com.sohaib.downloadmanager.data.entities.DownloadEntity
import com.sohaib.downloadmanager.domain.repository.RepositoryDownloads
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by: Sohaib Ahmed
 * Date: 3/5/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class RepositoryDownloadsImpl(
    private val localDataSource: DataSourceLocalDownloads,
    private val remoteDataSource: DataSourceRemoteDownloads
) : RepositoryDownloads {
    override suspend fun addDownload(download: DownloadEntity) {
        localDataSource.addDownload(download)
    }

    override suspend fun getDownloads(): List<DownloadEntity> {
        return localDataSource.getDownloads()
    }

    override suspend fun updateDownload(download: DownloadEntity) {
        localDataSource.addDownload(download)
    }

    override suspend fun startDownload(url: String): DownloadEntity {
        return withContext(Dispatchers.IO) {
            val download = remoteDataSource.startDownload(url) { progress, _, _ ->
                createNotification(progress)
            }
            addDownload(download)
            download
        }
    }

    override suspend fun pauseDownload(id: Int) {
        localDataSource.pauseDownload(id)
    }

    override suspend fun resumeDownload(id: Int) {
        localDataSource.resumeDownload(id)
    }

    override suspend fun cancelDownload(id: Int) {
        localDataSource.cancelDownload(id)
    }

    override suspend fun retryDownload(id: Int, url: String) {

    }

    fun createNotification(progress: Int) {
        /*val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "download_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Download Notifications", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("title")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notificationManager.notify(1, notification)*/
    }
}