package com.sohaib.pdfmaker

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.sohaib.pdfmaker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PERMISSION_REQUEST_CODE = 200

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val pdfMaker by lazy { PdfMakerManager(this) }

    private var pendingImageUris: List<Uri>? = null
    private var pendingDocUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        applyLicense()
        refreshPermissionStatus()

        binding.btnGenerateImagePdfMain.setOnClickListener {
            if (!canWriteToPublicDownloads()) {
                requestLegacyWritePermission()
                updateStatus(getString(R.string.status_need_storage_permission))
                return@setOnClickListener
            }
            pendingImageUris = null
            imagesResultLauncher.launch("image/*")
        }

        binding.btnGenerateWordPdfMain.setOnClickListener {
            if (!canWriteToPublicDownloads()) {
                requestLegacyWritePermission()
                updateStatus(getString(R.string.status_need_storage_permission))
                return@setOnClickListener
            }
            pendingDocUri = null
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword"
                    )
                )
            }
            docResultLauncher.launch(intent)
        }
    }

    private fun applyLicense() {
        /* Aspose license if needed:
        val lic = License()
        resources.openRawResource(R.raw.license).use { lic.setLicense(it) }
        */
    }

    private fun needsLegacyWritePermission(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    private fun canWriteToPublicDownloads(): Boolean = !needsLegacyWritePermission()
            || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestLegacyWritePermission() {
        if (!needsLegacyWritePermission()) return
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun refreshPermissionStatus() {
        if (!needsLegacyWritePermission()) {
            updateStatus(getString(R.string.status_ready_modern_storage))
            return
        }
        if (canWriteToPublicDownloads()) {
            updateStatus(getString(R.string.status_storage_granted))
        } else {
            updateStatus(getString(R.string.status_storage_required_tap_action))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST_CODE) return
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateStatus(getString(R.string.status_storage_granted))
            pendingImageUris?.let { uris ->
                pendingImageUris = null
                startImagesConversion(uris)
            }
            pendingDocUri?.let { uri ->
                pendingDocUri = null
                startDocConversion(uri)
            }
        } else {
            updateStatus(getString(R.string.status_permission_denied))
        }
    }

    private val imagesResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isEmpty()) {
            updateStatus(getString(R.string.status_no_images_selected))
            return@registerForActivityResult
        }
        if (!canWriteToPublicDownloads()) {
            pendingImageUris = uris
            requestLegacyWritePermission()
            updateStatus(getString(R.string.status_need_storage_permission))
            return@registerForActivityResult
        }
        startImagesConversion(uris)
    }

    private val docResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data
        if (uri == null) {
            updateStatus(getString(R.string.status_no_document_selected))
            return@registerForActivityResult
        }
        if (!canWriteToPublicDownloads()) {
            pendingDocUri = uri
            requestLegacyWritePermission()
            updateStatus(getString(R.string.status_need_storage_permission))
            return@registerForActivityResult
        }
        startDocConversion(uri)
    }

    private fun startImagesConversion(uris: List<Uri>) {
        binding.progressStatus.text = getString(R.string.status_converting_images)
        setWorking(true)
        updateStatus("")
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                pdfMaker.convertImagesToPdf(uris)
            }
            setWorking(false)
            when (result) {
                is PdfMakerManager.ConversionResult.Success -> {
                    binding.progressStatus.text = getString(R.string.status_idle)
                    updateStatus(getString(R.string.status_success_images, result.summary))
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_saved, result.summary),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is PdfMakerManager.ConversionResult.Failure -> {
                    binding.progressStatus.text = getString(R.string.status_idle)
                    updateStatus(getString(R.string.status_error, result.message))
                    Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startDocConversion(uri: Uri) {
        binding.progressStatus.text = getString(R.string.status_converting_word)
        setWorking(true)
        updateStatus("")
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                pdfMaker.convertDocToPdf(uri)
            }
            setWorking(false)
            when (result) {
                is PdfMakerManager.ConversionResult.Success -> {
                    binding.progressStatus.text = getString(R.string.status_idle)
                    updateStatus(getString(R.string.status_success_word, result.summary))
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_saved, result.summary),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is PdfMakerManager.ConversionResult.Failure -> {
                    binding.progressStatus.text = getString(R.string.status_idle)
                    updateStatus(getString(R.string.status_error, result.message))
                    Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setWorking(working: Boolean) {
        binding.progressBar.isVisible = working
        binding.btnGenerateImagePdfMain.isEnabled = !working
        binding.btnGenerateWordPdfMain.isEnabled = !working
    }

    private fun updateStatus(message: String) {
        binding.textStatus.text = message
        binding.textStatus.visibility = if (message.isBlank()) View.GONE else View.VISIBLE
    }
}
