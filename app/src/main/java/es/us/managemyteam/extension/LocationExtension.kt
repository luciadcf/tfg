package es.us.managemyteam.extension

import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import es.managemyteam.app.R
import es.us.managemyteam.data.model.MyLatLng
import es.us.managemyteam.ui.view.common_map.MapView
import es.us.managemyteam.ui.view.common_map.MarkerItemVo

fun Location.toLatLng() = LatLng(latitude, longitude)

fun MyLatLng.getMarkerItemVo(mapView: MapView) = MarkerItemVo(
    position = LatLng(this.lat, this.long),
    icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_poi),
    title = mapView.getAddressFromMarkerPosition(LatLng(this.lat, this.long))
)

fun LatLng.toMyLatLng() = MyLatLng(latitude, longitude)

fun MyLatLng.toLatLng() = LatLng(lat, long)

fun LatLng.toGoogleMapsFormat(): String {
    return "$latitude,$longitude"
}