package com.jk.mr.duo.clock

import android.content.Context
import android.util.AttributeSet
import android.widget.TextClock

class JText : TextClock {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


}