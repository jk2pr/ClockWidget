package com.jk.mr.duo.clock.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jk.mr.duo.clock.R
import java.util.*


class TextClock : LinearLayout {
    private var mTime: Calendar? = null
    private var tvHour: TextView? = null
    private var tvMinute: TextView? = null
    private var tvDay: TextView? = null
    private var tvMonth: TextView? = null
    private var tvSecond: TextView? = null
    private var tvMeridian: TextView? = null
    private var is24HourFormat: Boolean = false

    constructor(context: Context) : super(context)



    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    /*constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr)
    }*/

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        createTime(TimeZone.getDefault().id)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TextClock, defStyleAttr, 0)

        LayoutInflater.from(context).inflate(R.layout.text_clock_layout, this)

        tvHour = findViewById(R.id.hour)
        tvMinute = findViewById(R.id.minute)
        tvDay = findViewById(R.id.day)
        tvMonth = findViewById(R.id.month)
        tvMeridian = findViewById(R.id.meridian)
        tvSecond = findViewById(R.id.second)
        val color: Int

        val showSecond: Boolean
        try {
            color = a.getColor(R.styleable.TextClock_color, Color.BLACK)
            is24HourFormat = a.getBoolean(R.styleable.TextClock_format24Hour, false)
            showSecond = a.getBoolean(R.styleable.TextClock_showSecond, true)
        } finally {
            a.recycle()
        }

        setColor(color)

        if (!showSecond)
            tvSecond!!.visibility = View.GONE

        setTime()

        Handler().postDelayed(object : Runnable {
            override fun run() {
                setTime()
                if (handler != null)
                    handler.postDelayed(this, DALEY.toLong())
            }
        }, DALEY.toLong())
    }

    private fun setColor(color: Int) {
        tvHour!!.setTextColor(color)
        tvMinute!!.setTextColor(color)
        tvDay!!.setTextColor(color)
        tvMonth!!.setTextColor(color)
        tvMeridian!!.setTextColor(color)
        tvSecond!!.setTextColor(color)
        findViewById<View>(R.id.separador).setBackgroundColor(color)
    }

    private fun createTime(timeZone: String?) {
        if (timeZone != null)
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
        else
            mTime = Calendar.getInstance()
    }

    private fun setTime() {
        mTime!!.timeInMillis = System.currentTimeMillis()

        tvHour!!.text = (if (is24HourFormat)
            mTime!!.get(Calendar.HOUR_OF_DAY)
        else
            mTime!!.get(Calendar.HOUR)).toString()

        tvMinute!!.text = DateFormat.format("mm", mTime)
        tvSecond!!.text = mTime!!.get(Calendar.SECOND).toString()
        tvMeridian!!.text = DateFormat.format("a", mTime)

        tvDay!!.text = mTime!!.get(Calendar.DAY_OF_MONTH).toString()
        tvMonth!!.text = DateFormat.format("MMM", mTime)
    }

    companion object {

        private const val DALEY = 1000
    }
}