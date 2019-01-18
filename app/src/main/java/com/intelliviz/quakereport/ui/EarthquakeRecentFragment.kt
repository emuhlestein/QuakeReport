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
import com.intelliviz.quakereport.EarthquakeRecentViewModel
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.R
import com.intelliviz.quakereport.db.Earthquake
import kotlinx.android.synthetic.main.earthquake_range_fragment.*
import java.util.*

class EarthquakeRecentFragment: Fragment() {
    private lateinit var viewModel: EarthquakeRecentViewModel

    companion object {
        fun newInstance(): EarthquakeRecentFragment {
            val fragment = EarthquakeRecentFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_recent_fragment, container, false)

        val earthquakeListView = view.findViewById<RecyclerView>(R.id.earthquakeListView)

        val earthquakes = ArrayList<Earthquake>()
        val adapter = EarthquakeAdapter(context!!, earthquakes)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        earthquakeListView.layoutManager = layoutManager
        earthquakeListView.adapter = adapter

        earthquakeListView.setOnClickListener {
            Toast.makeText(activity, "HERE", Toast.LENGTH_LONG).show()
        }

        val earthquakeObserver = Observer<List<Earthquake>> { earthquakes ->
            val earthquakeData =  ArrayList<Earthquake>(earthquakes)
            if(earthquakeData.isEmpty()) {
                earthquakeListView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                earthquakeListView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
            adapter.addAll(earthquakeData)
        }

        val numDays: Int = QueryPreferences.getNumDays(context!!)
        val minMag: Int = QueryPreferences.getMinMag(context!!)
        val maxMag: Int = QueryPreferences.getMaxMag(context!!)

        val factory: EarthquakeRecentViewModel.Factory = EarthquakeRecentViewModel.Factory(activity!!.application, numDays, minMag, maxMag)
        viewModel = ViewModelProviders.of(this, factory).get(EarthquakeRecentViewModel::class.java)
        viewModel.getEarthquakes()?.observe(viewLifecycleOwner, earthquakeObserver)
        //viewModel.loadEarthquakes(url)

        viewModel.loadEarthquakes(numDays, minMag, maxMag)
        return view
    }

    fun loadEarthquakes(numDays: Int, minMag: Int, maxMag: Int) {
        viewModel.loadEarthquakes(numDays, minMag, maxMag)
    }
}