package com.sohaib.downloadmanager.data.dataSources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sohaib.downloadmanager.data.entities.DownloadEntity

/**
 * Created by: Sohaib Ahmed
 * Date: 3/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

@Dao
interface DownloadsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("SELECT * FROM downloads")
    suspend fun getAllDownloads(): List<DownloadEntity>

    @Query("UPDATE downloads SET status = 'paused' WHERE id = :id")
    suspend fun pauseDownload(id: Int)

    @Query("UPDATE downloads SET status = 'downloading' WHERE id = :id")
    suspend fun resumeDownload(id: Int)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun cancelDownload(id: Int)
}