package com.sohaib.qibla.compass.sensor.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Compass(private val sensorManager: SensorManager) : SensorEventListener {

    private var listener: CompassListener? = null
    private val gSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val mSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var azimuthFix = 0f

    /**
     * Starts the compass if location permissions are approved.
     */
    fun start() {
        sensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    /**
     * Stops the compass.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Resets the azimuth offset to zero.
     */
    fun resetAzimuthFix() {
        azimuthFix = 0f
    }

    /**
     * Sets a listener to receive azimuth updates.
     */
    fun setListener(listener: CompassListener?) {
        this.listener = listener
    }

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f
        synchronized(this) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                    mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                    mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                    mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                    mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
                }
            }

            val success = SensorManager.getRotationMatrix(
                ROTATION_ARRAY, IDENTITY_ARRAY, mGravity, mGeomagnetic
            )

            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(ROTATION_ARRAY, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + azimuthFix + 360) % 360

                listener?.onNewAzimuth(azimuth, orientation)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // No-op: Handle sensor accuracy changes if needed
    }

    interface CompassListener {
        fun onNewAzimuth(azimuth: Float, orientation: FloatArray)
    }

    companion object {
        private val ROTATION_ARRAY = FloatArray(9)
        private val IDENTITY_ARRAY = FloatArray(9)
    }
}