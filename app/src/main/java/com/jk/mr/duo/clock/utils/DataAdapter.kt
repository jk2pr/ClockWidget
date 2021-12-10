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

class DataAdapter(
    private val activity: AppWidgetConfigureActivity,
    dataSet: List<CalData> = emptyList(),
    val updateClock: (CalData) -> Unit
) : DragDropSwipeAdapter<CalData, DataAdapter.ViewHolder>(dataSet) {

    fun addCal(calData: CalData) {
        insertItem(0, calData)
        Looper.myLooper()?.let { it -> Handler(it).post { updateSelection() } }
        updateClock(calData)
    }

    override fun getViewHolder(itemView: View): ViewHolder = ViewHolder(itemView)

    override fun onBindViewHolder(item: CalData, viewHolder: ViewHolder, position: Int) =
        viewHolder.bind(item)

    inner class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {

        val dragIcon: View = itemView.findViewById(R.id.drag_icon)
        private val textClock: TextClock = itemView.tv_time
        private val imgFlag: ImageView = itemView.img_flag
        private val textCity: TextView = itemView.tv_city
        private val textCountry: TextView = itemView.tv_country
        val bebaBoldTypeFace = UiUtils.getBebasneueRegularTypeFace(itemView.context)
        val abelRegularTypeFace = UiUtils.getAbelRegularTypeFace(itemView.context)

        fun bind(calData: CalData): Unit = with(itemView) {

            root_constraint.isSelected = calData.isSelected
            textClock.apply {
                timeZone = calData.currentCityTimeZoneId?.trim()
                format12Hour = Utils.getItem12HoursFormat()
                format24Hour = Utils.getItem24HoursFormat()
                typeface = abelRegularTypeFace
            }
            textCountry.text = calData.name
            textCountry.typeface = abelRegularTypeFace

            textCity. typeface = bebaBoldTypeFace
            textCity.text = calData.address

            SvgLoader.pluck()
                .with(activity)
                .setPlaceHolder(R.drawable.ic_image_black_24dp, R.drawable.ic_broken_image_black_24dp)
                .load(calData.flag ?: "", imgFlag)
        }
    }

    private fun updateSelection() {
        (dataSet as MutableList).forEachIndexed { index, element ->
            run {
                element.isSelected = index == 0
                notifyItemChanged(index, element.isSelected)
            }
        }

        updateClock(dataSet[0])
    }
    override fun onDragFinished(item: CalData, viewHolder: ViewHolder) {
        super.onDragFinished(item, viewHolder)
        updateSelection()
    }

    override fun canBeSwiped(item: CalData, viewHolder: ViewHolder, position: Int): Boolean = position != 0

    override fun getViewToTouchToStartDraggingItem(item: CalData, viewHolder: ViewHolder, position: Int) = viewHolder.dragIcon
}
