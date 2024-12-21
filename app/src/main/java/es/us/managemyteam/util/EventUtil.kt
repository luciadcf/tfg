package es.us.managemyteam.util

import android.content.Context
import es.managemyteam.app.R

object EventUtil {

    fun getEventTypesList(context: Context): Array<String> {
        return arrayListOf<String>().apply {
            add(context.resources.getString(R.string.event_type_match))
            add(context.resources.getString(R.string.event_type_training))
            add(context.resources.getString(R.string.event_type_other))
        }.toTypedArray()
    }

}