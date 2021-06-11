package com.namnguyen.myalarm.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT =
    SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US)
object DateTimeUtils {

    fun Long.formatDate(): String {
        try {
            return DATE_FORMAT.format(this.times(1))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}