package com.sohaib.downloadmanager.data.dataSources.local

import com.sohaib.downloadmanager.data.dataSources.local.dao.DownloadsDao
import com.sohaib.downloadmanager.data.entities.DownloadEntity

/**
 * Created by: Sohaib Ahmed
 * Date: 3/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class DataSourceLocalDownloads(private val dao: DownloadsDao) {
    suspend fun addDownload(download: DownloadEntity) = dao.insertDownload(download)
    suspend fun getDownloads() = dao.getAllDownloads()
    suspend fun pauseDownload(id: Int) = dao.pauseDownload(id)
    suspend fun resumeDownload(id: Int) = dao.resumeDownload(id)
    suspend fun cancelDownload(id: Int) = dao.cancelDownload(id)
}