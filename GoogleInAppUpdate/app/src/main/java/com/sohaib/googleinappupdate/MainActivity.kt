package com.sohaib.googleinappupdate

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.install.model.AppUpdateType
import com.sohaib.googleinappupdate.databinding.ActivityMainBinding
import com.sohaib.googleinappupdate.manager.AppUpdateManager

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val appUpdateManager = AppUpdateManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkForUpdate()
    }

    /**
     *    Types of AppUpdate:
     *     -> AppUpdateType.IMMEDIATE
     *     -> AppUpdateType.FLEXIBLE
     */

    private fun checkForUpdate() {
        appUpdateManager.setUpdateType(AppUpdateType.IMMEDIATE)
        appUpdateManager.checkForUpdate { isAvailable, message ->
            binding.tvText.text = message
            if (isAvailable) {
                requestForUpdate()
            }
        }
    }

    private fun requestForUpdate() {
        appUpdateManager.requestForUpdate { isUpdated, message ->
            binding.tvText.text = message
            if (isUpdated) {
                // Proceed with Code...
                Log.d("TAG", "requestForUpdate: Running App...")
            }
        }
    }

    // Checks that the update is not stalled during 'onResume()'
    override fun onResume() {
        super.onResume()
        appUpdateManager.checkIfUpdateInstalled()
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}