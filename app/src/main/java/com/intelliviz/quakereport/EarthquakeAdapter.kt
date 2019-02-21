package com.intelliviz.quakereport

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intelliviz.quakereport.QueryUtils.getMagnitudeColor
import kotlinx.android.synthetic.main.earthquake_list_item.view.*


class EarthquakeAdapter(val context: Context, val items: MutableList<Earthquake>) :
        RecyclerView.Adapter<EarthquakeAdapter.ViewHolder>() {
    private val earthquakes: MutableList<Earthquake> = items
    //private lateinit var listener: GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.earthquake_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return earthquakes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val earthquake = getItem(position)
        holder.bind(earthquake, context)
    }

    fun addAll(earthquakes: List<Earthquake>) {
        this.earthquakes.clear()
        this.earthquakes.addAll(earthquakes)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Earthquake {
        return earthquakes[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val magnitudeTextView = view.magnitude
        private val distanceTextView = view.distance
        private val cityTextView = view.city
        private val dateTextView = view.date
        private val timeTextView = view.time
        private var earthquake: Earthquake? = null

        override fun onClick(p0: View?) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(earthquake!!.url)
            startActivity(context, intent, null)
        }

        init {
            view.touchables
            view.setOnClickListener(this)
        }

        fun bind(earthquake: Earthquake, context: Context) {
            this.earthquake = earthquake
            val str:String = "%2.1f".format(earthquake.magnitude)
            magnitudeTextView.text = str
            distanceTextView.text = earthquake.distance
            cityTextView.text = earthquake.city
            dateTextView.text = earthquake.date
            timeTextView.text = earthquake.time
            val magnitudeCircle = magnitudeTextView.background as GradientDrawable
            val magnitudeColor = getMagnitudeColor(context, earthquake.magnitude)
            magnitudeCircle.setColor(magnitudeColor)
        }
    }
}