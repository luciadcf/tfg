package es.us.managemyteam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MessageBo(
    val message: String = "",
    val fromId: String = "",
    val fromName: String = "",
    val date: Date = Date()
) : BaseBo(), Parcelable