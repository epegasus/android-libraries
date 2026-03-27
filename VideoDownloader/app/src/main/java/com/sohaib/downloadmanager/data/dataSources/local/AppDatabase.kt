package com.sohaib.downloadmanager.data.dataSources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(entities = [DownloadEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadsDao(): DownloadsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "downloads_db").build()
                instance = newInstance
                newInstance
            }
        }
    }
}