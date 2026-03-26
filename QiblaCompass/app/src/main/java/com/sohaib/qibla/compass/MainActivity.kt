package com.sohaib.qibla.compass

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.sohaib.qibla.compass.base.BaseActivity
import com.sohaib.qibla.compass.databinding.ActivityMainBinding
import com.sohaib.qibla.compass.sensor.compass.Compass
import com.sohaib.qibla.compass.viewModel.ViewModelCompass
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModelCompass by viewModels<ViewModelCompass>()

    private var compass: Compass? = null
    private var currentAzimuth = 0f
    private var qiblaLocationSave = 0f
    private val qiblaLatitude = 21.422487
    private val qiblaLongitude = 39.826206

    private var latitude = 0.0
    private var longitude = 0.0
    private var isSensorSupported = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPadding()
        askForPermission()
        initCompass()
        initObservers()

        binding.mbRefreshLocation.setOnClickListener { askForPermission() }
    }

    private fun setPadding() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /** ----------------- Compass Setup ----------------- **/

    private fun initCompass() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        isSensorSupported = hasRequiredSensors(sensorManager)

        if (!isSensorSupported) {
            Toast.makeText(this, R.string.sensor_not_supported_body, Toast.LENGTH_SHORT).show()
            return
        }

        compass = Compass(sensorManager).apply {
            setListener(compassListener)
        }
    }

    private fun hasRequiredSensors(sensorManager: SensorManager): Boolean {
        val hasAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        return hasAccelerometer && hasMagneticField
    }

    /** ----------------- On Resume ----------------- **/

    override fun onResume() {
        super.onResume()
        startCompass()
    }

    private fun startCompass() {
        if (isSensorSupported && checkGpsEnabled() && checkLocationPermission()) {
            compass?.start()
        }
    }

    /** ----------------- Permissions ----------------- **/

    private fun askForPermission() {
        binding.mtvAddress.text = ""
        binding.mtvDegree.text = ""
        binding.mtvCurrentDegree.text = ""

        askLocationPermission {
            when (it) {
                true -> askEnableGPS { gps ->
                    when (gps) {
                        true -> viewModelCompass.fetchLocation()
                        false -> showToast(R.string.toast_permission_denied)
                    }
                }

                false -> showToast(R.string.toast_permission_denied)
            }
        }
    }

    /** ----------------- Observers ----------------- **/

    private fun initObservers() {
        viewModelCompass.locationLiveData.observe(this) { updateQiblaBearing(it) }
        viewModelCompass.addressLiveData.observe(this) { binding.mtvAddress.text = it }
        viewModelCompass.toastLiveData.observe(this) { showToast(it) }
    }

    private fun updateQiblaBearing(location: Location) {
        latitude = location.latitude
        longitude = location.longitude

        // Hide Qibla indicator if coordinates are invalid/zero
        val isInvalidLocation = latitude < 0.001 && longitude < 0.001
        binding.sivQibla.visibility = if (isInvalidLocation) View.INVISIBLE else View.VISIBLE

        if (isInvalidLocation) return

        // Bearing calculation
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(qiblaLatitude)
        val longDiffRad = Math.toRadians(qiblaLongitude - longitude)

        val y = sin(longDiffRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(longDiffRad)

        val bearing = ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
        qiblaLocationSave = bearing

        // Update UI
        val originalDegree = "${bearing.toInt()}° NE"
        binding.mtvDegree.text = originalDegree
        binding.sivQibla.visibility = View.VISIBLE
    }

    /** ----------------- Compass Rotation ----------------- **/

    private val compassListener = object : Compass.CompassListener {
        override fun onNewAzimuth(azimuth: Float, orientation: FloatArray) {
            rotateDial(azimuth)
            rotateQiblaArrow(azimuth)
        }
    }

    private fun rotateDial(azimuth: Float) {
        binding.sivCompass.startAnimation(
            RotateAnimation(
                -currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 500
                fillAfter = true
            }
        )
        currentAzimuth = azimuth
    }

    @SuppressLint("SetTextI18n")
    private fun rotateQiblaArrow(azimuth: Float) {
        val qiblaDirection = qiblaLocationSave
        val rotateAnimation = RotateAnimation(-currentAzimuth + qiblaDirection, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        currentAzimuth = azimuth
        rotateAnimation.duration = 500
        rotateAnimation.repeatCount = 0
        rotateAnimation.fillAfter = true
        binding.sivQibla.isVisible = qiblaDirection > 0
        binding.sivQibla.startAnimation(rotateAnimation)
        binding.mtvCurrentDegree.text = "${currentAzimuth.toInt()}°"
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        compass?.stop()
    }

    override fun onDestroy() {
        compass?.setListener(null)
        compass?.stop()
        super.onDestroy()
    }
}