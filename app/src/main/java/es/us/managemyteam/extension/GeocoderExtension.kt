package es.us.managemyteam.extension

import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng

private const val MAX_RESULTS = 10

fun Geocoder.getLatLngOfAddress(address: String): LatLng? {
    var result: LatLng? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocationName(
                address, MAX_RESULTS
            ) { addresses ->
                result = addresses.firstOrNull()?.let {
                    LatLng(it.latitude, it.longitude)
                }
            }
        } else {
            getFromLocationName(address, MAX_RESULTS)?.firstOrNull()?.let {
                result = LatLng(it.latitude, it.longitude)
            }
        }

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return result
}


fun Geocoder.getAddressOfLatLng(latLng: LatLng?): String? {
    var address: String? = null
    latLng?.let {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    MAX_RESULTS
                ) { addresses -> address = addresses.firstOrNull()?.getAddressLine(0) }
            } else {
                address = getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    MAX_RESULTS
                )?.firstOrNull()?.getAddressLine(0)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    return address
}