package es.us.managemyteam.extension

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import com.google.android.gms.maps.model.LatLng
import es.us.managemyteam.data.model.LocationBo
import java.util.TimeZone


private const val GOOGLE_MAPS_URL = "http://maps.google.com/maps?q="
private const val PREFIX_PHONE = "tel:"
private const val CALENDAR_ID = 3

fun Context.startUrlActionView(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Context.startGoogleMapsByLatLng(latLng: LatLng) {
    startUrlActionView(GOOGLE_MAPS_URL + latLng.toGoogleMapsFormat())
}

fun Context.startCalendarEvent(title: String?, dateInit: Long?, locationBo: LocationBo?) {
    val intent: Intent = Intent(Intent.ACTION_INSERT)
        .setData(Events.CONTENT_URI)
        .putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            dateInit
        )
        .putExtra(Events.TITLE, title)
        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
        .putExtra(Events.EVENT_LOCATION, locationBo?.address)

    startActivity(intent)
}

private fun getContentValues(
    title: String?,
    dateIni: Long?
): ContentValues {
    val values = ContentValues()
    dateIni?.let {
        values.put(CalendarContract.Events.DTSTART, dateIni)
        values.put(CalendarContract.Events.DTEND, dateIni)
    }
    values.put(CalendarContract.Events.TITLE, title ?: "")
    values.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID)
    values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)

    return values
}