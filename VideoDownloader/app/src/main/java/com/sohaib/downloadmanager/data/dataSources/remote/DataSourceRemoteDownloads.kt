package com.sohaib.downloadmanager.data.dataSources.remote

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import com.sohaib.downloadmanager.data.entities.DownloadEntity
import com.sohaib.downloadmanager.utilities.ConstantUtils.TAG
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.max
import kotlin.math.min
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by: Sohaib Ahmed
 * Date: 3/5/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class DataSourceRemoteDownloads(private val context: Context) {


    private val client = OkHttpClient()

    fun startDownload(url: String, progressCallback: (progress: Int, speed: String, eta: String) -> Unit): DownloadEntity {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code}: ${response.message}")
                }
                val body = response.body ?: throw IOException("Empty response body")
                val contentLength = body.contentLength()

                var downloadedBytes = 0L
                val buffer = ByteArray(8192)
                val startTime = System.currentTimeMillis()

                body.byteStream().use { input ->
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        downloadedBytes += read

                        val elapsedSec = max(1L, (System.currentTimeMillis() - startTime) / 1000)
                        val speed = "${downloadedBytes / 1024 / elapsedSec} KB/s"

                        val progress = when {
                            contentLength > 0 ->
                                min(100, ((downloadedBytes * 100) / contentLength).toInt())
                            else ->
                                min(99, (downloadedBytes / (5 * 1024 * 1024)).toInt())
                        }

                        val eta = when {
                            contentLength > 0 && downloadedBytes > 0 -> {
                                val remainingBytes = contentLength - downloadedBytes
                                val bytesPerSec = downloadedBytes / elapsedSec
                                if (bytesPerSec > 0) {
                                    "${remainingBytes / bytesPerSec} sec left"
                                } else {
                                    "Calculating..."
                                }
                            }
                            contentLength <= 0 -> "Unknown size"
                            else -> "Calculating..."
                        }

                        progressCallback(progress, speed, eta)
                    }
                }

                if (contentLength <= 0 && downloadedBytes > 0) {
                    val elapsedSec = max(1L, (System.currentTimeMillis() - startTime) / 1000)
                    progressCallback(100, "${downloadedBytes / 1024 / elapsedSec} KB/s", "Completed")
                }

                val metadata = extractMetadata(context, url.toUri())
                val sizeLabel = if (contentLength > 0) "$contentLength bytes" else "$downloadedBytes bytes"

                return DownloadEntity(
                    url = url,
                    title = metadata["title"] ?: "Video.mp4",
                    status = "completed",
                    progress = 100,
                    speed = "N/A",
                    fileSize = sizeLabel,
                    eta = "Completed",
                    thumbnailUrl = metadata["thumbnailUrl"] ?: "",
                    resolution = metadata["resolution"] ?: "N/A"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            val detail = e.message?.takeIf { it.isNotBlank() } ?: e.javaClass.simpleName
            throw IOException("Download failed: $detail", e)
        }
    }

    fun extractMetadata(context: Context, videoUri: Uri): Map<String, String> {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown"
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0"
            val resolution = "${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)}x${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)}"

            val thumbnail = retriever.frameAtTime?.let { bitmap ->
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            } ?: ""

            mapOf("title" to title, "duration" to duration, "resolution" to resolution, "thumbnailUrl" to thumbnail)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract metadata: ${e.message}")
            emptyMap()
        } finally {
            retriever.release()
        }
    }
}