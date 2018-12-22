package com.intelliviz.quakereport.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.intelliviz.quakereport.EarthquakeAdapter
import com.intelliviz.quakereport.EarthquakeViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.db.Earthquake
import java.util.*

class EarthquakeRangeFragment: Fragment() {
    private lateinit var viewModel: EarthquakeViewModel
    companion object {

        fun newInstance(): EarthquakeRangeFragment {
            val fragment = EarthquakeRangeFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_range_fragment, container, false)

        var earthquakeListView = view.findViewById<RecyclerView>(R.id.earthquakeListView)

        val earthquakes = ArrayList<Earthquake>()
        val adapter = EarthquakeAdapter(context!!, earthquakes)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        earthquakeListView.layoutManager = layoutManager
        earthquakeListView.adapter = adapter

        earthquakeListView.setOnClickListener {
            Toast.makeText(activity, "HERE", Toast.LENGTH_LONG).show()
        }

        val earthquakeObserver = Observer<List<Earthquake>> { earthquake ->
            val earthquakeData =  ArrayList<Earthquake>(earthquake)
            adapter.addAll(earthquakeData)
        }

        val endDate: String = QueryPreferences.getEndDate(context!!)
        val startDate: String = QueryPreferences.getStartDate(context!!)
        val minMag: Int = QueryPreferences.getMinMag(context!!)
        val maxMag: Int = QueryPreferences.getMaxMag(context!!)

        val factory: EarthquakeViewModel.Factory = EarthquakeViewModel.Factory(activity!!.application, endDate, startDate, minMag, maxMag)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeViewModel::class.java)
        viewModel.getEarthquakes()?.observe(this, earthquakeObserver)
        //viewModel.loadEarthquakes(url)

        viewModel.loadEarthquakes(endDate, startDate, minMag, maxMag)
        return view
    }

    fun loadEarthquakes(startDate: String, endDate: String, minMag: Int, maxMag: Int) {
        viewModel.loadEarthquakes(endDate, startDate, minMag, maxMag)
    }

}