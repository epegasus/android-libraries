package com.sohaib.downloadmanager.domain.useCases

import com.sohaib.downloadmanager.data.entities.DownloadEntity
import com.sohaib.downloadmanager.data.repository.RepositoryDownloadsImpl

/**
 * Created by: Sohaib Ahmed
 * Date: 3/5/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class UseCaseDownloads(private val repository: RepositoryDownloadsImpl) {

    suspend fun getDownloads() = repository.getDownloads()
    suspend fun addDownload(download: DownloadEntity) = repository.addDownload(download)
    suspend fun startDownload(url: String) = repository.startDownload(url)
    suspend fun pauseDownload(id: Int) = repository.pauseDownload(id)
    suspend fun resumeDownload(id: Int) = repository.resumeDownload(id)
    suspend fun cancelDownload(id: Int) = repository.cancelDownload(id)
    suspend fun retryDownload(id: Int, url: String) = repository.retryDownload(id, url)
}