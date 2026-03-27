package com.sohaib.cameraview.demo.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.sohaib.cameraview.helper.CameraException
import com.sohaib.cameraview.helper.CameraListener
import com.sohaib.cameraview.CameraView
import com.sohaib.cameraview.helper.FileCallback
import com.sohaib.cameraview.helper.PictureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed interface InitResult {
    data object Success : InitResult
    data class Failure(val message: String, val throwable: Throwable? = null) : InitResult
}

sealed interface CaptureResult {
    data class Success(val uri: Uri) : CaptureResult
    data class Failure(val message: String, val throwable: Throwable? = null) : CaptureResult
}

class CameraCaptureManager(
    private val context: Context,
    private val cameraView: CameraView
) {

    fun init(): InitResult {
        return runCatching {
            cameraView.open()
        }.fold(
            onSuccess = { InitResult.Success },
            onFailure = { InitResult.Failure("Unable to open camera.", it) }
        )
    }

    suspend fun captureAndSaveFilteredImage(): CaptureResult {
        return runCatching {
            val picture = captureSnapshot()
            val tempFile = writeToTempFile(picture)
            val uri = publishToGallery(tempFile)
            tempFile.delete()
            uri
        }.fold(
            onSuccess = { CaptureResult.Success(it) },
            onFailure = { CaptureResult.Failure(it.message ?: "Capture failed.", it) }
        )
    }

    private suspend fun captureSnapshot(): PictureResult = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val listener = object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    cameraView.removeCameraListener(this)
                    if (continuation.isCancelled) return
                    if (result.data.isEmpty()) {
                        continuation.resumeWithException(IllegalStateException("Empty image data."))
                        return
                    }
                    continuation.resume(result)
                }

                override fun onCameraError(exception: CameraException) {
                    cameraView.removeCameraListener(this)
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(exception)
                }
            }

            cameraView.addCameraListener(listener)
            continuation.invokeOnCancellation { cameraView.removeCameraListener(listener) }
            try {
                // Snapshot capture goes through GL path, so active filters are applied.
                cameraView.takePictureSnapshot()
            } catch (e: Exception) {
                cameraView.removeCameraListener(listener)
                continuation.resumeWithException(e)
            }
        }
    }

    private suspend fun writeToTempFile(result: PictureResult): File = withContext(Dispatchers.Main) {
        val dir = File(context.cacheDir, "captures").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        suspendCancellableCoroutine { continuation ->
            result.toFile(file, object : FileCallback {
                override fun onFileReady(file: File?) {
                    if (continuation.isCancelled) return
                    if (file == null) {
                        continuation.resumeWithException(IllegalStateException("Failed to write image file."))
                    } else {
                        continuation.resume(file)
                    }
                }
            })
        }
    }

    private fun publishToGallery(sourceFile: File): Uri {
        val resolver = context.contentResolver
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val displayName = "CameraFilters_$stamp.jpg"
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        var pendingUri: Uri? = null
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/CameraFilters")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            pendingUri = resolver.insert(collection, values)
                ?: throw IllegalStateException("Could not create gallery item.")

            resolver.openOutputStream(pendingUri)?.use { output ->
                sourceFile.inputStream().use { input -> input.copyTo(output) }
            } ?: throw IllegalStateException("Could not write gallery file.")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.update(
                    pendingUri,
                    ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) },
                    null,
                    null
                )
            }

            return pendingUri
        } catch (e: Exception) {
            pendingUri?.let {
                runCatching { resolver.delete(it, null, null) }
            }
            throw e
        }
    }
}