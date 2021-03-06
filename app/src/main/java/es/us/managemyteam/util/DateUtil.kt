package es.us.managemyteam.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {
        private const val DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
        private const val DEFAULT_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
        private const val DATE_FORMAT_MONTH_COMPLETE_TEXT = "MMMM"
        private const val DATE_FORMAT_DAY_OF_WEEK_TEXT = "EEEE"
        private const val TIME_FORMAT = "HH:mm"
        private const val TIME_FORMAT_SECONDS = "HH:mm:ss"

        fun format(iso: String, format: String = DEFAULT_DATE_TIME_FORMAT): String {
            return format(parseIso(iso), format)
        }

        private fun format(timestamp: Long, format: String = DEFAULT_DATE_TIME_FORMAT): String {
            return format(Date().apply { time = timestamp }, format)
        }

        private fun format(calendar: Calendar, format: String = DEFAULT_DATE_TIME_FORMAT): String {
            return format(calendar.timeInMillis, format)
        }

        fun format(date: Date, format: String = DEFAULT_DATE_TIME_FORMAT): String {
            return SimpleDateFormat(format, Locale.getDefault()).format(date)
        }

        private fun parseIso(iso: String, isoFormat: String = DEFAULT_ISO_FORMAT): Date {
            return try {
                SimpleDateFormat(isoFormat, Locale.getDefault()).parse(iso)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Date()
            }
        }

        fun getDayFromMillis(millis: Long): Int {
            return getCalendarFromMillis(millis)[Calendar.DAY_OF_MONTH]
        }

        fun getMonthLabelFromMillis(millis: Long): String {
            return SimpleDateFormat(DATE_FORMAT_MONTH_COMPLETE_TEXT, Locale.getDefault()).format(
                getCalendarFromMillis(millis).time
            )
        }

        fun getTimeFromMillis(millis: Long): String {
            return SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(
                getCalendarFromMillis(
                    millis
                ).time
            )
        }

        private fun getCalendarFromMillis(millis: Long): Calendar {
            return Calendar.getInstance().apply {
                timeInMillis = millis
            }
        }

    }

}