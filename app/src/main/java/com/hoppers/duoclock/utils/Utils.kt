package com.hoppers.duoclock.utils

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object Utils {

    fun getTimeWithDate12h(): CharSequence {
        val time = SpannableString("hh:mm")
        val amPm = SpannableString(" a\n")
        val date = SpannableString("EEE, dd MMM")

        time.setSpan(RelativeSizeSpan(1.0f), 0, time.length, 0)
        amPm.setSpan(RelativeSizeSpan(0.8f), 0, amPm.length, 0)
        date.setSpan(RelativeSizeSpan(0.8f), 0, date.length, 0)

        return TextUtils.concat(time, amPm, date)
    }

    fun get24HoursFormat(): CharSequence {

        val span1 = SpannableString("HH:mm \n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        span4.setSpan(RelativeSizeSpan(0.80f), 0, 9, 0)

        return TextUtils.concat(span1, span4)
    }

    fun getDashBoard12HoursFormat(): CharSequence {
        val hours = SpannableString("\nhh")
        val minute = SpannableString(":mm")
        val second = SpannableString("\t\tss")
        val amPmMarker = SpannableString("\ta")
        val fullDate = SpannableString("E, dd MMM yyyy")

        hours.setSpan(RelativeSizeSpan(2.50f), 0, hours.length, 0)
        minute.setSpan(RelativeSizeSpan(2.50f), 0, minute.length, 0)
        second.setSpan(RelativeSizeSpan(1.50f), 0, second.length, 0)
        amPmMarker.setSpan(RelativeSizeSpan(1.00f), 0, amPmMarker.length, 0)
        fullDate.setSpan(RelativeSizeSpan(1.0f), 0, fullDate.length, 0)

        return TextUtils.concat(fullDate, hours, minute, amPmMarker)
    }

    fun getItem12HoursFormat(remoteTimeZone: String): CharSequence {
        val time = SpannableString("hh:mm a")
        val span2 = SpannableString("\n")
        val date = SpannableString("EEE, d MMM")

        span2.setSpan(RelativeSizeSpan(0.90f), 0, span2.length, 0)
        date.setSpan(RelativeSizeSpan(0.90f), 0, date.length, 0)

        val localDate = LocalDate.now(ZoneId.systemDefault())
        val remoteDate = LocalDate.now(ZoneId.of(remoteTimeZone))

        return if (remoteDate == localDate)
            TextUtils.concat(time)
        else
            TextUtils.concat(time, span2, date)
    }

    fun getItem24HoursFormat(): CharSequence {
        val span1 = SpannableString("HH:mm, ")
        val span2 = SpannableString("EEE, MMM d")
        return TextUtils.concat(span1, span2)
    }

    fun getDashBoard24HoursFormat(): CharSequence {
        val hours = SpannableString("\nHH")
        val minute = SpannableString(":mm")
        val second = SpannableString("\t\tss")
        val fullDate = SpannableString("E, dd MMM yyyy")

        hours.setSpan(RelativeSizeSpan(2.50f), 0, hours.length, 0)
        minute.setSpan(RelativeSizeSpan(2.50f), 0, minute.length, 0)
        second.setSpan(RelativeSizeSpan(1.50f), 0, second.length, 0)
        fullDate.setSpan(RelativeSizeSpan(1.0f), 0, fullDate.length, 0)

        return TextUtils.concat(fullDate, hours, minute)
    }


    fun getFormattedTime(
        timeZoneId: String,
        context: Context
    ): String {
        val pattern =
            if (android.text.format.DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm a"
        return ZonedDateTime
            .now(ZoneId.of(timeZoneId))
            .format(
                DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            )
    }

    fun getTimeDifferenceString(remoteZoneId: String?): String {
        if (remoteZoneId.isNullOrBlank()) {
            return ""
        }

        val localTimeZone = TimeZone.getDefault()
        val remoteTimeZone = try {
            TimeZone.getTimeZone(remoteZoneId)
        } catch (e: Exception) {
            return "" // Invalid zone ID
        }

        val now = Date().time
        val diffInMillis = (remoteTimeZone.getOffset(now) - localTimeZone.getOffset(now)).toLong()

        if (diffInMillis == 0L) {
            return ""
        }

        val hours = abs(TimeUnit.MILLISECONDS.toHours(diffInMillis))
        val minutes = abs(TimeUnit.MILLISECONDS.toMinutes(diffInMillis)) % 60

        val relation = if (diffInMillis >= 0) "ahead" else "behind"

        val parts = mutableListOf<String>()
        if (hours > 0) parts.add("$hours hr")
        if (minutes > 0) parts.add("$minutes min")

        if (parts.isEmpty()) return "Local time"

        return "${parts.joinToString(" ")} $relation"
    }
}
