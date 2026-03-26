package com.sohaib.qibla.compass.base

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    private var callback: ((Boolean) -> Unit)? = null
    private var permissionName: String? = null
    private var permissionArray = arrayOf<String>()

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val values: Collection<Boolean> = it.values
        val contains = values.contains(true)
        callback?.invoke(contains)
    }

    private var settingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        permissionName?.let { permissionLauncher.launch(permissionArray) }
    }

    private var gpsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        callback?.invoke(checkGpsEnabled())
    }

    protected fun checkPermissionStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return true
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    protected fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    protected fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    protected fun askStoragePermission(callback: (Boolean) -> Unit) {
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), "Allow this app to access storage.", callback)
    }

    protected fun askAudioPermission(callback: (Boolean) -> Unit) {
        askPermission(Manifest.permission.RECORD_AUDIO, arrayOf(Manifest.permission.RECORD_AUDIO), "Allow this app to Record Audio.", callback)
    }

    protected fun askLocationPermission(callback: (Boolean) -> Unit) {
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), "Allow this app to access your location.", callback)
    }

    private fun askPermission(permission: String, permissions: Array<String>, message: String, callback: (Boolean) -> Unit) {
        this.callback = callback
        this.permissionName = permission
        this.permissionArray = permissions

        val checkPermission = when (permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> checkPermissionStorage()
            Manifest.permission.RECORD_AUDIO -> checkAudioPermission()
            Manifest.permission.ACCESS_FINE_LOCATION -> checkLocationPermission()
            else -> false
        }
        if (checkPermission) {
            callback.invoke(true)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionDialog(message, permissions)
            } else {
                if (sharedPreferences == null) {
                    sharedPreferences = getSharedPreferences("permission_preferences", MODE_PRIVATE)
                }
                if (sharedPreferences?.getBoolean(permission, true) == true) {
                    sharedPreferences?.edit { putBoolean(permission, false) }
                    permissionLauncher.launch(permissions)
                } else {
                    showSettingDialog()
                }
            }
        }
    }

    @Synchronized
    private fun showPermissionDialog(message: String, permissions: Array<String>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialogInterface, _ ->
                dialogInterface.dismiss()
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton("Deny") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission required")
            .setMessage("Allow permission from 'Settings' to proceed")
            .setCancelable(false)
            .setPositiveButton("Setting") { dialogInterface, _ ->
                dialogInterface.dismiss()
                openSettingPage()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun openSettingPage() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        settingLauncher.launch(intent)
    }

    /* ---------------------------------------- GPS ---------------------------------------- */

    protected fun checkGpsEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }

    protected fun askEnableGPS(callback: (Boolean) -> Unit) {
        if (checkGpsEnabled()) {
            callback.invoke(true)
            return
        }

        this.callback = callback
        MaterialAlertDialogBuilder(this)
            .setTitle("Enable GPS")
            .setMessage("Location services are required for this feature. Please enable GPS.")
            .setCancelable(false)
            .setPositiveButton("Enable") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                gpsLauncher.launch(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}