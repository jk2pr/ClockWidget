package com.hoppers.duoclock.utils

import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import com.hoppers.duoclock.utils.Constants.TAG
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object Utils {

    fun get12HoursFormat(): CharSequence {
        val span1 = SpannableString("hh:mm")
        //  val span2 = SpannableString("ss")
        val span3 = SpannableString(" a\n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        //  span2.setSpan(RelativeSizeSpan(0.60f), 0, 2, 0)
        span3.setSpan(RelativeSizeSpan(0.80f), 0, 2, 0)
        span4.setSpan(RelativeSizeSpan(0.80f), 0, 9, 0)

        return TextUtils.concat(span1, span3, span4)
    }

    fun get24HoursFormat(): CharSequence {
        val span1 = SpannableString("HH:mm \n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        span4.setSpan(RelativeSizeSpan(0.80f), 0, 9, 0)

        return TextUtils.concat(span1, span4)
    }

    fun getDashBoard12HoursFormat(): CharSequence {
        val span1 = SpannableString("hh:mm")
        val span2 = SpannableString(":ss ")
        val span3 = SpannableString("a\n")
        val span4 = SpannableString("E, dd MMMM yyyy")

        span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)
        span3.setSpan(RelativeSizeSpan(0.50f), 0, span3.length, 0)
        span4.setSpan(RelativeSizeSpan(0.50f), 0, span4.length, 0)

        return TextUtils.concat(span1, span2, span3, span4)
    }

    fun getItem12HoursFormat(): CharSequence {
        val span1 = SpannableString("hh:mm a")
        val span2 = SpannableString(", ")
        val span3 = SpannableString("EEE, MMM d")

        span2.setSpan(RelativeSizeSpan(0.90f), 0, span2.length, 0)
        span3.setSpan(RelativeSizeSpan(0.90f), 0, span3.length, 0)

        return TextUtils.concat(span1, span2, span3)
    }

    fun getItem24HoursFormat(): CharSequence {
        val span1 = SpannableString("HH:mm, ")
        val span2 = SpannableString("EEE, MMM d")
        return TextUtils.concat(span1, span2)
    }

    fun getDashBoard24HoursFormat(): CharSequence {
        val span1 = SpannableString("HH:mm")
        val span2 = SpannableString(":ss\n ")
        val span4 = SpannableString("E, dd MMMM yyyy")
        span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)

        span4.setSpan(RelativeSizeSpan(0.35f), 0, span4.length, 0)

        return TextUtils.concat(span1, span2, span4)
    }


    fun getFormattedTime(
        timeZoneId: String,
        is24Hour: Boolean = true
    ): String {
        val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
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
            return "Local time"
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
