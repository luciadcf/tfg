package es.us.managemyteam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

open class BaseBo(
    var uuid: String? = null
)

@Parcelize
data class LocationBo(
    val location: MyLatLng? = null,
    val address: String? = null
) : Parcelable

@Parcelize
data class MyLatLng(
    val lat: Double = 0.0,
    val long: Double = 0.0
) : Parcelable