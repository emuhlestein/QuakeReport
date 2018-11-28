package com.intelliviz.quakereport

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.earthquake_list_item.view.*

class EarthquakeAdapter(val context: Context, val items: MutableList<Earthquake>) :
        RecyclerView.Adapter<EarthquakeAdapter.ViewHolder>() {
    private val earthquakes: MutableList<Earthquake> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.earthquake_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return earthquakes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val earthquake = getItem(position)
        holder.magnitudeTextView.text = earthquake.magnitude.toString()
        holder.locationTextView.text = earthquake.location
        holder.dateTextView.text = earthquake.date
    }

    fun addAll(earthquakes: List<Earthquake>) {
        this.earthquakes.clear()
        this.earthquakes.addAll(earthquakes)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Earthquake {
        return earthquakes[position]
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val magnitudeTextView = view.magnitude
        val locationTextView = view.location
        val dateTextView = view.date
    }
}