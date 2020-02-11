package com.jk.mr.duo.clock.utils

import android.graphics.Typeface
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.data.caldata.CalData
import kotlinx.android.synthetic.main.item_cal_layout.view.*
import java.util.*


class DataAdapter(private val activity: AppWidgetConfigureActivity
                  ,  val listener: (CalData) -> Unit) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    private lateinit var mRecyclerView: RecyclerView
    val data = LinkedList<CalData>()

    fun addCal(calData: CalData) {
        calData.isSelected = true
        data.add(0, calData)
        notifyItemInserted(0)
        Handler().post {
            data.forEach {
                it.isSelected = it == calData
            }
            notifyDataSetChanged()
        }
        listener.invoke(calData)
        mRecyclerView.scrollToPosition(0)
    }

    fun removeAt(position: Int) {

        data.removeAt(position)
        notifyItemRemoved(position)

    }


    private fun moveToTop(calData: CalData) {
        val currentPosition = data.indexOf(calData)
        if (currentPosition == 0)
            return

        data.forEach {
            it.isSelected = it == calData
        }
        notifyDataSetChanged()

        mRecyclerView.post {
            data.remove(calData)
            data.addFirst(calData)
            val zeroViewHolder = mRecyclerView.findViewHolderForAdapterPosition(0)
            zeroViewHolder?.itemView?.isClickable = false

            val previousViewHolder = mRecyclerView.findViewHolderForAdapterPosition(currentPosition)
            previousViewHolder?.itemView?.isClickable = true


            notifyItemChanged(0, zeroViewHolder?.itemView?.isClickable)
            notifyItemChanged(currentPosition, zeroViewHolder?.itemView?.isClickable)

            notifyItemMoved(currentPosition, 0)

            mRecyclerView.scrollToPosition(0)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cal_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val bebasneueRegularTypeFace by lazy {

        Constants.getBebasneueRegularTypeFace(activity)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calData = data[position]
        holder.bind(calData, listener)


    }

    fun addAll(calData: List<CalData>) {
        data.clear()
        data.addAll(calData)
        notifyDataSetChanged()
       // notifyItemRangeInserted(0, data.size)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val rootConstraint: ConstraintLayout = itemView.root_constraint
        private val textClock: TextClock = itemView.tv_time
        private val imgFlag: ImageView = itemView.img_flag
        private val textCity: TextView = itemView.tv_city
        private val textCountry: TextView = itemView.tv_country


        //private val textColor = android.R.color.white
        //  private val tint = activity.getTintFromTheme(activity.getThemePref())

        fun bind(calData: CalData, listener: (CalData) -> Unit) = with(itemView) {

            root_constraint.isSelected = calData.isSelected
            tag = calData
            // viewSprt.setBackgroundColor(ContextCompat.getColor(activity, tint))
            textClock.apply {
                timeZone = calData.currentCityTimeZone.trim()
                format12Hour = Utils.getItem12HoursFormat()
               // typeface = bebasneueRegularTypeFace
                // setTextColor(ContextCompat.getColor(activity, tint)) America/Toronto
                format24Hour = Utils.getItem24HoursFormat()
            }
            textCountry.apply {

                var final: String = calData.name
                val dd = final.split(" ".toRegex(), 3)
                if (dd.size > 1)
                    final = dd.first().plus(" ").plus(dd[1])
                text = final
                //  setTextColor(ContextCompat.getColor(activity, tint))
              //  typeface = bebasneueRegularTypeFace
            }
            textCity.apply {
                text = calData.address
              //  typeface = bebasneueRegularTypeFace
                // setTextColor(ContextCompat.getColor(activity, tint))
            }
            val url = calData.flag
            SvgLoader.pluck()
                    .with(activity)
                    .setPlaceHolder(R.drawable.ic_image_black_24dp, R.drawable.ic_broken_image_black_24dp)
                    .load(url, imgFlag)
            isClickable = (adapterPosition != 0)
            if (isClickable)
                setOnClickListener {
                    rootConstraint.isSelected = true
                    moveToTop(calData)
                    listener(calData)
                }
        }

    }

}

