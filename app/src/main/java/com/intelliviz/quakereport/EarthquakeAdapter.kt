package com.intelliviz.quakereport

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.earthquake_list_item.view.*


class EarthquakeAdapter(val context: Context, val items: MutableList<Earthquake>) :
        RecyclerView.Adapter<EarthquakeAdapter.ViewHolder>() {
    private val earthquakes: MutableList<Earthquake> = items
    private lateinit var listener: GetEarthQuakeDataAsyncTask.OnEarthquakeLoadListener

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


        val magnitudeTextView = view.magnitude
        val distanceTextView = view.distance
        val cityTextView = view.city
        val dateTextView = view.date
        val timeTextView = view.time
        var earthquake: Earthquake? = null

        override fun onClick(p0: View?) {
            val intent = Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(earthquake!!.url))
            startActivity(context, intent, null)
        }

        init {
            view.touchables
            view.setOnClickListener(this)
        }

        fun bind(earthquake: Earthquake, context: Context) {
            this.earthquake = earthquake
            magnitudeTextView.text = earthquake.magnitude.toString()
            distanceTextView.text = earthquake.distance
            cityTextView.text = earthquake.city
            dateTextView.text = earthquake.date
            timeTextView.text = earthquake.time
            val magnitudeCircle = magnitudeTextView.getBackground() as GradientDrawable
            var magnitudeColor = getMagnitudeColor(context, earthquake.magnitude)
            magnitudeCircle.setColor(magnitudeColor)
        }

        private fun getMagnitudeColor(context: Context, magnitude: Double): Int {

            var colorIndex: Int
            if(magnitude <= 1.0) {
                colorIndex = R.color.magnitude1
            } else if(magnitude <= 2.0) {
                colorIndex = R.color.magnitude2
            } else if(magnitude <= 3.0) {
                colorIndex = R.color.magnitude3
            } else if(magnitude <= 4.0) {
                colorIndex = R.color.magnitude4
            } else if(magnitude <= 5.0) {
                colorIndex = R.color.magnitude5
            } else if(magnitude <= 6.0) {
                colorIndex = R.color.magnitude6
            } else if(magnitude <= 7.0) {
                colorIndex = R.color.magnitude7
            } else if(magnitude <= 8.0) {
                colorIndex = R.color.magnitude8
            } else if(magnitude <= 9.0) {
                colorIndex = R.color.magnitude9
            } else {
                colorIndex = R.color.magnitude10plus
            }

            return ContextCompat.getColor(context, colorIndex)
        }
    }
}