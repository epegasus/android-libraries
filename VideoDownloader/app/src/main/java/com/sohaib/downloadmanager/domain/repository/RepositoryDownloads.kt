package com.sohaib.downloadmanager.domain.repository

import com.sohaib.downloadmanager.data.entities.DownloadEntity

/**
 * Created by: Sohaib Ahmed
 * Date: 3/5/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

interface RepositoryDownloads {
    suspend fun addDownload(download: DownloadEntity)
    suspend fun getDownloads(): List<DownloadEntity>
    suspend fun updateDownload(download: DownloadEntity)
    suspend fun startDownload(url: String): DownloadEntity
    suspend fun pauseDownload(id: Int)
    suspend fun resumeDownload(id: Int)
    suspend fun cancelDownload(id: Int)
    suspend fun retryDownload(id: Int, url: String)
}