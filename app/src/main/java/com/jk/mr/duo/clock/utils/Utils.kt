package com.jk.mr.duo.clock.utils

import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.widget.GridLayout

object Utils {

    fun get12HoursFormat(): CharSequence {

        val span1 = SpannableString("hh:mm")
        //  val span2 = SpannableString("ss")
        val span3 = SpannableString(" a\n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        //  span2.setSpan(RelativeSizeSpan(0.60f), 0, 2, 0)
        span3.setSpan(RelativeSizeSpan(0.50f), 0, 2, 0)
        span4.setSpan(RelativeSizeSpan(0.50f), 0, 9, 0)

        return TextUtils.concat(span1, span3, span4)
    }

    fun get24HoursFormat(): CharSequence {

        val span1 = SpannableString("HH:mm \n")
        //  val span2 = SpannableString("ss")
        //  val span3 = SpannableString("\n")
        //val span3 = SpannableString("a\n")
        val span4 = SpannableString("E, dd MMM")
        span1.setSpan(RelativeSizeSpan(1.00f), 0, 4, 0)
        //  span2.setSpan(RelativeSizeSpan(0.60f), 0, 2, 0)
        //   span3.setSpan(RelativeSizeSpan(0.40f), 0, 2, 0)
        span4.setSpan(RelativeSizeSpan(0.50f), 0, 9, 0)

        return TextUtils.concat(span1, span4)
    }


    fun getDashBoard12HoursFormat(): CharSequence {

        val span1 = SpannableString("hh:mm")
        val span2 = SpannableString(":ss ")
        val span3 = SpannableString("a\n")
        val span4 = SpannableString("E, dd MMMM yyyy")


        // span1.setSpan(UnderlineSpan(),0,span1.length,0)
        // span2.setSpan(UnderlineSpan(),0,span2.length,0)
        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        // span1.setSpan(SuperscriptSpan(), 0, span1.length, 0)
        // span2.setSpan(SubscriptSpan(), 0, span2.length, 0)
        span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)
        span3.setSpan(RelativeSizeSpan(0.50f), 0, span3.length, 0)


        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        //  span4.setSpan(SubscriptSpan(), 0, span4.length, 0)
        span4.setSpan(RelativeSizeSpan(0.40f), 0, span4.length, 0)



        return TextUtils.concat(span1, span2, span3, span4)

    }

    fun getItem12HoursFormat(): CharSequence {

        val span1 = SpannableString("hh:mm")
       // val span2 = SpannableString(" : ss ")
        val span3 = SpannableString(" a ")
        val span4 = SpannableString("\n E, dd MMMM yyyy")


        // span1.setSpan(UnderlineSpan(),0,span1.length,0)
        // span2.setSpan(UnderlineSpan(),0,span2.length,0)
        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        // span1.setSpan(SuperscriptSpan(), 0, span1.length, 0)
        // span2.setSpan(SubscriptSpan(), 0, span2.length, 0)
      //  span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)
        span3.setSpan(RelativeSizeSpan(0.50f), 0, span3.length, 0)


        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        //  span4.setSpan(SubscriptSpan(), 0, span4.length, 0)
        span4.setSpan(RelativeSizeSpan(0.50f), 0, span4.length, 0)

       // val complete = TextUtils.concat(span1, span2, span3, span4)
       // val cs = SpannableString(complete)

        //val start = span1.length + span2.length + span3.length
        span4.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
                0, span4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



        return  TextUtils.concat(span1, span3, span4)

    } fun getItem24HoursFormat(): CharSequence {

        val span1 = SpannableString("HH:mm")
       // val span2 = SpannableString(" : ss ")
       // val span3 = SpannableString("a ")
        val span4 = SpannableString("\n E, dd MMMM yyyy")


        // span1.setSpan(UnderlineSpan(),0,span1.length,0)
        // span2.setSpan(UnderlineSpan(),0,span2.length,0)
        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        // span1.setSpan(SuperscriptSpan(), 0, span1.length, 0)
        // span2.setSpan(SubscriptSpan(), 0, span2.length, 0)
       // span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)
       // span3.setSpan(RelativeSizeSpan(0.50f), 0, span3.length, 0)


        // span3.setSpan(UnderlineSpan(),0,span3.length,0)

        //  span4.setSpan(SubscriptSpan(), 0, span4.length, 0)
        span4.setSpan(RelativeSizeSpan(0.50f), 0, span4.length, 0)

       // val complete = TextUtils.concat(span1, span2, span3, span4)
       // val cs = SpannableString(complete)

        //val start = span1.length + span2.length + span3.length
        span4.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
                0, span4.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



        return  TextUtils.concat(span1,  span4)

    }

    fun getDashBoard24HoursFormat(): CharSequence {

        val span1 = SpannableString("HH:mm")
        val span2 = SpannableString(":ss\n ")
        val span4 = SpannableString("E, dd MMMM yyyy")
        span2.setSpan(RelativeSizeSpan(0.50f), 0, span2.length, 0)

        span4.setSpan(RelativeSizeSpan(0.35f), 0, span4.length, 0)

        return TextUtils.concat(span1, span2, span4)
    }
}