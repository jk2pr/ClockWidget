package com.jk.mr.duo.clock.utils

import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan

object Utils {

    fun getSpannable(): CharSequence {

        val span1 = SpannableString("hh:mm ")
      //  val span2 = SpannableString("ss")
        val span3 = SpannableString("a\n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        //  span2.setSpan(RelativeSizeSpan(0.60f), 0, 2, 0)
        span3.setSpan(RelativeSizeSpan(0.40f), 0, 2, 0)
        span4.setSpan(RelativeSizeSpan(0.60f), 0, 9, 0)

        return TextUtils.concat(span1, span3, span4)
    }
}