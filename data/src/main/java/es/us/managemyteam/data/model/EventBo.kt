package es.us.managemyteam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class EventBo(
    var title: String? = null,
    var date: Date? = null,
    var location: LocationBo? = null,
    var description: String? = null,
    var call: CallBo? = null,
    var eventType: String? = null
) : BaseBo(), Parcelable
