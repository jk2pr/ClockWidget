package com.jk.mr.duo.clock.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import com.ahmadrosid.svgloader.SvgLoader
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.data.caldata.CalData
import kotlinx.android.synthetic.main.item_cal_layout.view.*

class DataAdapter(private val activity: AppWidgetConfigureActivity, dataSet: List<CalData> = emptyList(), val addCallback: (CalData) -> Unit) : DragDropSwipeAdapter<CalData, DataAdapter.ViewHolder>(dataSet) {

    fun addCal(calData: CalData) {
        insertItem(0, calData)
        Looper.myLooper()?.let { it -> Handler(it).post { updateSelection() } }
        addCallback(calData)
    }

    fun removeAt(position: Int) {
        removeItem(position)
    }
    override fun getViewHolder(itemView: View): ViewHolder = ViewHolder(itemView)

    override fun onBindViewHolder(item: CalData, viewHolder: ViewHolder, position: Int) = viewHolder.bind(item)

    inner class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {

        val dragIcon: View = itemView.findViewById(R.id.drag_icon)
        private val textClock: TextClock = itemView.tv_time
        private val imgFlag: ImageView = itemView.img_flag
        private val textCity: TextView = itemView.tv_city
        private val textCountry: TextView = itemView.tv_country

        fun bind(calData: CalData): Unit = with(itemView) {

            root_constraint.isSelected = calData.isSelected
            textClock.apply {
                timeZone = calData.currentCityTimeZone.trim()
                format12Hour = Utils.getItem12HoursFormat()
                format24Hour = Utils.getItem24HoursFormat()
            }
            textCountry.apply {

                var final: String = calData.name
                val dd = final.split(" ".toRegex(), 3)
                if (dd.size > 1) final = dd.first().plus(" ").plus(dd[1])
                text = final
            }
            textCity.text = calData.address

            SvgLoader.pluck()
                .with(activity)
                .setPlaceHolder(R.drawable.ic_image_black_24dp, R.drawable.ic_broken_image_black_24dp)
                .load(calData.flag, imgFlag)
        }
    }

    private fun updateSelection() {
        (dataSet as MutableList).forEachIndexed { index, element -> element.isSelected = index == 0 }
        notifyDataSetChanged()
    }
    override fun onDragFinished(item: CalData, viewHolder: ViewHolder) {
        super.onDragFinished(item, viewHolder)
        updateSelection()
        addCallback(item)
    }

    override fun canBeDragged(item: CalData, viewHolder: ViewHolder, position: Int): Boolean = position != 0
    override fun canBeSwiped(item: CalData, viewHolder: ViewHolder, position: Int): Boolean = position != 0

    override fun getViewToTouchToStartDraggingItem(item: CalData, viewHolder: ViewHolder, position: Int) = viewHolder.dragIcon
}
