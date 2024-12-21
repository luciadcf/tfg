package es.us.managemyteam.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import es.us.managemyteam.constant.Permission.REQUEST_CODE
import es.us.managemyteam.contract.LocationInterface
import es.us.managemyteam.extension.getAddressOfLatLng
import es.us.managemyteam.extension.getLatLngOfAddress
import es.us.managemyteam.extension.toLatLng
import es.us.managemyteam.util.PermissionUtil
import java.util.Locale
import java.util.concurrent.TimeUnit

private val INTERVAL = TimeUnit.SECONDS.toMillis(10L)
private val FASTEST_INTERVAL = TimeUnit.SECONDS.toMillis(5L)

@Suppress("DEPRECATION")
class LocationManager(appContext: Context) : LocationInterface,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val geocoder = Geocoder(appContext, Locale.getDefault())
    private val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationRequest: LocationRequest? = null
    private var client: GoogleApiClient? = null
    private var enabled = false
    private var currentLocation: Location? = null
    private val locationData = MutableLiveData<LatLng>()

    override fun checkGrantedPermissions(permissions: IntArray): Boolean {
        return PermissionUtil.isPermissionGranted(REQUEST_CODE, permissions)
    }

    override fun checkNeedPermissions(context: Context): Boolean {
        return PermissionUtil.needPermission(context, *permissions)
    }

    override fun requestPermission(fragment: Fragment) {
        PermissionUtil.requestPermission(fragment, *permissions)
    }

    override fun onCreate(context: Context) {
        enabled = isGooglePlayServicesAvailable(context)
        if (enabled) {
            setupLocationRequest()
            setupClient(context)
        }
    }

    override fun onStart(context: Context) {
        if (enabled && client?.isConnected == true && !checkNeedPermissions(context)) {
            startLocationUpdates()
        }
    }

    override fun onStop() {
        if (enabled) {
            stopLocationUpdates()
        }
    }

    override fun getCurrentLocationData(): LiveData<LatLng> {
        return locationData
    }

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): LatLng? {
        try {
            val isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            var location: Location? = null
            if (isGPSEnabled || isNetworkEnabled) {
                if (isGPSEnabled) {
                    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }

                if (isNetworkEnabled) {
                    location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                location?.let {
                    currentLocation = it
                }
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
        val location = currentLocation?.toLatLng()

        location?.let {
            locationData.value = it
        }

        return location
    }

    override fun getAddressOfLatLng(latLng: LatLng): String? {
        return geocoder.getAddressOfLatLng(latLng)
    }

    override fun getLatLngOfAddress(address: String): LatLng? {
        return geocoder.getLatLngOfAddress(address)
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun setupClient(context: Context) {
        client = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
    }

    private fun startLocationUpdates() {
        try {
            if (client != null && locationRequest != null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    client!!,
                    locationRequest!!,
                    this
                )
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        try {
            client?.let {
                LocationServices.FusedLocationApi.removeLocationUpdates(it, this)
            }
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS == status
    }

    // GoogleApiClient.ConnectionCallbacks

    override fun onConnected(extras: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
        // Nothing to do
    }

    // GoogleApiClient.OnConnectionFailedListener

    override fun onConnectionFailed(error: ConnectionResult) {
        // Nothing to do
    }

    // LocationListener

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        locationData.value = location.toLatLng()
    }

}

inline fun <T : Any> guardLet(vararg elements: T?, closure: () -> Nothing): List<T> {
    return if (elements.all { it != null }) {
        elements.filterNotNull()
    } else {
        closure()
    }
}

inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}