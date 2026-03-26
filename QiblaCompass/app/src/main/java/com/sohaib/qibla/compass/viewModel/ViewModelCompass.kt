package com.sohaib.qibla.compass.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.sohaib.qibla.compass.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "TAG_MyTag"

class ViewModelCompass(application: Application) : AndroidViewModel(application) {

    private val fusedLocationProvider by lazy { LocationServices.getFusedLocationProviderClient(application) }
    private val geocoder by lazy { Geocoder(application) }

    /* ---------------------------------- Location ---------------------------------- */

    private val _locationLiveData = MutableLiveData<Location>()
    val locationLiveData: LiveData<Location> get() = _locationLiveData

    private val _addressLiveData = MutableLiveData<String?>()
    val addressLiveData: LiveData<String?> get() = _addressLiveData

    private val _toastLiveData = MutableLiveData<Int>()
    val toastLiveData: LiveData<Int> get() = _toastLiveData

    @SuppressLint("MissingPermission")
    fun fetchLocation() = viewModelScope.launch(Dispatchers.IO) {
        fusedLocationProvider
            .lastLocation
            .addOnSuccessListener {
                Log.d(TAG, "ViewModelCompass: fetchLocation: addOnSuccessListener: $it")
                when (it != null) {
                    true -> findAddress(it)
                    false -> _toastLiveData.postValue(R.string.toast_failed_to_fetch_location)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "ViewModelCompass: fetchLocation: addOnFailureListener: Exception: ", it)
                _toastLiveData.postValue(R.string.toast_something_went_wrong)
            }
    }

    private fun findAddress(location: Location) = viewModelScope.launch(Dispatchers.IO) {
        _locationLiveData.postValue(location)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        addresses.firstOrNull()?.getAddressLine(0)?.let { address ->
                            _addressLiveData.postValue(address)
                        } ?: run {
                            _addressLiveData.postValue(null)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        _addressLiveData.postValue(null)
                    }
                })
            } else {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                addresses?.firstOrNull()?.getAddressLine(0)?.let { address ->
                    _addressLiveData.postValue(address)
                } ?: run {
                    _addressLiveData.postValue(null)
                }
            }
        } catch (ex: IOException) {
            Log.e(TAG, "ViewModelCompass: findAddress: IOException: ", ex)
            _addressLiveData.postValue(null)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "ViewModelCompass: findAddress: IllegalArgumentException: ", ex)
            _addressLiveData.postValue(null)
        }
    }
}