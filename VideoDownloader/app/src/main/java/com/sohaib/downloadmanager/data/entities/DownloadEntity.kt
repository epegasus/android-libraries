package com.sohaib.downloadmanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by: Sohaib Ahmed
 * Date: 3/7/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val title: String,
    val status: String,
    val progress: Int,
    val speed: String,
    val fileSize: String,
    val eta: String,
    val thumbnailUrl: String,
    val resolution: String
)